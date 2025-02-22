package ast;

/*
 can be used as PARAMETER NODE for function parameters w/o expression field.
 */
public class VariableDeclarationNode extends ASTNode {

    private String type;
    private DeclaratorNode name;
    private ASTNode expression;

    public VariableDeclarationNode(String type, DeclaratorNode name, ASTNode expression) {
        this.name = name;
        this.expression = expression;
        this.type = type;

    }

    public VariableDeclarationNode(String type, DeclaratorNode name) {
        this.name = name;
        this.type = type;
    }

    public VariableDeclarationNode(){};

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeclaratorNode getName() {
        return name;
    }

    public void setName(DeclaratorNode name) {
        this.name = name;
    }

    public ASTNode getExpression() {
        return expression;
    }

    public void setExpression(ASTNode expression) {
        this.expression = expression;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("VariableDeclaration{");
        if (type != null) {
            sb.append("type: ").append(type).append(" ");
        }

        if (name.getDeclaratorId() != null) {
            sb.append("name: ").append(name.getDeclaratorId()).append(" ");
        }

        if (name.getParameters() != null) {
            sb.append("parameters: ").append(name.getParameters()).append(" ");
        }

        if (expression != null) {
            sb.append("expression: ").append(expression).append(" ");
        }

        sb.append("}");
        return sb.toString();
    }
    public String toPython(int indent) {

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < indent; i++){
            sb.append("\t");
        }
        sb.append(name.toPython(0));
        sb.append(" = ");
        sb.append(expression.toPython(0));
        return sb.toString();
    }
}
