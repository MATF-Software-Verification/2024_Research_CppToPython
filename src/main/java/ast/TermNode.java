package ast;

import ast.codegen.CodegenContext;

public class TermNode extends ASTNode {

    private String type;
    private ASTNode expression;

    public TermNode(String type) {
        this.type = type;
        this.expression = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public ASTNode getExpression() {
        return expression;
    }
    public void setExpression(ASTNode expression) {
        this.expression = expression;
    }


    private enum Action { RETURN, BREAK, CONTINUE, THROW, UNKNOWN }

    private static Action actionOf(String t) {
        if (t == null) return Action.UNKNOWN;
        switch (t) {
            case "return":   return Action.RETURN;
            case "break":    return Action.BREAK;
            case "continue": return Action.CONTINUE;
            case "throw":    return Action.THROW;
            default:         return Action.UNKNOWN;
        }
    }
    @Override
    protected String nodeLabel() {
        String a = actionOf(type).name().toLowerCase();
        String hasExpr = (expression != null) ? ", expr" : "";
        return "Term(" + a + hasExpr + ")";
    }
    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        if (expression != null) sb.append(expression.toTree(indent + 1));
        return sb.toString();
    }
    @Deprecated
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TermNode [type= ");
        sb.append(type);
        if (expression != null) {
            sb.append(", expression= ");
            sb.append(expression);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(" ");
        if (expression != null) {
            sb.append(expression.toPython(indent));
        }
        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        Action a = actionOf(type);
        switch (a) {
            case RETURN:
                if (expression == null) {
                    ctx.out.writeln("return");
                } else {
                    ctx.out.writeln("return " + expression.toPython(0));
                }
                break;

            case BREAK:
                ctx.out.writeln("break");
                break;

            case CONTINUE:
                ctx.out.writeln("continue");
                break;

            case THROW:
                if (expression == null) {
                    ctx.out.writeln("raise");
                } else {
                    ctx.out.writeln("raise " + expression.toPython(0));
                }
                break;

            case UNKNOWN:
            default:
                if (expression == null) {
                    ctx.out.writeln("# term: " + (type == null ? "unknown" : type));
                } else {
                    ctx.out.writeln("# term: " + type + " " + expression.toPython(0));
                }
                break;
        }
        return "";
    }
    @Override
    public void collectImports(CodegenContext ctx) {
        if (expression != null) expression.collectImports(ctx);
    }

    @Override
    public void discover(CodegenContext ctx) {
        if (expression != null) expression.discover(ctx);
    }
}
