package parallelBOAT.tree;

import parallelBOAT.Article;
import parallelBOAT.Attribute;
import parallelBOAT.ImpurityFunction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
    public Node generateDecisionTree(Article[] data){
        Node[] trees = new Node[width];
        for(int i = 0; i < width; i++){
            final int index = i;
            pool.execute(() -> trees[index] = super.generateDecisionTree(sample(data, depth)));
        }
        pool.awaitQuiescence(width*depth, TimeUnit.MILLISECONDS);
        return refineTree(data, combineOrPrune(trees));
    }

    private Article[] sample(Article[] data, int size){
        Article[] sample = new Article[size];
        for(int i = 0; i < size; i++){
            sample[i] = data[ThreadLocalRandom.current().nextInt(0, data.length)];
        }
        return sample;
    }

    private Node refineTree(Article[] data, Node n){
        if(n == null || n instanceof LeafNode)
            return n;
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        if(n instanceof ConfidenceNode) {
            n = refineNode(data, (ConfidenceNode) n, left, right);
        }
        else {
            InternalNode iNode = (InternalNode) n;
            splitData(data, left, right, iNode.getSplitAttribute(), iNode.getSplitPoint());
        }
        n.setLeftChild(refineTree(left.toArray(new Article[0]), n.getLeftChild()));
        n.setRightChild(refineTree(right.toArray(new Article[0]), n.getRightChild()));
        return n;
    }

    private Node refineNode(Article data[], ConfidenceNode n, ArrayList<Article> left, ArrayList<Article> right){
        ArrayList<Article> mid = new ArrayList<>();
        Attribute att = n.getSplitAttribute();
        splitData(data, left, right, mid, att, n.getSplitPoint(), n.getSplitConfidence());
        InternalNode exact = new InternalNode(
                att,
                getExactSplit(data, att,
                        mid.stream()
                        .mapToDouble(article -> getDouble(article.getData()[att.getIndex()]))
                        .toArray())
        );
        splitData(mid.toArray(new Article[0]),left, right, att, exact.getSplitPoint());
        exact.setLeftChild(n.getLeftChild());
        exact.setRightChild(n.getRightChild());
        return exact;
    }

    protected void splitData(Article[] data, ArrayList<Article> left, ArrayList<Article> right, ArrayList<Article> middle, Attribute attribute, double splitPoint, double conf) {
        if (Double.isNaN(splitPoint)) throw new RuntimeException("No confidence interval for booleans");
        for (Article a : data) {
            if (direction(a, attribute,  splitPoint - conf) == LEFT)
                left.add(a);
            else if (direction(a, attribute,  splitPoint + conf) == RIGHT)
                right.add(a);
            else
                middle.add(a);
        }
    }

    private double getExactSplit(Article[] data, Attribute attribute, double[] range){
        // Sort data by attribute value
        Arrays.sort(range);

        ArrayList<ForkJoinTask<double[]>> bests = new ArrayList<>(range.length);
        // Check each split point for best
        for (double split : range) {
            bests.add(pool.submit(() -> {
                double bestSplit = Double.NaN;
                double bestImp = Double.POSITIVE_INFINITY;
                ArrayList<Article> left = new ArrayList<>();
                ArrayList<Article> right = new ArrayList<>();
                splitData(data, left, right, attribute, split);
                double currentImp = imp.computeImpurity(left.toArray(new Article[0]), right.toArray(new Article[0]));
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

    private Node combineOrPrune(Node[] trees){
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
