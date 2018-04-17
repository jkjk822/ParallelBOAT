package parallelBOAT;

import javafx.util.Pair;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.util.*;
import java.util.function.Predicate;

public class DecisionTree {

    public static Node generateDecisionTree(Article[] data) {
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attribute.values()));
        attributes.remove(attributes.size()-1);
        attributes.remove(1);
        attributes.remove(0);
        return generateDecisionTree(data, new ArrayList<>(attributes),0);
    }

    public static Node generateDecisionTree(Article[] data, ArrayList<Attribute> attributes, int level) {
//        System.out.println("LEVEL : " + level);
        // If all articles are same class -> return leaf node
        if(getClass(data) != Popularity.MULTI) {
            return new LeafNode(getClass(data));
        }

        // If all attributes have been used -> return leaf node with majority class
        if(attributes.isEmpty()) {
            return new LeafNode(getMajorityClass(data));
        }

        // Find best attribute-split point and split data
        Pair<Attribute, Double> bestSplit = chooseBestAttribute(data, attributes);

        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        splitData(left, right, data, bestSplit.getKey(), bestSplit.getValue());
        attributes.remove(bestSplit.getKey());
        ArrayList<Attribute> leftAttributes = new ArrayList<>(attributes);
        ArrayList<Attribute> rightAttributes = new ArrayList<>(attributes);

        // Build internal Node
        InternalNode node = new InternalNode(bestSplit.getKey(), bestSplit.getValue());

        if(left.isEmpty())
            node.setLeftChild(new LeafNode(getMajorityClass(data)));
        else
            node.setLeftChild(generateDecisionTree(left.toArray(new Article[0]), leftAttributes, level + 1));

        if(right.isEmpty())
            node.setLeftChild(new LeafNode(getMajorityClass(data)));
        else
            node.setLeftChild(generateDecisionTree(right.toArray(new Article[0]), rightAttributes, level + 1));

        return node;
    }

    private static Popularity getClass(Article [] data) {
        Popularity p = data[0].getPopularity();
        if(Arrays.stream(data).map(Article::getPopularity).allMatch(Predicate.isEqual(p)))
            return p;
        else
            return Popularity.MULTI;
    }

    private static Popularity getMajorityClass(Article [] data) {

        HashMap<Popularity, Integer> count = new HashMap<>();

        for (Article a : data) {
            count.putIfAbsent(a.getPopularity(), 0);
            count.put(a.getPopularity(), count.get(a.getPopularity()) + 1);
        }

        return Collections.max(count.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

    }

    private static void splitData(ArrayList<Article> left, ArrayList<Article> right, Article[] data, Attribute attribute, Double splitPoint) {
        Boolean isBool = data[0].getData()[attribute.getIndex()] instanceof Boolean;
        for(Article a : data) {
            if(isBool) {
                if((boolean)a.getData()[attribute.getIndex()])
                    right.add(a);
                else
                    left.add(a);
            } else {
                if(getDouble(a.getData()[attribute.getIndex()]) > splitPoint)
                    right.add(a);
                else
                    left.add(a);
            }
        }
    }

    private static Pair<Attribute, Double> chooseBestAttribute(Article[] data, ArrayList<Attribute> attributes) {
        Attribute bestAttribute = attributes.get(0);
        double bestSplit = Double.NaN;
        double bestGini = Double.POSITIVE_INFINITY;

        for(Attribute a : attributes) {
            Pair<Double, Double> result = bestGiniSplit(data, a);
            if(result.getKey() > bestGini) {
                bestAttribute = a;
                bestSplit = result.getValue();
                bestGini = result.getKey();
            }
        }

        return new Pair<>(bestAttribute, bestSplit);
    }

    private static Pair<Double, Double> bestGiniSplit(Article [] data, Attribute attribute) {
        // If boolean we already know best split point
        if(data[0].getData()[attribute.getIndex()] instanceof Boolean) {
            return bestGiniSplitBool(data, attribute);
        }
        return bestGiniSplitDouble(data, attribute);
    }

    private static Pair<Double, Double> bestGiniSplitBool(Article[] data, Attribute attribute){
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        splitData(left, right, data, attribute, 0.0);
        return new Pair<>(Driver.imp.computeImpurity(left.toArray(new Article[0]), right.toArray(new Article[0])), 0.0);
    }

    static Pair<Double, Double> bestGiniSplitDouble(Article[] data, Attribute attribute){
        // Sort data by attribute value
        Arrays.sort(data, new CompareByAttribute(attribute));
        double bestSplit = Double.NaN;
        double bestGini = Double.POSITIVE_INFINITY;

        // Check each split point for best
        for(int i = 1; i < data.length; i++) {
            Article[] left = Arrays.copyOfRange(data, 0, i);
            Article[] right = Arrays.copyOfRange(data, i, data.length);
            double currentGini = Driver.imp.computeImpurity(left, right);
            if(currentGini < bestGini) {
                bestGini = currentGini;
                bestSplit = (getDouble(data[i-1].getData()[attribute.getIndex()])
                        + getDouble(data[i-1].getData()[attribute.getIndex()])) / 2;
            }
        }

        // Return best split point
        return new Pair<>(bestGini, bestSplit);
    }

    static double getDouble(Object n){
        return ((Number) n).doubleValue();
    }

}
