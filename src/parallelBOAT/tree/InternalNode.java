package parallelBOAT.tree;

import parallelBOAT.Attribute;

import java.util.Objects;

public class InternalNode<T> extends Node{

    protected Attribute splitAttribute;
    protected T splitPoint;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalNode<?> that = (InternalNode<?>) o;
        if (splitAttribute != that.splitAttribute) return false;
        if(this.splitPoint instanceof Boolean)
            return Objects.equals(splitPoint, that.splitPoint);
        return true;
    }
}
