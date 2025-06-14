package ast;

import java.util.ArrayList;
import java.util.List;

public class DeclaratorNode extends ASTNode{

    private Boolean classMember= Boolean.FALSE;
    private String declaratorId; // name of the declared value
    private String pointer;
    private ArrayList<VariableDeclarationNode> parameters;

    public DeclaratorNode() {
    }

    public String getDeclaratorId() {
        return declaratorId;
    }
    public Boolean getClassMember() {
        return classMember;
    }
    public void setClassMember() {classMember = Boolean.TRUE;}
    public void setDeclaratorId(String declaratorId) {
        this.declaratorId = declaratorId;
    }

    public ArrayList<VariableDeclarationNode> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<VariableDeclarationNode> parameters) {
        this.parameters = parameters;
    }

    public String getPointer() {
        return pointer;
    }

    public void setPointer(String pointer) {
        this.pointer = pointer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeclaratorNode{");
        if (declaratorId != null) {
            sb.append("name: ").append(declaratorId).append(" ");
        }

        if (parameters != null) {
            sb.append("parameters: ").append(parameters);
        }
        sb.append("}");

        return sb.toString();
    }
    @Override
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();

        StringBuilder line = new StringBuilder();

        if (classMember) {
            line.append("self.");
            line.append(declaratorId);
        }
        if(parameters == null){

            sb.append(getIndentedPythonCode(indent,declaratorId));
        }
        sb.append(line.toString());
        return sb.toString();
    }
}
