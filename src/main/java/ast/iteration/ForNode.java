package ast.iteration;

import ast.ASTNode;
import ast.ExpressionNode;
import ast.LiteralNode;
import ast.VariableDeclarationNode;
import ast.codegen.CodegenContext;

public class ForNode extends IterationNode {

    //first Type
    private ASTNode initialStatement;
    private ASTNode condition;
    private ASTNode expression;

    public ForNode(){
        super();
    }

    //First Type
    public ForNode(ASTNode initialStatement, ASTNode condition, ASTNode expression) {
        this.initialStatement = initialStatement;
        this.condition = condition;
        this.expression = expression;
    }


    public ASTNode getInitialStatement() {
        return initialStatement;
    }

    public void setInitialStatement(ASTNode initialStatement) {
        this.initialStatement = initialStatement;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public void setCondition(ASTNode condition) {
        this.condition = condition;
    }

    public ASTNode getExpression() {
        return expression;
    }

    public void setExpression(ASTNode expression) {
        this.expression = expression;
    }


    @Override
    protected String nodeLabel() {
        ExpressionNode cond = (ExpressionNode) condition;

        return "ForNode(" + cond.getValue() + ")";
    }

    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        if (body != null) {
            sb.append(body.toTree(indent + 1));
        } else {
            sb.append(line(indent + 1, "(no body)"));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ForNode [initialStatement=" + initialStatement + ", condition=" + condition + ", expression=" + expression);
        sb.append(" body= ");
        sb.append(body.toString());

        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String toPython(int indent) {

        StringBuilder sb = new StringBuilder();
        StringBuilder line = new StringBuilder();

        if (initialStatement != null) {
            line.append("for ");
            line.append(((VariableDeclarationNode)initialStatement).getNameOut());
            line.append(" in ");
            line.append("range(");
            line.append(((VariableDeclarationNode)initialStatement).getExpressionOut());
            line.append(",");
            line.append(((ExpressionNode)((ExpressionNode)condition).getChildren().get(1)).getValue());
            line.append("):");
        }
        sb.append(getIndentedPythonCode(indent-1,line.toString()));
        if (body != null) {
            sb.append( body.toPython(indent+1));

        }

        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String s = toPython(indent);
        if (s != null && !s.isEmpty()) ctx.out.writeln(s);
        return "";
    }
}
