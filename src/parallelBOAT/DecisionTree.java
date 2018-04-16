package parallelBOAT;

import javafx.util.Pair;
import parallelBOAT.tree.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class DecisionTree {

    public DecisionTree() {}

    public Node generateDecisionTree(Article[] data, ArrayList<Attribute> attributes) {
        return null;
    }

    private Popularity getClass(Article [] data) {
        Popularity p = data[0].getPopularity();
        if(Arrays.stream(data).map(Article::getPopularity).allMatch(Predicate.isEqual(p)))
            return p;
        else
            return Popularity.MULTI;
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
