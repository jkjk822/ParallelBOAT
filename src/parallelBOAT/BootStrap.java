package parallelBOAT;

import parallelBOAT.tree.BootStrapNode;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class BootStrap {

    public static Article[] sample(Article[] dataset, int size){
        Article[] sample = new Article[size];
        for(int i = 0; i < size; i++){
            sample[i] = dataset[ThreadLocalRandom.current().nextInt(0, dataset.length)];
        }
        return sample;
    }

    public static Node buildBootStrapTree(Node... trees){
        return combineOrPrune(trees);
    }

    private static Node combineOrPrune(Node... trees){
        if(Arrays.stream(trees).allMatch(Predicate.isEqual(trees[0]))){
            if(trees[0] instanceof LeafNode)
                return trees[0];
            Node left = combineOrPrune(Arrays.stream(trees).map(Node::getLeftChild).toArray(Node[]::new));
            Node right = combineOrPrune(Arrays.stream(trees).map(Node::getRightChild).toArray(Node[]::new));
            BootStrapNode n = combine(trees);
            n.setLeftChild(left);
            n.setRightChild(right);
            return n;
        }
        else{
            return null;
        }

    }

    private static BootStrapNode combine(Node... trees){
        //compute confidence interval
        return new BootStrapNode();
    }
}
