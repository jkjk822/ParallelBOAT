package parallelBOAT;

import parallelBOAT.tree.ConfidenceNode;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class BootStrapTreeBuilder extends DecisionTreeBuilder {

    protected int width, depth;

    public BootStrapTreeBuilder(Article[] data, ImpurityFunction imp, int width, int depth){
        super(data, imp);
        this.width = width;
        this.depth = depth;
    }

    @Override
    public Node generateDecisionTree(Article[] data){
        Node[] trees = new Node[width];
        for(int i = 0; i < width; i++)
            trees[i] = super.generateDecisionTree(sample(data, depth));
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
        if(n instanceof LeafNode)
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
        return exact;
    }

    protected void splitData(Article[] data, ArrayList<Article> left, ArrayList<Article> right, ArrayList<Article> middle, Attribute attribute, double splitPoint, double conf) {
        if (Double.isNaN(splitPoint)) throw new RuntimeException("No confidence interval for booleans");
        for (Article a : data) {
            // TODO: You can use the direction() method here too, just feed in (splitPoint - conf) as splitpoint
            double val = getDouble(a.getData()[attribute.getIndex()]);
            if (val < splitPoint - conf)
                left.add(a);
            else if (val > splitPoint + conf)
                right.add(a);
            else
                middle.add(a);
        }
    }

    private double getExactSplit(Article[] data, Attribute attribute, double[] range){
        // Sort data by attribute value
        Arrays.sort(range);
        double bestSplit = Double.NaN;
        double bestImp = Double.POSITIVE_INFINITY;

        // Check each split point for best
        for(double split : range) {
            ArrayList<Article> left = new ArrayList<>();
            ArrayList<Article> right = new ArrayList<>();
            splitData(data, left, right, attribute, split);
            double currentImp = imp.computeImpurity(left.toArray(new Article[0]), right.toArray(new Article[0]));
            if(currentImp < bestImp) {
                bestImp = currentImp;
                bestSplit = split;
            }
        }

        // Return best split point
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
        if(trees[0].getSplitPoint() == Double.NaN) //boolean split
            return new InternalNode(trees[0]);
        double[] splitPoints = Arrays.stream(trees).mapToDouble(InternalNode::getSplitPoint).toArray();
        double confLevel = 1.96; //95% confidence
        return new ConfidenceNode(trees[0], computeConfidence(splitPoints, confLevel));
    }

    private double computeConfidence(double[] values, double conf){
        double mean = Arrays.stream(values).sum() / values.length;
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
