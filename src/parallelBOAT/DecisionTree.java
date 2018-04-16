package parallelBOAT;

import javafx.util.Pair;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.util.*;
import java.util.function.Predicate;

public class DecisionTree {

    public DecisionTree() {}

    public Node generateDecisionTree(Article[] data, ArrayList<Attribute> attributes) {

        // If all articles are same class -> return leaf node
        if(getClass(data) != Popularity.MULTI) {
            return new LeafNode(getClass(data));
        }

        // If all attributes have been used -> return leaf node with majority class
        if(attributes.isEmpty()) {
            return new LeafNode(getMajorityClass(data));
        }

        // Split data
        Pair<Attribute, Double> bestSplit = chooseBestAttribute(data, attributes);
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        Boolean isBool = data[0].getData()[bestSplit.getKey().getIndex()] instanceof Boolean;
        for(Article a : data) {
            if(isBool) {
                if((boolean)a.getData()[bestSplit.getKey().getIndex()])
                    right.add(a);
                else
                    left.add(a);
            } else {
                if((double) a.getData()[bestSplit.getKey().getIndex()] > bestSplit.getValue())
                    right.add(a);
                else
                    left.add(a);
            }
        }
        attributes.remove(bestSplit.getKey());
        ArrayList<Attribute> leftAttributes = new ArrayList<Attribute>(attributes);
        ArrayList<Attribute> rightAttributes = new ArrayList<Attribute>(attributes);

        // Build internal Node
        InternalNode<Double> node = new InternalNode<>(bestSplit.getKey(), bestSplit.getValue());

        if(left.isEmpty())
            node.setLeftChild(new LeafNode(getMajorityClass(data)));
        else
            node.setLeftChild(generateDecisionTree(left.toArray(new Article[left.size()]), leftAttributes));

        if(right.isEmpty())
            node.setLeftChild(new LeafNode(getMajorityClass(data)));
        else
            node.setLeftChild(generateDecisionTree(right.toArray(new Article[right.size()]), rightAttributes));

        return node;
    }

    private Popularity getClass(Article [] data) {
        Popularity p = data[0].getPopularity();
        if(Arrays.stream(data).map(Article::getPopularity).allMatch(Predicate.isEqual(p)))
            return p;
        else
            return Popularity.MULTI;
    }

    private Popularity getMajorityClass(Article [] data) {

        HashMap<Popularity, Integer> count = new HashMap<Popularity, Integer>();

        for (Article a : data) {
            count.putIfAbsent(a.getPopularity(), 0);
            count.put(a.getPopularity(), count.get(a.getPopularity()) + 1);
        }

        return Collections.max(count.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

    }

    private Pair<Attribute, Double> chooseBestAttribute(Article[] data, ArrayList<Attribute> attributes) {
        return new Pair<>(attributes.get(0), 0.0);
    }

    private Pair<Double, Double> computeGini(Article [] data, Attribute attribute) {
        //sort data by attribute
        //for each splitting point
        //  giniSum
        //  for each class
        //      sum += number in class/total
        //  ginisum += sum
        //  if ginisum < bestsum
        //      bestsum = ginisum
        //      bestsplit = currentsplit
        //  return best

        return null;
    }
}
