package parallelBOAT.tree;

import parallelBOAT.Popularity;

public class LeafNode extends Node {

    private Popularity classLabel;

    public LeafNode (Popularity p) {
        classLabel = p;
    }

    public LeafNode(LeafNode n){
        classLabel = n.classLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeafNode leafNode = (LeafNode) o;
        return classLabel == leafNode.classLabel;
    }
}
