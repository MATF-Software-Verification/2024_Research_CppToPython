package ast;


import java.util.ArrayList;

public class SelectionNode extends ASTNode {

    private String type; // "if", "else if", "else", or "switch"
    private ExpressionNode condition; // the condition for "if" or "else if"
    private ASTNode thenBranch; // the body of "if" or "else if"
    private ASTNode elseBranch; // the "else" branch or the next "else if"
    private ArrayList<CaseNode> cases; // for "switch", a list of cases, null for "if(s)"

    public SelectionNode(String type, ExpressionNode condition, ASTNode thenBranch) {
        this.type = type;
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.cases = new ArrayList<>();
    }

    public SelectionNode(String type, ExpressionNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this(type, condition, thenBranch);
        this.elseBranch = elseBranch;
    }

    public SelectionNode(String type, ArrayList<CaseNode> cases) { // Constructor for "switch"
        this.type = type;
        this.cases = cases;
    }

    public String getType() {
        return type;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public ASTNode getThenBranch() {
        return thenBranch;
    }

    public ASTNode getElseBranch() {
        return elseBranch;
    }

    public ArrayList<CaseNode> getCases() {
        return cases;
    }

    public void addCase(CaseNode caseNode) {
        this.cases.add(caseNode);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (condition != null) {
            sb.append(" (").append(condition).append(")");
        }
        sb.append(" { ").append(thenBranch).append(" }");
        if (elseBranch != null) {
            sb.append(" else { ").append(elseBranch).append(" }");
        }
        if (cases != null && !cases.isEmpty()) {
            sb.append(" cases: ").append(cases);
        }
        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        return "";
    }
}