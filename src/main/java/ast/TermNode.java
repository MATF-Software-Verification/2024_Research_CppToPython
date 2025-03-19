package ast;

public class TermNode extends ASTNode {

    private String type;
    private ASTNode expression;

    public TermNode(String type) {
        this.type = type;
        this.expression = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public ASTNode getExpression() {
        return expression;
    }
    public void setExpression(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TermNode [type= ");
        sb.append(type);
        if (expression != null) {
            sb.append(", expression= ");
            sb.append(expression);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(" ");
        if (expression != null) {
            sb.append(expression.toPython(indent));
        }
        return sb.toString();
    }
}
