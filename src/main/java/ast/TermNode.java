package ast;

import ast.codegen.CodegenContext;

import java.util.Locale;

public class TermNode extends ASTNode {

    public enum Action { RETURN, BREAK, CONTINUE, UNKNOWN }
    private final Action action;
    private ASTNode expression;

    public TermNode(String keyword) {
        this.action = actionOf(keyword);
    }

    public ASTNode getExpression() { return expression; }
    public void setExpression(ASTNode expression) { this.expression = expression; }
    private static Action actionOf(String t) {
        if (t == null) return Action.UNKNOWN;
        switch (t.toLowerCase(Locale.ROOT).trim()) {
            case "return":   return Action.RETURN;
            case "break":    return Action.BREAK;
            case "continue": return Action.CONTINUE;
            default:         return Action.UNKNOWN;
        }
    }
    @Override
    protected String nodeLabel() {
        String a = action.name().toLowerCase(Locale.ROOT);
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

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        switch (action) {
            case RETURN:
                if (expression == null) ctx.out.writeln("return");
                else ctx.out.writeln("return " + expression.toPython(0));
                break;

            case BREAK:
                ctx.out.writeln("break");
                break;

            case CONTINUE:
                ctx.out.writeln("continue");
                break;
            case UNKNOWN:
            default:
                if (expression == null) {
                    ctx.out.writeln("# term: unknown");
                } else {
                    ctx.out.writeln("# term: unknown " + expression.toPython(0));
                }
                break;
        }
        return "";
    }

    @Override
    public void discover(CodegenContext ctx) {
        if (expression != null) expression.discover(ctx);
    }
}
