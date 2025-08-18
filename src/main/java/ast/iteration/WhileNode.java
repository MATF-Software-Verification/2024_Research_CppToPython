package ast.iteration;

import ast.ASTNode;
import ast.ExpressionNode;
import ast.codegen.CodegenContext;

public class WhileNode  extends  IterationNode {

    private ASTNode condition;

    public WhileNode() {super();}
    public WhileNode(ASTNode condition){
        super();
        this.condition = condition;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public void setCondition(ASTNode condition) {
        this.condition = condition;
    }

    @Override
    protected String nodeLabel() {
        ExpressionNode cond = (ExpressionNode) condition;
        String c = cond.getValue();
        return "While(" + c + ")";
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
        sb.append("WhileNode [condition=");
        sb.append(condition);
        sb.append("body=");
        sb.append(body);
        sb.append("]");
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder line = new StringBuilder();
        line.append("while ");
        if (condition != null) {
            line.append(((ExpressionNode)getCondition()).getValue());
        }

        line.append(":");
        sb.append(getIndentedPythonCode(indent-2,line.toString()));
        if(body != null){
            sb.append(body.toPython(indent+1));
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
