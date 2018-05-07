package parallelBOAT.tree;

// Abstract node class (can be of type internal or leaf)
public abstract class Node {
    protected Node leftChild;
    protected Node rightChild;

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    Node(){
        this.leftChild = null;
        this.rightChild = null;
    }
}
