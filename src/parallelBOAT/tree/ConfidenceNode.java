package parallelBOAT.tree;

public class ConfidenceNode extends InternalNode {

    protected double splitConfidence;

    public ConfidenceNode(InternalNode n, double splitPoint, double splitConfidence) {
        super(n);
        this.splitPoint = splitPoint;
        this.splitConfidence = splitConfidence;
    }

    public double getSplitConfidence() {
        return splitConfidence;
    }

    public void setSplitConfidence(double splitConfidence) {
        this.splitConfidence = splitConfidence;
    }
}
