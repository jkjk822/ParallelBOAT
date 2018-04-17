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

public class BootStrap {

    private static ImpurityFunction imp;

    //TODO: turn into actual class (with data as field?)

    public static Node generateBOATTree(Article[] data, int width, int depth){
        Node[] trees = new Node[width];
        for(int i = 0; i < width; i++)
            trees[i] = DecisionTree.generateDecisionTree(sample(data, depth));
        Node tree = refine(data, combineOrPrune(trees));
        return tree;
    }

    public static Article[] sample(Article[] data, int size){
        Article[] sample = new Article[size];
        for(int i = 0; i < size; i++){
            sample[i] = data[ThreadLocalRandom.current().nextInt(0, data.length)];
        }
        return sample;
    }

    private static Node refine(Article[] data, Node n){
        if(n instanceof LeafNode)
            return n;
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        if(n instanceof ConfidenceNode) {
            n = refine(data, (ConfidenceNode) n, left, right);
        }
        else {
            InternalNode iNode = (InternalNode) n;
            DecisionTree.splitData(data, left, right, iNode.getSplitAttribute(), iNode.getSplitPoint());
        }
        n.setLeftChild(refine(left.toArray(new Article[0]), n.getLeftChild()));
        n.setRightChild(refine(right.toArray(new Article[0]), n.getRightChild()));
        return n;
    }

    private static Node refine(Article data[], ConfidenceNode n, ArrayList<Article> left, ArrayList<Article> right){
        ArrayList<Article> mid = new ArrayList<>();
        Attribute att = n.getSplitAttribute();
        DecisionTree.splitData(data, left, right, mid, att, n.getSplitPoint(), n.getSplitConfidence());
        InternalNode exact = new InternalNode(
                att,
                getExactSplitPoint(data, att,
                        mid.stream()
                        .mapToDouble(article -> DecisionTree.getDouble(article.getData()[att.getIndex()]))
                        .toArray())
        );
        DecisionTree.splitData(mid.toArray(new Article[0]),left, right, att, exact.getSplitPoint());
        return exact;
    }

    private static double getExactSplitPoint(Article[] data, Attribute attribute, double[] range){
        return DecisionTree.bestSplitBootStrap(data, attribute, range)[1];
    }

    private static Node combineOrPrune(Node[] trees){
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

    private static InternalNode combine(InternalNode[] trees){
        if(trees[0].getSplitPoint() == Double.NaN) //boolean split
            return new InternalNode(trees[0]);
        double[] splitPoints = Arrays.stream(trees).mapToDouble(InternalNode::getSplitPoint).toArray();
        double confLevel = 1.96; //95% confidence
        return new ConfidenceNode(trees[0], computeConfidence(splitPoints, confLevel));
    }

    private static double computeConfidence(double[] values, double conf){
        double mean = Arrays.stream(values).sum() / values.length;
        double std = Math.sqrt(
                Arrays.stream(values)
                .map(x -> (x - mean)*(x - mean))
                .sum()
                / values.length);
        return conf*std/Math.sqrt(values.length);
    }

    private static <T extends Node> T[] castTo(Class<T> clazz, Node[] array){
        T[] castedArr = (T[]) Array.newInstance(clazz, array.length);
        for(int i = 0; i < array.length; i++){
            castedArr[i] = (T) array[i];
        }
        return castedArr;
    }
}
