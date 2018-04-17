package parallelBOAT;

import javafx.util.Pair;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.util.*;
import java.util.function.Predicate;

public class DecisionTree {

    public DecisionTree() {}

    public Node generateDecisionTree(Article[] data, ArrayList<Attribute> attributes, int level) {
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
        ArrayList<Attribute> leftAttributes = new ArrayList<Attribute>(attributes);
        ArrayList<Attribute> rightAttributes = new ArrayList<Attribute>(attributes);

        // Build internal Node
        InternalNode node = new InternalNode(bestSplit.getKey(), bestSplit.getValue());

        if(left.isEmpty())
            node.setLeftChild(new LeafNode(getMajorityClass(data)));
        else
            node.setLeftChild(generateDecisionTree(left.toArray(new Article[left.size()]), leftAttributes, level + 1));

        if(right.isEmpty())
            node.setLeftChild(new LeafNode(getMajorityClass(data)));
        else
            node.setLeftChild(generateDecisionTree(right.toArray(new Article[right.size()]), rightAttributes, level + 1));

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

    private void splitData(ArrayList<Article> left, ArrayList<Article> right, Article[] data, Attribute attribute, Double splitPoint) {
        Boolean isBool = data[0].getData()[attribute.getIndex()] instanceof Boolean;
        for(Article a : data) {
            if(isBool) {
                if((boolean)a.getData()[attribute.getIndex()])
                    right.add(a);
                else
                    left.add(a);
            } else {
                if((double) a.getData()[attribute.getIndex()] > splitPoint)
                    right.add(a);
                else
                    left.add(a);
            }
        }
    }

    private Pair<Attribute, Double> chooseBestAttribute(Article[] data, ArrayList<Attribute> attributes) {
        Attribute bestAttribute = attributes.get(0);
        Double bestSplit = 0.0;
        Double bestGini = -1.0;

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

    private Pair<Double, Double> bestGiniSplit(Article [] data, Attribute attribute) {
        // If boolean we already know best split point
        if(data[0].getData()[attribute.getIndex()] instanceof Boolean) {
            ArrayList<Article> left = new ArrayList<>();
            ArrayList<Article> right = new ArrayList<>();
            splitData(left, right, data, attribute, 0.0);
            return new Pair<>(giniIndex(left.toArray(new Article[left.size()]), right.toArray(new Article[left.size()])), 0.0);
        }

        // Sort data by attribute value
        Arrays.sort(data, new CompareByAttribute(attribute));
        Double bestSplit = 0.0;
        Double bestGini = 0.0;

        // Check each split point for best
        for(int i = 0; i < data.length; i++) {
            Article[] left = Arrays.copyOfRange(data, 0, i + 1);
            Article[] right = Arrays.copyOfRange(data, i, data.length);
            Double currentGini = giniIndex(left, right);
            if(currentGini > bestGini) {
                bestGini = currentGini;
                bestSplit = ((Double)data[i].getData()[attribute.getIndex()] + (Double)data[i + 1].getData()[attribute.getIndex()]) / 2;
            }
        }

        // Return best split point
        return new Pair<>(bestGini, bestSplit);
    }

    private Double giniIndex(Article[] left, Article[] right) {
        Double giniLeft = giniIndex(left) * left.length / (left.length + right.length);
        Double giniRight = giniIndex(right) * right.length / (left.length + right.length);
        return giniLeft + giniRight;
    }

    private Double giniIndex(Article[] data) {
        HashMap<Popularity, Integer> count = new HashMap<Popularity, Integer>();

        // Count up occurances of each class
        for (Article a : data) {
            count.putIfAbsent(a.getPopularity(), 0);
            count.put(a.getPopularity(), count.get(a.getPopularity()) + 1);
        }

        // Get each p^2
        Double sum = 0.0;
        Iterator it = count.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            sum += Math.pow((Integer)pair.getValue() / (double)data.length, 2);
        }

        // Return 1 - sum of p^2
        return 1 - sum;

    }
}
