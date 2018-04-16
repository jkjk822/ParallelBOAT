package parallelBOAT.tree;

import parallelBOAT.Popularity;

import java.util.Objects;

public class LeafNode extends Node {

    private Popularity classLabel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeafNode leafNode = (LeafNode) o;
        return classLabel == leafNode.classLabel;
    }
}
