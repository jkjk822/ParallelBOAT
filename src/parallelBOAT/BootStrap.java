package parallelBOAT;

import javafx.util.Pair;
import parallelBOAT.tree.ConfidenceNode;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class BootStrap {

    private static ImpurityFunction imp;

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

    private static Node refine(Article[] data, Node root){
        if(root==null)
            return null;
        if(root instanceof ConfidenceNode)
            root = getExactSplit(data, (ConfidenceNode) root);
        root.setLeftChild(refine(data, root.getLeftChild()));
        root.setRightChild(refine(data, root.getRightChild()));
        return root;
    }

    private static InternalNode getExactSplit(Article[] data, ConfidenceNode n){
        double low = n.getSplitPoint()-n.getSplitConfidence();
        double high = n.getSplitPoint()+n.getSplitConfidence();
        List<Article> articles = new ArrayList<>();
        for(Article article : data) {
            double val = DecisionTree.getDouble(article.getData()[n.getSplitAttribute().getIndex()]);
            if (low <= val && val <= high) {
                articles.add(article);
            }
        }
        Pair<Double, Double> p = DecisionTree.bestGiniSplitDouble(articles.toArray(new Article[0]), n.getSplitAttribute());
        return new InternalNode(n.getSplitAttribute(), p.getValue());
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
        double std = Math.sqrt(Arrays.stream(values).map(x -> (x - mean)*(x - mean)).sum() / values.length);
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
