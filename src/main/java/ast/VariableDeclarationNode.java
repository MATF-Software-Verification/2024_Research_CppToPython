package ast;

import utils.ClassStorage;
import utils.FunctionStorage;

/*
 can be used as PARAMETER NODE for function parameters w/o expression field.
 */
public class VariableDeclarationNode extends ASTNode {

    private String type;
    private DeclaratorNode name;
    private ASTNode expression;
    private Boolean classMember = Boolean.FALSE;

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

    public Boolean getClassMember() {
        return classMember;
    }
    public void setClassMember() {
        this.classMember = Boolean.TRUE;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeclaratorNode getName() {
        return name;
    }
    public String getNameOut(){
        return name.getDeclaratorId();
    }

    public void setName(DeclaratorNode name) {
        this.name = name;
    }

    public ASTNode getExpression() {
        return expression;
    }
    public String getExpressionOut(){
        //TODO fix probably needed
        ExpressionNode exp = (ExpressionNode) expression;
        return exp.getValue();
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

        if (name != null  && name.getDeclaratorId() != null) {
            sb.append("name: ").append(name.getDeclaratorId()).append(" ");
        }

        if ( name != null && name.getParameters() != null) {
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
        StringBuilder line = new StringBuilder();

        if(type != null && type.equals("class")) {
            sb.append(line);
            sb.append(expression.toPython(indent));

        }
        else if(name != null && FunctionStorage.getInstance().hasFunction(name.getDeclaratorId()) && !ClassStorage.getInstance().hasClass(name.getDeclaratorId())) {
            line.append(name.getDeclaratorId()).append("(");
            if(expression != null) {
                line.append(expression.toPython(0));
            }
            line.append(")");
            sb.append(line);
        }
        else if (type != null && ClassStorage.getInstance().hasClass(type)) {

            line.append(getNameOut());
            line.append("=").append(getType()).append("(");
            if(expression != null) {
                line.append(((ExpressionNode)expression).toPython(0));
            }
            line.append(")");
            sb.append(line);
            //TODO: this should be fixed crucial;
        }
        else if (type != null && type.contains("vector")){
            line.append(getNameOut());
            line.append(" = ");
            line.append("[");
            line.append(expression.toPython(indent));
            line.append("]");
            sb.append(line);
        }
        else {
            if (expression != null) {
                line.append(getNameOut());
                line.append(" = ");
                line.append(expression.toPython(indent));
            }
            sb.append(line);
        }

        return sb.toString();
    }
}
