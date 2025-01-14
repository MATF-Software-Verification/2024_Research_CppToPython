package ast;

import java.util.List;

public class DeclaratorNode extends ASTNode{

    private String declaratorId;
    private List<ASTNode> parameters;

    public DeclaratorNode() {
    }

    public String getDeclaratorId() {
        return declaratorId;
    }

    public void setDeclaratorId(String declaratorId) {
        this.declaratorId = declaratorId;
    }

    public List<ASTNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<ASTNode> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "DeclaratorNode{ " + declaratorId + " : " + parameters + " }";
    }
}
