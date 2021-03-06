package parallelBOAT.tree;

import javafx.util.Pair;
import parallelBOAT.*;

import java.util.*;
import java.util.function.Predicate;

public class DecisionTreeBuilder {
    protected static final int LEFT = -1;
    protected static final int RIGHT = 1;

    protected Article[] data;
    protected Node decisionTree;
    protected ImpurityFunction impFunc;

    // Empty Constructor
    public DecisionTreeBuilder(){
    }

    // Data and impurity Constructor
    public DecisionTreeBuilder(Article[] data, ImpurityFunction impFunc){
        this.data =  data;
        this.impFunc = impFunc;
    }

    // Compare method exposed
    public boolean isEqual(Node otherTree) {
        return isEqual(this.decisionTree, otherTree);
    }

    // Compare trees for equivalence
    private boolean isEqual(Node a, Node b) {
        if(a == null && b == null)
            return true;
        else if(a == null)
            return false;
        else if(b == null)
            return false;
        if(a.equals(b))
            return isEqual(a.getLeftChild(), b.getLeftChild()) && isEqual(a.getRightChild(), b.getRightChild());
        return false;
    }

    // Exposed classify method
    public Popularity classify(Article article) {
        return classify(decisionTree, article);
    }

    // Classify an articles popularity using our decision tree
    private Popularity classify(Node tree, Article article) {
        if(tree instanceof LeafNode) {
            return ((LeafNode) tree).getClassLabel();
        }
        double splitPoint = ((InternalNode) tree).getSplitPoint();
        if(direction(article, ((InternalNode) tree).getSplitAttribute(), splitPoint) == RIGHT) {
            return classify(tree.getRightChild(), article);
        } else {
            return classify(tree.getLeftChild(), article);
        }
    }

    // Exposed method, build tree
    public void generateDecisionTree() {
        decisionTree = generateDecisionTree(data);
    }

    // Build tree method, used by BOAT
    protected Node generateDecisionTree(Article[] data) {
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attribute.values()));
        attributes.remove(attributes.size()-1);
        attributes.remove(1);
        attributes.remove(0);
        return generateDecisionTree(data, attributes,0);
    }

    // Basic decision tree building algorithm
    protected Node generateDecisionTree(Article[] data, ArrayList<Attribute> attributes, int level) {
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

        // Build internal Node
        InternalNode node = new InternalNode(bestSplit.getKey(), bestSplit.getValue());
        if(left.isEmpty())
            node.setLeftChild(new LeafNode(getMajorityClass(data)));
        else
            node.setLeftChild(generateDecisionTree(left.toArray(new Article[0]), new ArrayList<>(attributes), level + 1));

        if(right.isEmpty())
            node.setRightChild(new LeafNode(getMajorityClass(data)));
        else
            node.setRightChild(generateDecisionTree(right.toArray(new Article[0]), new ArrayList<>(attributes), level + 1));

        return node;
    }

    // Helper method to return the Popularity class of data (LOW, HIGH, VIRAL, or MULTI if multiple classes are present)
    private Popularity getClass(Article [] data) {
        Popularity p = data[0].getPopularity();
        if(Arrays.stream(data).map(Article::getPopularity).allMatch(Predicate.isEqual(p)))
            return p;
        else
            return Popularity.MULTI;
    }

    // Given a data set, return majority class
    private Popularity getMajorityClass(Article [] data) {
        //TODO: implement same way as gini?
        HashMap<Popularity, Integer> count = new HashMap<>();

        for (Article article : data) {
            count.putIfAbsent(article.getPopularity(), 0);
            count.put(article.getPopularity(), count.get(article.getPopularity()) + 1);
        }

        return Collections.max(count.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

    }

    // Splits data based on attribute and split point
    protected void splitData(Article[] data, ArrayList<Article> left, ArrayList<Article> right, Attribute attribute, double splitPoint) {
        for (Article article : data) {
            if(direction(article, attribute, splitPoint) == RIGHT)
                right.add(article);
            else
                left.add(article);
        }
    }

    // Find best attribute and corresponding split point for dataset and available attributes
    protected Pair<Attribute, Double> chooseBestAttribute(Article[] data, ArrayList<Attribute> attributes) {
        Attribute bestAttribute = attributes.get(0);
        double bestSplit = Double.NaN;
        double bestImp = Double.POSITIVE_INFINITY;

        // Check each available attribute
        for(Attribute a : attributes) {
            double[] result = bestSplit(data, a);
            // if impurity is better (lower) than current best, update
            if(result[0] < bestImp) {
                bestAttribute = a;
                bestImp = result[0];
                bestSplit = result[1];
            }
        }

        return new Pair<>(bestAttribute, bestSplit);
    }

    //TODO: use arraylists instead of arrays
    // Find best split point
    protected double[] bestSplit(Article [] data, Attribute attribute) {
        // If boolean we already know best split point
        if(data[0].getData()[attribute.getIndex()] instanceof Boolean) {
            return bestSplitBool(data, attribute);
        }
        return bestSplitDouble(data, attribute);
    }

    // Find best split point for boolean (used by BOAT)
    protected double[] bestSplitBool(Article[] data, Attribute attribute){
        ArrayList<Article> left = new ArrayList<>();
        ArrayList<Article> right = new ArrayList<>();
        splitData(data, left, right, attribute, Double.NaN);
        return new double[]{impFunc.computeImpurity(left.toArray(new Article[0]), right.toArray(new Article[0])), Double.NaN};
    }

    // Find best split point for double attribute (used by BOAT)
    protected double[] bestSplitDouble(Article[] data, Attribute attribute){
        // Sort data by attribute value
        Arrays.sort(data, new CompareByAttribute(attribute));
        double bestSplit = Double.NaN;
        double bestImp = Double.POSITIVE_INFINITY;

        // Check each split point for best
        for(int i = 1; i < data.length; i++) {
            Article[] left = Arrays.copyOfRange(data, 0, i);
            Article[] right = Arrays.copyOfRange(data, i, data.length);
            double currentImp = impFunc.computeImpurity(left, right);
            if(currentImp < bestImp) {
                bestImp = currentImp;
                bestSplit = (getDouble(data[i-1], attribute)
                        + getDouble(data[i-1], attribute)) / 2;
            }
        }

        // Return best split point
        return new double[]{bestImp, bestSplit};
    }

    // Classify as left or right for a split point and attribute
    protected int direction(Article article, Attribute attribute, double splitPoint) {
        //TODO: Rewrite to return function instead of testing for bool each iteration
        if(Double.isNaN(splitPoint)) {
            if((boolean)article.getData()[attribute.getIndex()])
                return RIGHT;
            else
                return LEFT;
        } else {
            if (getDouble(article, attribute) > splitPoint) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }
    }

    // Return double value of an attribute
    protected double getDouble(Article article, Attribute a){
        return ((Number) article.getData()[a.getIndex()]).doubleValue();
    }

    // Public getter for decision tree
    public Node getDecisionTree() {
        return decisionTree;
    }
}
