package parallelBOAT.tree;

import parallelBOAT.Article;
import parallelBOAT.Attribute;
import parallelBOAT.CompareByAttribute;
import parallelBOAT.ImpurityFunction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class BootStrapTreeBuilder extends DecisionTreeBuilder {

    protected int width, depth;
    protected ForkJoinPool pool = new ForkJoinPool();

    public BootStrapTreeBuilder(Article[] data, ImpurityFunction imp, int width, int depth){
        super(data, imp);
        this.width = width;
        this.depth = depth;
    }

    @Override
    protected Node generateDecisionTree(Article[] data) {
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attribute.values()));
        attributes.remove(attributes.size()-1);
        attributes.remove(1);
        attributes.remove(0);
        return generateBootStrapDecisionTree(data, attributes);
    }

    private Node generateBootStrapDecisionTree(Article[] data, ArrayList<Attribute> attributes) {
        if(data.length < 2000)
            return generateDecisionTree(data, new ArrayList<>(attributes), 0);

        Node[] trees = new Node[width];
        for(int i = 0; i < width; i++){
            final int index = i;
            pool.execute(() -> trees[index] = generateDecisionTree(sample(data, depth), new ArrayList<>(attributes), 0));
        }
        pool.awaitQuiescence(width*depth*depth, TimeUnit.SECONDS);
        System.out.println(trees[0]);
        Node partialTree = combineOrPrune(trees);
        partialTree = refineTree(data, partialTree, new ArrayList<>(attributes));
        partialTree = perfect(data, partialTree, new ArrayList<>(attributes));
        return partialTree;
    }

    private Article[] sample(Article[] data, int size){
        Article[] sample = new Article[size];
        for(int i = 0; i < size; i++){
            sample[i] = data[ThreadLocalRandom.current().nextInt(0, data.length)];
        }
        return sample;
    }

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
        for(Attribute a : attributes){ //TODO: parallel stream
            if(data[0].getData()[a.getIndex()] instanceof Boolean) {
                if (bestSplitBool(data, a)[0] < estImp)
                    return null;
            }
            else {
                for (double split : getBuckets(data, a, estImp))
                    if (impurityOfSplit(data, a, split) < estImp)
                        return null;
                }
        }
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

    private Node perfect(Article[] data, Node n, ArrayList<Attribute> attributes){
        while(n == null)
            n = generateBootStrapDecisionTree(data,  attributes);
        if(n instanceof LeafNode)
            return n;
        InternalNode node = (InternalNode) n;
        attributes.remove(node.getSplitAttribute());
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        splitData(data, left, right, node.getSplitAttribute(), node.getSplitPoint());
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
        // Sort data by attribute value
        Arrays.sort(range);

        ArrayList<ForkJoinTask<double[]>> bests = new ArrayList<>(range.length);
        // Check each split point for best
        for (double split : range) {
            //TODO: see if sorting data could vastly improve this
            //TODO: parallel stream?
            bests.add(pool.submit(() -> {
                double bestSplit = Double.NaN;
                double bestImp = Double.POSITIVE_INFINITY;
                double currentImp = impurityOfSplit(data, attribute, split);
                if (currentImp < bestImp) {
                    bestImp = currentImp;
                    bestSplit = split;
                }
                return new double[]{bestImp, bestSplit};
            }));
        }

        // Return best split point
        double bestSplit = Double.NaN;
        double bestImp = Double.POSITIVE_INFINITY;
        for( ForkJoinTask<double[]> task : bests){
            try{
                double currentImp = task.get()[0];
                if (currentImp < bestImp) {
                    bestImp = currentImp;
                    bestSplit = task.get()[1];
                }
            } catch(Exception e){
                e.printStackTrace();
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

    private Node combineOrPrune(Node[] trees){
        //TODO: make this majority vote
        if(trees[0] == null || !Arrays.stream(trees).allMatch(Predicate.isEqual(trees[0]))) {
            return null;
        }
        if(trees[0] instanceof LeafNode)
            return new LeafNode((LeafNode) trees[0]);
        InternalNode n = combine(castTo(InternalNode.class, trees));
        n.setLeftChild(combineOrPrune(Arrays.stream(trees).map(Node::getLeftChild).toArray(Node[]::new)));
        n.setRightChild(combineOrPrune(Arrays.stream(trees).map(Node::getRightChild).toArray(Node[]::new)));
        return n;
    }

    private InternalNode combine(InternalNode[] trees){
        if(Double.isNaN(trees[0].getSplitPoint())) //boolean split
            return new InternalNode(trees[0]);
        double[] splitPoints = Arrays.stream(trees).mapToDouble(InternalNode::getSplitPoint).toArray();
        double confLevel = 1.96; //95% confidence
        double mean = Arrays.stream(splitPoints).sum() / splitPoints.length;
        return new ConfidenceNode(trees[0], mean, computeConfidence(splitPoints, mean, confLevel));
    }

    private double computeConfidence(double[] values, double mean, double conf){
        double std = Math.sqrt(
                Arrays.stream(values)
                .map(x -> (x - mean)*(x - mean))
                .sum()
                / values.length);
        return conf*std/Math.sqrt(values.length);
    }

    private <T extends Node> T[] castTo(Class<T> clazz, Node[] array){
        T[] castedArr = (T[]) Array.newInstance(clazz, array.length);
        for(int i = 0; i < array.length; i++){
            castedArr[i] = (T) array[i];
        }
        return castedArr;
    }
}
