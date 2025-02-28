package ast;

import java.util.ArrayList;
import java.util.List;

public class CompoundNode extends ASTNode{

    private List<ASTNode> statements;

    public CompoundNode() {
        this.statements = new ArrayList<ASTNode>();
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public void setStatements(List<ASTNode> statements) {
        this.statements = statements;
    }

    public void add(ASTNode statement) {
        statements.add(statement);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("CompoundNode{");
        sb.append("statements=").append(statements);
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        return "";
    }
}
