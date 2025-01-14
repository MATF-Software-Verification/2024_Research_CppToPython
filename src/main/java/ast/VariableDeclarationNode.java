package ast;

public class VariableDeclarationNode extends ASTNode {

    private String type;
    private DeclaratorNode name;
    private ASTNode expression;

    public VariableDeclarationNode(String type, DeclaratorNode name, ASTNode expression) {
        this.name = name;
        this.expression = expression;
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
        return "VariableDeclaration{ " + "name=" + name + ", type= "+ type +  ", expression=" + expression + " }";
    }
}
