package ast.iteration;

import ast.ASTNode;
import ast.ExpressionNode;
import ast.LiteralNode;
import ast.VariableDeclarationNode;
import ast.codegen.CodegenContext;

import java.util.List;
import java.util.Objects;

public class ForNode extends IterationNode {

    private ASTNode initialStatement;
    private ASTNode condition;
    private ASTNode expression;

    public ForNode(){
        super();
    }

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


    private static String expr(ASTNode n, CodegenContext ctx) {
        if (n instanceof ExpressionNode en) return en.emitExpr(ctx).code;
        return n == null ? "" : n.toPython(0);
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
        String var   = "i";
        String start = "0";
        String end   = "0";
        String step  = "1";
        if (initialStatement instanceof VariableDeclarationNode v) {
            var = v.getNameOut();
            if (v.getExpression() != null) start = expr(v.getExpression(), ctx);
        } else {
            ctx.out.writeln("# unsupported for-init");
            ctx.out.writeln("pass");
            return "";
        }

        if (condition instanceof ExpressionNode ce) {
            String op = ce.getOperator();
            var kids = ce.getChildren();
            if (kids != null && kids.size() >= 2) {
                String lhs = expr(kids.get(0), ctx);
                String rhs = expr(kids.get(1), ctx);
                if (lhs.equals(var)) {
                    if ("<".equals(op))       end = rhs;
                    else if ("<=".equals(op)) end = "(" + rhs + " + 1)";
                    else {
                        ctx.out.writeln("pass");
                        return "";
                    }
                } else {
                    ctx.out.writeln("pass");
                    return "";
                }
            } else {
                ctx.out.writeln("pass");
                return "";
            }
        } else {
            ctx.out.writeln("pass");
            return "";
        }

        if (expression instanceof ExpressionNode ue) {
            String uop = ue.getOperator();
            var kids = ue.getChildren();
            if ("++".equals(uop)) {
                step = "1";
            } else if ("+=".equals(uop) && kids != null && kids.size() >= 2) {
                step = expr(kids.get(1), ctx);
            } else {
                ctx.out.writeln("pass");
                return "";
            }
        } else {
            ctx.out.writeln("pass");
            return "";
        }

        String rangeArgs = start + ", " + end + ( "1".equals(step) ? "" : ", " + step );
        ctx.out.writeln("for " + var + " in range(" + rangeArgs + "):");
        ctx.out.indent();
        if (body != null) body.toPython(0, ctx);
        else ctx.out.writeln("pass");
        ctx.out.dedent();
        return "";
    }

}
