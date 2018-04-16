package parallelBOAT.tree;

public class ConfidenceNode extends InternalNode {

    protected double splitConfidence;

    public ConfidenceNode(InternalNode n, double splitConfidence) {
        super(n);
        this.splitConfidence = splitConfidence;
    }
}
