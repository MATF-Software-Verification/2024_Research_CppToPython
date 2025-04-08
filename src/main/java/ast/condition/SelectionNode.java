package ast.condition;


import ast.ASTNode;
import ast.CaseNode;
import ast.CompoundNode;
import ast.ExpressionNode;

import java.util.ArrayList;

public class SelectionNode extends ASTNode {

    private String type; // "if", "else if", "else", or "switch"
    private ExpressionNode condition; // the condition for "if" or "else if"
    private ASTNode thenBranch; // the body of "if" or "else if"
    private ASTNode elseBranch; // the "else" branch or the next "else if"
    private ArrayList<CaseNode> cases; // for "switch", a list of cases, null for "if(s)"


    public SelectionNode(){}
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

    public void setElseBranch(ASTNode elseBranch) {
        this.elseBranch = elseBranch;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) { this.condition = condition; }
    public ASTNode getThenBranch() {
        return thenBranch;
    }
    public void setThenBranch(ASTNode thenBranch) {this.thenBranch = thenBranch;}

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
        sb.append("SelectionNode[ type = ");
        sb.append(type);
        if (condition != null) {
            sb.append(" Condition = [").append(condition).append("]");
        }
        sb.append(" THEN[ ").append(thenBranch).append(" ]");
        if (elseBranch != null) {
            sb.append(" ELSE [ ").append(elseBranch).append(" ]");
        }
        if (cases != null && !cases.isEmpty()) {
            sb.append(" cases: ").append(cases);
        }
        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder line = new StringBuilder();

        if(type != null && type.equals("if") && condition != null && thenBranch != null ) {
            line.append(this.type).append(" ");
            line.append(this.condition.toPython(indent)).append(":");
            sb.append(line.toString()).append('\n');
            if(thenBranch instanceof CompoundNode) {
                sb.append(this.thenBranch.toPython(indent+1));
            }else{
                sb.append(getIndentedPythonCode(indent+1,this.thenBranch.toPython(indent)));
            }

            if (elseBranch != null) {
                sb.append(getIndentedPythonCode(indent, this.elseBranch.toPython(indent)));
            }
        }else if(type != null && type.equals("elseif") && thenBranch != null && elseBranch != null && condition != null) {
            line.append("elif ");
            line.append(this.condition.toPython(indent)).append(":");
            sb.append(line.toString()).append('\n');
            sb.append(this.thenBranch.toPython(indent+1));
            if (elseBranch != null) {
                sb.append(getIndentedPythonCode(indent, this.elseBranch.toPython(indent)));
            }
        }
        if(type!= null && type.equals("else") && thenBranch != null){
            line.append(this.type);
            line.append(":");
            sb.append(line.toString()).append('\n');
            sb.append(this.thenBranch.toPython(indent+1));
        }

        return sb.toString();
    }
}