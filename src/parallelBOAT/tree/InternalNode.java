package parallelBOAT.tree;

import parallelBOAT.Attribute;

import java.util.Objects;

public class InternalNode<T> extends Node{

    protected Attribute splitAttribute;
    protected T splitPoint;

    public InternalNode(InternalNode<T> n){
        splitAttribute = n.splitAttribute;
        splitPoint = n.splitPoint;
    }

    public InternalNode(InternalNode<T> n, boolean keepChildren){
        this(n);
        if(keepChildren) {
            leftChild = n.leftChild;
            rightChild = n.rightChild;
        }
    }

    public Attribute getSplitAttribute() {
        return splitAttribute;
    }

    public void setSplitAttribute(Attribute splitAttribute) {
        this.splitAttribute = splitAttribute;
    }

    public T getSplitPoint() {
        return splitPoint;
    }

    public void setSplitPoint(T splitPoint) {
        this.splitPoint = splitPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalNode<?> that = (InternalNode<?>) o;
        if (splitAttribute != that.splitAttribute) return false;
        if (this.splitPoint instanceof Boolean)
            return Objects.equals(splitPoint, that.splitPoint);
        return true;
    }
}
