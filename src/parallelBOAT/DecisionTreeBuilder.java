package parallelBOAT;

import javafx.util.Pair;
import parallelBOAT.tree.InternalNode;
import parallelBOAT.tree.LeafNode;
import parallelBOAT.tree.Node;

import java.util.*;
import java.util.function.Predicate;

public class DecisionTreeBuilder {
    protected static final int LEFT = -1;
    protected static final int RIGHT = 1;

    protected Article[] data;
    protected Node tree;
    protected ImpurityFunction imp;

    public DecisionTreeBuilder(){
    }

    public DecisionTreeBuilder(Article[] data, ImpurityFunction imp){
        this.data =  data;
        this.imp = imp;
    }

    public Popularity classify(Node tree, Article article) {
        if(tree instanceof LeafNode) {
            return ((LeafNode) tree).getClassLabel();
        }
        double splitPoint = ((InternalNode) tree).getSplitPoint();
        if(direction(article, ((InternalNode) tree).getSplitAttribute(), splitPoint) == 1) {
            return classify(tree.getRightChild(), article);
        } else {
            return classify(tree.getLeftChild(), article);
        }
    }

    public void generateDecisionTree() {
        tree = generateDecisionTree(data);
    }

    protected Node generateDecisionTree(Article[] data) {
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attribute.values()));
        attributes.remove(attributes.size()-1);
        attributes.remove(1);
        attributes.remove(0);
        return generateDecisionTree(data, new ArrayList<>(attributes),0);
    }

    private Node generateDecisionTree(Article[] data, ArrayList<Attribute> attributes, int level) {
        System.out.println(level);
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

        splitData(data, left, right, bestSplit.getKey(), bestSplit.getValue());
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
            node.setRightChild(new LeafNode(getMajorityClass(data)));
        else
            node.setRightChild(generateDecisionTree(right.toArray(new Article[0]), rightAttributes, level + 1));

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
        //TODO: implement same way as gini?
        HashMap<Popularity, Integer> count = new HashMap<>();

        for (Article a : data) {
            count.putIfAbsent(a.getPopularity(), 0);
            count.put(a.getPopularity(), count.get(a.getPopularity()) + 1);
        }

        return Collections.max(count.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

    }

    protected void splitData(Article[] data, ArrayList<Article> left, ArrayList<Article> right, Attribute attribute, double splitPoint) {
        for (Article a : data) {
            if(direction(a, attribute, splitPoint) == RIGHT)
                right.add(a);
            else
                left.add(a);
        }
    }

    private Pair<Attribute, Double> chooseBestAttribute(Article[] data, ArrayList<Attribute> attributes) {
        Attribute bestAttribute = attributes.get(0);
        double bestSplit = Double.NaN;
        double bestImp = Double.POSITIVE_INFINITY;

        for(Attribute a : attributes) {
            double[] result = bestSplit(data, a);
            if(result[0] < bestImp) {
                bestAttribute = a;
                bestImp = result[0];
                bestSplit = result[1];
            }
        }

        return new Pair<>(bestAttribute, bestSplit);
    }
    //TODO: use arraylists instead of arrays
    protected double[] bestSplit(Article [] data, Attribute attribute) {
        // If boolean we already know best split point
        if(data[0].getData()[attribute.getIndex()] instanceof Boolean) {
            return bestSplitBool(data, attribute);
        }
        return bestSplitDouble(data, attribute);
    }

    private double[] bestSplitBool(Article[] data, Attribute attribute){
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        splitData(data, left, right, attribute, Double.NaN);
        return new double[]{imp.computeImpurity(left.toArray(new Article[0]), right.toArray(new Article[0])), Double.NaN};
    }

    private double[] bestSplitDouble(Article[] data, Attribute attribute){
        // Sort data by attribute value
        Arrays.sort(data, new CompareByAttribute(attribute));
        double bestSplit = Double.NaN;
        double bestImp = Double.POSITIVE_INFINITY;

        // Check each split point for best
        for(int i = 1; i < data.length; i++) {
            Article[] left = Arrays.copyOfRange(data, 0, i);
            Article[] right = Arrays.copyOfRange(data, i, data.length);
            double currentImp = imp.computeImpurity(left, right);
            if(currentImp < bestImp) {
                bestImp = currentImp;
                bestSplit = (getDouble(data[i-1].getData()[attribute.getIndex()])
                        + getDouble(data[i-1].getData()[attribute.getIndex()])) / 2;
            }
        }

        // Return best split point
        return new double[]{bestImp, bestSplit};
    }

    // Classify as left or right for a split point and attribute
    protected int direction(Article article, Attribute attribute, double splitPoint) {
        if(Double.isNaN(splitPoint)) {
            if((boolean)article.getData()[attribute.getIndex()])
                return RIGHT;
            else
                return LEFT;
        } else {
            if (getDouble(article.getData()[attribute.getIndex()]) > splitPoint) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }
    }

    protected double getDouble(Object n){
        return ((Number) n).doubleValue();
    }

}
