package ast;

public class CaseNode extends ASTNode{

    private ExpressionNode condition; // the case condition (null for default)
    private ASTNode body; // the body of the case

    public CaseNode(ExpressionNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (condition != null) {
            sb.append("case ").append(condition).append(": ");
        } else {
            sb.append("default: ");
        }
        sb.append(body);
        return sb.toString();
    }

    @Override
    public String toPython(int ident) {
        return "";
    }
}
