package parallelBOAT.tree;

import parallelBOAT.Popularity;

// Leaf node class  ---->  These have no children, just a popularity class label
public class LeafNode extends Node {

    private Popularity classLabel;

    public LeafNode (Popularity p) {
        classLabel = p;
    }

    public LeafNode(LeafNode n){
        classLabel = n.classLabel;
    }

    @Override
    public Node getLeftChild() {
        return null;
    }

    @Override
    public Node getRightChild() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeafNode leafNode = (LeafNode) o;
        return classLabel == leafNode.classLabel;
    }

    public Popularity getClassLabel() {
        return this.classLabel;
    }
}
