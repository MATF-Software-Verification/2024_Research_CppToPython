package ast.iteration;

import ast.ASTNode;
import ast.ExpressionNode;
import ast.codegen.CodegenContext;

public class WhileNode  extends  IterationNode {

    private ASTNode condition;

    public WhileNode() {super();}

    public ASTNode getCondition() {
        return condition;
    }

    public void setCondition(ASTNode condition) {
        this.condition = condition;
    }

    @Override
    protected String nodeLabel() {
        String c;
        if (condition instanceof ExpressionNode en) c = en.getValue();
        else if (condition != null) c = condition.toPython(0);
        else c = "True";
        return "While(" + (c == null ? "" : c) + ")";
    }

    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        if (body != null) sb.append(body.toTree(indent + 1));
        else sb.append(line(indent + 1, "(no body)"));
        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String cond = (condition == null) ? "True" : condition.toPython(0);
        ctx.out.writeln("while " + (cond == null ? "True" : cond) + ":");
        ctx.out.indent();
        if (body != null) {
            body.toPython(0, ctx);
        } else {
            ctx.out.writeln("pass");
        }
        ctx.out.dedent();
        return "";
    }
}
