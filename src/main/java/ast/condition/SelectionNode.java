package ast.condition;


import ast.ASTNode;
import ast.CaseNode;
import ast.CompoundNode;
import ast.ExpressionNode;
import ast.codegen.CodegenContext;

import java.util.ArrayList;
import java.util.Locale;

public class SelectionNode extends ASTNode {

    private String type;
    private ExpressionNode condition;
    private ASTNode thenBranch;
    private ASTNode elseBranch;


    public SelectionNode(){}
    public SelectionNode(String type, ExpressionNode condition, ASTNode thenBranch) {
        this.type = type;
        this.condition = condition;
        this.thenBranch = thenBranch;
    }

    public void setElseBranch(ASTNode elseBranch) {
        this.elseBranch = elseBranch;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) { this.condition = condition; }
    public void setThenBranch(ASTNode thenBranch) {this.thenBranch = thenBranch;}


    private enum Kind { IF, ELIF, ELSE, SWITCH, UNKNOWN }

    private Kind kind() {
        String t = (type == null ? "" : type.trim().toLowerCase(Locale.ROOT));
        if (t.equals("if")) return Kind.IF;
        if (t.equals("elseif") || t.equals("else if")) return Kind.ELIF;
        if (t.equals("else")) return Kind.ELSE;
        if (t.equals("switch")) return Kind.SWITCH;
        return Kind.UNKNOWN;
    }

    @Override
    protected String nodeLabel() {
        String t = (type == null ? "" : type);
        String c = (condition == null ? "" : condition.getValue());
        return "Condition(" + t + (c.isEmpty() ? "" : " " + c) + ")";
    }

    private void emitElseChain(ASTNode branch, CodegenContext ctx) {
        if (branch == null) return;

        if (branch instanceof SelectionNode sel) {
            switch (sel.kind()) {
                case ELIF: {
                    if (sel.condition == null || sel.thenBranch == null) return;
                    ctx.out.writeln("elif " + sel.condition.toPython(0) + ":");
                    ctx.out.indent();
                    sel.thenBranch.toPython(0, ctx);
                    ctx.out.dedent();
                    emitElseChain(sel.elseBranch, ctx);
                    return;
                }
                case ELSE: {
                    if (sel.thenBranch == null) return;
                    ctx.out.writeln("else:");
                    ctx.out.indent();
                    sel.thenBranch.toPython(0, ctx);
                    ctx.out.dedent();
                    return;
                }
                default:
            }
        }

        ctx.out.writeln("else:");
        ctx.out.indent();
        branch.toPython(0, ctx);
        ctx.out.dedent();
    }


    @Override
    public String toPython(int indent, CodegenContext ctx) {
        switch (kind()) {
            case IF: {
                if (condition == null || thenBranch == null) return "";
                ctx.out.writeln("if " + condition.toPython(0) + ":");
                ctx.out.indent();
                thenBranch.toPython(0, ctx);
                ctx.out.dedent();
                emitElseChain(elseBranch, ctx);
                return "";
            }
            case ELIF: {
                if (condition == null || thenBranch == null) return "";
                ctx.out.writeln("elif " + condition.toPython(0) + ":");
                ctx.out.indent();
                thenBranch.toPython(0, ctx);
                ctx.out.dedent();
                emitElseChain(elseBranch, ctx);
                return "";
            }
            case ELSE: {
                if (thenBranch == null) return "";
                ctx.out.writeln("else:");
                ctx.out.indent();
                thenBranch.toPython(0, ctx);
                ctx.out.dedent();
                return "";
            }
            case SWITCH: {
                ctx.out.writeln("# TODO: switch/case");
                return "";
            }
            case UNKNOWN:
            default: {
                ctx.out.writeln("unknown selection node");
                return "";
            }
        }
    }
}