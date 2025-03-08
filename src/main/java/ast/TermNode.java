package ast;

public class TermNode extends ASTNode {

    private String type;

    public TermNode(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TermNode [type= ");
        sb.append(type);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        return type;
    }
}
