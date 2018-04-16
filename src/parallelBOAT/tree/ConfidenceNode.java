package parallelBOAT.tree;

public class ConfidenceNode extends InternalNode<Number> {

    protected double splitConfidence;

    public ConfidenceNode(InternalNode<Number> n, double splitConfidence) {
        super(n);
        this.splitConfidence = splitConfidence;
    }
}
