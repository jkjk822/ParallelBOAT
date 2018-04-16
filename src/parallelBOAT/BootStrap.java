package parallelBOAT;

import parallelBOAT.tree.ConfidenceNode;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class BootStrap {

    private static ImpurityFunction imp;

    public static Article[] sample(Article[] dataset, int size){
        Article[] sample = new Article[size];
        for(int i = 0; i < size; i++){
            sample[i] = dataset[ThreadLocalRandom.current().nextInt(0, dataset.length)];
        }
        return sample;
    }

    public static Node buildBootStrapTree(Article[] dataset, int width, int depth){
        Node[] trees = new Node[width];
        for(int i = 0; i < width; i++)
            trees[i] = generateDecisionTree(sample(dataset, depth));
        return refine(combineOrPrune(trees));
    }

    private static Node refine(Node root){
        if(root instanceof ConfidenceNode)
            root = getExactSplit((ConfidenceNode) root);
        root.setLeftChild(refine(root.getLeftChild()));
        root.setRightChild(refine(root.getRightChild()));
        return root;
    }

    private static InternalNode getExactSplit(ConfidenceNode n){
//        InternalNode
//
//        return
    }

    private static Node combineOrPrune(Node[] trees){
        if(Arrays.stream(trees).allMatch(Predicate.isEqual(trees[0]))){
            if(trees[0] instanceof LeafNode)
                return new LeafNode((LeafNode) trees[0]);
            InternalNode n = combine((InternalNode[])trees);
            n.setLeftChild(combineOrPrune(Arrays.stream(trees).map(Node::getLeftChild).toArray(Node[]::new)));
            n.setRightChild(combineOrPrune(Arrays.stream(trees).map(Node::getRightChild).toArray(Node[]::new)));
            return n;
        }
        else{
            return null;
        }
    }

    private static InternalNode combine(InternalNode[] trees){
        if(trees[0].getSplitPoint() instanceof Boolean)
            return new InternalNode<Boolean>(trees[0]);
        double[] splitPoints = Arrays.stream(trees).mapToDouble(n -> (double) n.getSplitPoint()).toArray();
        double confLevel = 1.96; //95% confidence
        return new ConfidenceNode(trees[0], computeConfidence(splitPoints, confLevel));
    }

    private static double computeConfidence(double[] data, double conf){
        double mean = Arrays.stream(data).sum()/data.length;
        double std = Math.sqrt(Arrays.stream(data).map(x->(x-mean)*(x-mean)).sum()/data.length);
        return conf*std/Math.sqrt(data.length);
    }
}
