package ast;

public class LiteralNode extends ASTNode {

    private String value;


    public LiteralNode(String value) {
        this.value = value;
    }
    public LiteralNode() {}


    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LiteralNode{ " + value + " }";
    }

    @Override
    public String toPython(int indent) {
        return value;
    }
}
