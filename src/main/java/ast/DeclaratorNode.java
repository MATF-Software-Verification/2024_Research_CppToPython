package ast;

import ast.codegen.CodegenContext;

import java.util.ArrayList;
import java.util.List;

public class DeclaratorNode extends ASTNode{

    private Boolean classMember= Boolean.FALSE;
    private String declaratorId;
    private String pointer;
    private ArrayList<VariableDeclarationNode> parameters;

    public DeclaratorNode() {
    }

    public String getDeclaratorId() {
        return declaratorId;
    }
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

    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }


    @Override
    protected String nodeLabel() {
        String id = declaratorId == null ? "<anon>" : declaratorId;
        String ptr = pointer == null ? "" : ", ptr=" + pointer;
        String arity = hasParameters() ? (", params=" + parameters.size()) : "";
        String cm = classMember ? ", member" : "";
        return "Declarator(" + id + ptr + arity + cm + ")";
    }


    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        if (hasParameters()) {
            for (VariableDeclarationNode p : parameters) {
                sb.append(p.toTree(indent + 1));
            }
        }
        return sb.toString();
    }

    @Deprecated
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
        if(!hasParameters()){

            sb.append(getIndentedPythonCode(indent,declaratorId));
        }
        sb.append(line);
        return sb.toString();
    }

    //TODO: Check if we need to fix CodegenContext
    @Override
    public String toPython(int indent, CodegenContext ctx) {
        return toPython(indent);
    }
}
