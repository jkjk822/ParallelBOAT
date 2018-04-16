package parallelBOAT;

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
}
