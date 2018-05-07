package parallelBOAT.tree;

import parallelBOAT.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;

public class BootStrapTreeBuilder extends DecisionTreeBuilder {

    protected int width, depth;
    protected ForkJoinPool pool = new ForkJoinPool();

    // Initialize the tree builder
    public BootStrapTreeBuilder(Article[] data, ImpurityFunction imp, int width, int depth){
        super(data, imp);
        this.width = width;
        this.depth = depth;
    }

    // Wrapper call which initializes the attribute list
    @Override
    protected Node generateDecisionTree(Article[] data) {
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attribute.values()));
        attributes.remove(attributes.size()-1);
        attributes.remove(1);
        attributes.remove(0);
        return generateBootStrapDecisionTree(data, attributes);
    }

    /*********************************
     * 3.2 Coarse Splitting Criteria
     *********************************/

    // Main call to generate the tree
    // Starts by creating samples using bootstrapping and then using the naive
    // decision tree algorithm to build trees from each
    // These are then combined, refined, and perfected to form the final tree
    private Node generateBootStrapDecisionTree(Article[] data, ArrayList<Attribute> attributes) {
        if(data.length < 2000) //use naive approach if data set is small enough
            return generateDecisionTree(data, new ArrayList<>(attributes), 0);
        Node[] trees = new Node[width];
        for(int i = 0; i < width; i++){
            final int index = i;
            trees[index] = generateDecisionTree(sample(data, depth), new ArrayList<>(attributes), 0);
        }
//        pool.awaitQuiescence(width*depth*depth, TimeUnit.SECONDS);
        Node partialTree = combineOrPrune(trees);
        partialTree = refineTree(data, partialTree, new ArrayList<>(attributes));
        partialTree = perfect(data, partialTree, new ArrayList<>(attributes));
        return partialTree;
    }

    // Sample `size` items from data with replacement
    private Article[] sample(Article[] data, int size){
        Article[] sample = new Article[size];
        for(int i = 0; i < size; i++){
            sample[i] = data[ThreadLocalRandom.current().nextInt(0, data.length)];
        }
        return sample;
    }

    // Combine the trees who's roots are in `trees`
    // Prune trees which do not split on the same
    // attribute as the majority
    private Node combineOrPrune(Node[] trees){
        trees = keepMajority(trees);
        if(trees[0] == null || trees[0] instanceof LeafNode)
            return trees[0];
        InternalNode n = combine(castTo(InternalNode.class, trees));
        n.setLeftChild(combineOrPrune(Arrays.stream(trees).map(Node::getLeftChild).toArray(Node[]::new)));
        n.setRightChild(combineOrPrune(Arrays.stream(trees).map(Node::getRightChild).toArray(Node[]::new)));
        return n;
    }

    // A modification to the BOAT algorithm
    // Instead of requiring all nodes agree,
    // find the most common splitting attribute
    // and only keep nodes which split on it
    private Node[] keepMajority(Node[] trees){
        int atts = Attribute.values().length;
        int pops = Popularity.values().length;
        int[] count =  new int[atts+pops+2];
        for(Node tree : trees){
            if(tree == null)
                count[atts+pops+1]++;
            else if(tree instanceof LeafNode)
                count[atts+((LeafNode)tree).getClassLabel().ordinal()]++;
            else
                count[((InternalNode) tree).getSplitAttribute().getIndex()]++;
        }
        int max = 0;
        int index = -1;
        for(int i = 0; i < count.length; i++){
            if(count[i] > max) {
                max = count[i];
                index = i;
            }
        }
        //Mostly null, or only 1 of everything
        if(index > atts+pops || max == 1)
            return new Node[]{null};
        Node[] newTrees = new Node[max];
        int i = 0;
        for(Node tree : trees){
            if(tree instanceof LeafNode && index == atts+((LeafNode) tree).getClassLabel().ordinal()
                    || tree instanceof InternalNode && index == ((InternalNode) tree).getSplitAttribute().getIndex()){
                newTrees[i] = tree;
                i++;
            }
        }
        return newTrees;
    }

    // Combine a group of internal nodes into a
    // confidence node by creating a confidence
    // interval from their split points
    private InternalNode combine(InternalNode[] trees){
        if(Double.isNaN(trees[0].getSplitPoint())) //boolean split
            return new InternalNode(trees[0]);
        double[] splitPoints = Arrays.stream(trees).mapToDouble(InternalNode::getSplitPoint).toArray();
        double confLevel = 1.96; //95% confidence
        double mean = Arrays.stream(splitPoints).sum() / splitPoints.length;
        return new ConfidenceNode(trees[0], mean, computeConfidence(splitPoints, mean, confLevel));
    }

    // Find the width of the confidence interval
    // Assumes uniform distribution
    private double computeConfidence(double[] values, double mean, double conf){
        double std = Math.sqrt(
                Arrays.stream(values)
                        .map(x -> (x - mean)*(x - mean))
                        .sum()
                        / values.length);
        return conf*std/Math.sqrt(values.length);
    }

    // Cast a Node array to an array of something which subclasses Node
    private <T extends Node> T[] castTo(Class<T> clazz, Node[] array){
        T[] castedArr = (T[]) Array.newInstance(clazz, array.length);
        for(int i = 0; i < array.length; i++){
            castedArr[i] = (T) array[i];
        }
        return castedArr;
    }

    /**********************************************
     * 3.3 From Coarse to Exact Splitting Criteria
     **********************************************/

    // Refine confidence nodes back into internal nodes by finding the best
    // split point in the confidence interval
    // After this is done, the node is checked to make sure the best global
    // splitting attribute and split point have indeed been found. If not,
    // delete this node and it's subtree
    private Node refineTree(Article[] data, Node n, ArrayList<Attribute> attributes){
        if(n == null || n instanceof LeafNode)
            return n;
        InternalNode node = (InternalNode) n;
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        if(node instanceof ConfidenceNode) {
            node = refineNode(data, (ConfidenceNode) node, left, right);
        }
        else {
            splitData(data, left, right, node.getSplitAttribute(), node.getSplitPoint());
        }

        double estImp = impFunc.computeImpurity(left.toArray(new Article[0]), right.toArray(new Article[0]));

        for(Attribute a : attributes) { //TODO: parallel stream
            if (data[0].getData()[a.getIndex()] instanceof Boolean) {
                if (bestSplitBool(data, a)[0] < estImp)
                    return null;
            } else {
                for (double split : getBuckets(data, a, estImp))
                    if (impurityOfSplit(data, a, split) < estImp)
                        return null;
            }
        }

        // Find out if we had any failures
//        ArrayList<Callable<Boolean>> failures = new ArrayList<>(attributes.size());
//        for(Attribute a : attributes){
//            failures.add(() -> {
//                if (data[0].getData()[a.getIndex()] instanceof Boolean) {
//                    if (bestSplitBool(data, a)[0] < estImp)
//                        return true;
//                } else {
//                    for (double split : getBuckets(data, a, estImp))
//                        if (impurityOfSplit(data, a, split) < estImp)
//                            return true;
//                }
//                return false;
//            });
//        }
//        try {
//            if(pool.invokeAny(failures))
//                return null;
//        } catch(Exception e){
//            e.printStackTrace();
//        }
        attributes.remove(node.getSplitAttribute());
        node.setLeftChild(
                perfect(left.toArray(new Article[0]),
                        refineTree(left.toArray(new Article[0]), node.getLeftChild(), new ArrayList<>(attributes)),
                        new ArrayList<>(attributes))
        );
        node.setRightChild(
                perfect(right.toArray(new Article[0]),
                        refineTree(right.toArray(new Article[0]), node.getRightChild(), new ArrayList<>(attributes)),
                        new ArrayList<>(attributes))
        );
        return node;
    }

    private InternalNode refineNode(Article data[], ConfidenceNode n, ArrayList<Article> left, ArrayList<Article> right){
        ArrayList<Article> mid = new ArrayList<>();
        Attribute att = n.getSplitAttribute();
        splitData(data, left, right, mid, att, n.getSplitPoint(), n.getSplitConfidence());
        InternalNode exact = new InternalNode(
                att,
                getExactSplit(data, att,
                        mid.stream()
                                .mapToDouble(article -> getDouble(article, att))
                                .toArray())
        );
        if(mid.isEmpty())
            exact = new InternalNode(att, n.getSplitPoint());
        else
            splitData(mid.toArray(new Article[0]),left, right, att, exact.getSplitPoint());
        exact.setLeftChild(n.getLeftChild());
        exact.setRightChild(n.getRightChild());
        return exact;
    }

    protected void splitData(Article[] data, ArrayList<Article> left, ArrayList<Article> right, ArrayList<Article> middle, Attribute attribute, double splitPoint, double conf) {
        if (Double.isNaN(splitPoint)) throw new RuntimeException("No confidence interval for booleans");
        for (Article article : data) {
            if (direction(article, attribute,  splitPoint - conf) == LEFT)
                left.add(article);
            else if (direction(article, attribute,  splitPoint + conf) == RIGHT)
                right.add(article);
            else
                middle.add(article);
        }
    }

    private double getExactSplit(Article[] data, Attribute attribute, double[] range){
        double bestSplit = Double.NaN;
        double bestImp = Double.POSITIVE_INFINITY;

        // Check each split point for best
        for (double split : range) {
            double currentImp = impurityOfSplit(data, attribute, split);
            if (currentImp < bestImp) {
                bestImp = currentImp;
                bestSplit = split;
            }
        }
        return bestSplit;
    }

    private double impurityOfSplit(Article[] data, Attribute attribute, double splitPoint){
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        splitData(data, left, right, attribute, splitPoint);
        return impFunc.computeImpurity(left.toArray(new Article[0]), right.toArray(new Article[0]));
    }

    /*****************************
     * 3.4 How To Detect Failure
     *****************************/

    private Node perfect(Article[] data, Node n, ArrayList<Attribute> attributes){
        while(n == null)
            n = generateBootStrapDecisionTree(data,  attributes);
        if(n instanceof LeafNode)
            return n;
        InternalNode node = (InternalNode) n;
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        splitData(data, left, right, node.getSplitAttribute(), node.getSplitPoint());
        attributes.remove(node.getSplitAttribute());
        node.setLeftChild(perfect(left.toArray(new Article[0]), node.getLeftChild(), new ArrayList<>(attributes)));
        node.setRightChild(perfect(right.toArray(new Article[0]), node.getRightChild(), new ArrayList<>(attributes)));
        return node;
    }

    private Set<Double> getBuckets(Article[] data, Attribute attribute, double estImp){
        Set<Double> buckets = new HashSet<>();
        Arrays.sort(data, new CompareByAttribute(attribute));
        double tolerance = .01;
        for(int i = 1, j=data.length/100; i < data.length; j++, i+=j) {
            Article[] left = Arrays.copyOfRange(data, 0, i);
            Article[] right = Arrays.copyOfRange(data, i, data.length);
            double currentImp = impFunc.computeImpurity(left, right);
            if(currentImp > estImp+tolerance) {
                j *= 1.5;
                tolerance*=1.2;
                j = j < 0 ? 1+data.length/10: j; //handle overflow
                if(j > data.length/10){
                    j = data.length/10;
                    buckets.add(getDouble(data[i], attribute));
                }
            }
            else {
                j /= 1.5;
                tolerance /= 1.2;
                j = Math.max(data.length/100, j);
                buckets.add(getDouble(data[i], attribute));
            }
        }
        return buckets;
    }
}
