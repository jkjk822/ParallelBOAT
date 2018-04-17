package parallelBOAT.tree;

import parallelBOAT.Attribute;

public class InternalNode extends Node{

    protected Attribute splitAttribute;
    protected double splitPoint = Double.NaN; //NaN means we're doing a boolean split

    public InternalNode(Attribute splitAttribute, double splitPoint) {
        this.splitAttribute = splitAttribute;
        this.splitPoint = splitPoint;
    }

    public InternalNode(InternalNode n){
        splitAttribute = n.splitAttribute;
        splitPoint = n.splitPoint;
    }

    public InternalNode(InternalNode n, boolean keepChildren){
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

    public double getSplitPoint() {
        return splitPoint;
    }

    public void setSplitPoint(double splitPoint) {
        this.splitPoint = splitPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalNode that = (InternalNode) o;
        return splitAttribute == that.splitAttribute;
    }
}
