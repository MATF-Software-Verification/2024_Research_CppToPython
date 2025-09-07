package ast;

import ast.codegen.CodegenContext;
import ast.codegen.ExprResult;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpressionNode extends ASTNode {

    private String type;
    private String operator;
    private List<ASTNode> children = new ArrayList<>();
    private String value;


    public ExpressionNode(){}
    public ExpressionNode(List<ASTNode> children, String type){
        this.children = children;
        this.type = type;
    }


    public List<ASTNode> getChildren() { return children; }
    public String getType() { return type; }
    public String getOperator() {return operator; }
    public String getValue() { return normalizePrimary(value); }
    public void setValue(String value) { this.value = value; }
    public void setType(String type) { this.type = type; }
    public void setOperator(String operator) { this.operator = operator; }
    public void setChildren(List<ASTNode> children) { this.children = children; }
    public void addChild(ASTNode child) { children.add(child); }

    @Override
    protected String nodeLabel() {
        String t = (type == null ? "" : "type=" + type);
        String op = (operator == null ? "" : ", op=" + operator);
        String v = (value == null ? "" : ", val=" + value);
        return "Expr(" + t + op + v + ")";
    }
    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        for (ASTNode c : children) {
            sb.append(c.toTree(indent + 1));
        }
        return sb.toString();
    }
    @Override
    public String toPython(int indent, CodegenContext ctx) {
        emitAsStatement(ctx);
        return "";
    }


    public void emitAsStatement(CodegenContext ctx) {

        switch (safeType()) {
            case "AssignmentExpression": {
                ExprResult left  = emitChildExpr(0, ctx);
                ExprResult right = emitChildExpr(1, ctx);
                String op =  ConvertOperator.convert(operator);
                for (String l : left.pre)  ctx.out.writeln(l);
                for (String l : right.pre) ctx.out.writeln(l);
                ctx.out.writeln(left.code + " " + op + " " + right.code);
                for (String l : left.post)  ctx.out.writeln(l);
                for (String l : right.post) ctx.out.writeln(l);
                return;
            }
            case "PostfixIncrement": {
                String target = emitChildSimple(0, ctx);
                ctx.out.writeln(target + " += 1");
                return;
            }
            case "PostfixDecrement": {
                String target = emitChildSimple(0, ctx);
                ctx.out.writeln(target + " -= 1");
                return;
            }
            case "prefixIncrement":
            case "prefixDecrement": {
                String target = emitChildSimple(0, ctx);
                if ("++".equals(operator)) ctx.out.writeln(target + " += 1");
                else if ("--".equals(operator)) ctx.out.writeln(target + " -= 1");
                else ctx.out.writeln(target);
                return;
            }
            case "ShiftExpression": {
                if (operator != null && children.size() >= 2) {
                    var handler = ctx.chains.get(operator);
                    if (handler != null) {
                        ASTNode lhs = children.get(0);
                        List<ASTNode> rhs = children.subList(1, children.size());
                        handler.emit(this, lhs, rhs, ctx);
                        return;
                    }
                }
                break;
            }
        }

        ExprResult r = emitExpr(ctx);
        r.emitAsStatement(ctx.out);
    }

    public ExprResult emitExpr(CodegenContext ctx) {
        switch (safeType()) {
            case "PrimaryExpression": {
                return ExprResult.of(normalizePrimary(value));
            }

            case "LambdaExpression": {
                String params = (value == null ? "" : value);
                ExprResult body = emitChildExpr(0, ctx);
                List<String> pre = body.pre;
                List<String> post = body.post;
                String code = "(lambda " + params + ": " + body.code + ")";
                return new ExprResult(pre, code, post);
            }

            case "PostfixExpression": {
                if (children.isEmpty()) return ExprResult.of("");
                if (operator != null && children.size() == 1) {
                    ExprResult inner = emitChildExpr(0, ctx);
                    String op = ConvertOperator.convert(operator == null ? "" : operator);
                    return new ExprResult(inner.pre, op + inner.code , inner.post);
                }
                ExprResult baseR = emitChildExpr(0, ctx);
                String code = baseR.code;
                String pendingMember = null;

                for (int i = 1; i < children.size(); i++) {
                    ASTNode ch = children.get(i);

                    if (ch instanceof LiteralNode lit) {
                        pendingMember = lit.getValue();
                        code = code + "." + pendingMember;
                        continue;
                    }

                    if (ch instanceof ExpressionNode en && ("LIST".equals(en.getType()) || "LIST_IDX".equals(en.getType()))) {
                        String tail = en.getValue();
                        boolean isCall = "LIST".equals(en.getType());

                        if (isCall && pendingMember != null) {
                            List<ExprResult> args = new java.util.ArrayList<>();
                            if (en.getChildren()!=null && !en.getChildren().isEmpty()){
                                for (ASTNode argNode : en.getChildren()){
                                    args.add(argNode instanceof ExpressionNode earg ? earg.emitExpr(ctx)
                                            : ExprResult.of(argNode.toPython(0)));
                                }
                            }

                            String recvType = guessReceiverType(children.get(0), ctx);
                            var rw = ctx.rewrites.findMethod(recvType, pendingMember);
                            if (rw != null) {
                                var pre = new java.util.ArrayList<String>();
                                pre.addAll(baseR.pre);
                                for (var a : args) pre.addAll(a.pre);

                                var post = new java.util.ArrayList<String>();
                                for (var a : args) post.addAll(a.post);
                                post.addAll(baseR.post);

                                ExprResult rewritten = rw.rewrite(baseR.code, args, ctx);
                                return new ExprResult(pre, rewritten.code, post);
                            } else {
                            }
                        }

                        code = code + (tail == null ? "" : tail);
                        pendingMember = null;
                        continue;
                    }

                    ExprResult sub = emitArbitraryChild(ch, ctx);
                    baseR = new ExprResult(ExprResult.concat(baseR.pre, sub.pre), code, ExprResult.concat(baseR.post, sub.post));
                    code = baseR.code + sub.code;
                    pendingMember = null;
                }

                return new ExprResult(baseR.pre, code, baseR.post);
            }

            case "prefixIncrement":
            case "prefixDecrement": {
                String base = emitChildSimple(0, ctx);
                String pre = base + ("++".equals(operator) ? " += 1" : " -= 1");
                return new ExprResult(List.of(pre), base, List.of());
            }

            case "PostfixIncrement":
            case "PostfixDecrement": {
                String base = emitChildSimple(0, ctx);
                String t = ctx.freshTemp("t");
                List<String> pre = new ArrayList<>();
                pre.add(t + " = " + base);
                pre.add(base + ("PostfixIncrement".equals(type) ? " += 1" : " -= 1"));
                return new ExprResult(pre, t, List.of());
            }

            case "UnaryOperator":
            case "unaryOperator": {
                ExprResult a = emitChildExpr(0, ctx);
                return new ExprResult(a.pre, operator + a.code, a.post);
            }

            case "PointerMemberExpression": {
                String conv = ConvertFunctionCall.convert(value == null ? "" : value);
                return ExprResult.of(conv);
            }

            case "AssignmentExpression": {
                ExprResult left  = emitChildExpr(0, ctx);
                ExprResult right = emitChildExpr(1, ctx);
                String t = ctx.freshTemp("asgn");
                List<String> pre = new ArrayList<>();
                pre.addAll(left.pre); pre.addAll(right.pre);
                pre.add(t + " = " + right.code);
                pre.add(left.code + " " +  ConvertOperator.convert(operator) + " " + t);
                List<String> post = new ArrayList<>();
                post.addAll(left.post); post.addAll(right.post);
                return new ExprResult(pre, t, post);
            }

            case "AdditiveExpression":
            case "MultiplicativeExpression": {
                if ("/".equals(operator) && children.size() == 2) {
                    ExprResult leftR  = emitChildExpr(0, ctx);
                    ExprResult rightR = emitChildExpr(1, ctx);

                    String op = "//";

                    List<String> pre  = ExprResult.concat(leftR.pre, rightR.pre);
                    List<String> post = ExprResult.concat(leftR.post, rightR.post);
                    String code = leftR.code + " " + op + " " + rightR.code;
                    return new ExprResult(pre, code, post);
                }

                String joinOp = ConvertOperator.convert(operator);
                return foldNAry(joinOp, ctx);
            }
            case "ShiftExpression": {
                if (operator != null && ctx.chains.get(operator) != null) {
                    List<ExprResult> parts = emitChildren(ctx);
                    List<String> pre = concatPre(parts);
                    List<String> post = concatPost(parts);
                    return new ExprResult(pre, "None", post);
                }
                String joinOp =  ConvertOperator.convert(operator);
                return foldNAry(joinOp, ctx);
            }

            case "RelationalExpression":
            case "EqualityExpression":
            case "LogicalAndExpression":
            case "LogicalOrExpression": {
                String joinOp = ConvertOperator.convert(operator);
                return foldNAry(joinOp, ctx);
            }

            case "InitializerList": {
                List<ExprResult> parts = emitChildren(ctx);
                List<String> pre = concatPre(parts);
                List<String> post = concatPost(parts);
                String code = parts.stream().map(p -> p.code).collect(Collectors.joining(", "));
                return new ExprResult(pre, code, post);
            }

            case "Conditional": {
                ExprResult c = emitChildExpr(0, ctx);
                ExprResult a = emitChildExpr(1, ctx);
                ExprResult b = emitChildExpr(2, ctx);
                List<String> pre = ExprResult.concat(ExprResult.concat(c.pre, a.pre), b.pre);
                List<String> post = ExprResult.concat(ExprResult.concat(c.post, a.post), b.post);
                String code = "(" + a.code + " if " + c.code + " else " + b.code + ")";
                return new ExprResult(pre, code, post);
            }

            default:
                if (children.isEmpty()) return ExprResult.of(normalizePrimary(value));
                List<ExprResult> parts = emitChildren(ctx);
                List<String> pre = concatPre(parts);
                List<String> post = concatPost(parts);
                String code = parts.stream().map(p -> p.code).collect(Collectors.joining(" "));
                return new ExprResult(pre, code, post);
        }
    }
    private String safeType() { return (type == null ? "" : type); }


    private ExprResult emitChildExpr(int idx, CodegenContext ctx) {
        if (idx < 0 || idx >= children.size()) return ExprResult.of("");
        ASTNode ch = children.get(idx);
        if (ch instanceof ExpressionNode en) return en.emitExpr(ctx);
        return ExprResult.of(ch.toPython(0));
    }
    private ExprResult emitArbitraryChild(ASTNode ch, CodegenContext ctx) {
        if (ch instanceof ExpressionNode en) return en.emitExpr(ctx);
        return ExprResult.of(ch.toPython(0));
    }
    private String emitChildSimple(int idx, CodegenContext ctx) {
        return emitChildExpr(idx, ctx).code;
    }
    private List<ExprResult> emitChildren(CodegenContext ctx) {
        List<ExprResult> out = new ArrayList<>(children.size());
        for (ASTNode c : children) out.add(c instanceof ExpressionNode en ? en.emitExpr(ctx) : ExprResult.of(c.toPython(0)));
        return out;
    }
    private static List<String> concatPre(List<ExprResult> xs) {
        List<String> out = new ArrayList<>();
        for (ExprResult r : xs) out.addAll(r.pre);
        return out;
    }
    private static List<String> concatPost(List<ExprResult> xs) {
        List<String> out = new ArrayList<>();
        for (ExprResult r : xs) out.addAll(r.post);
        return out;
    }

    private ExprResult foldNAry(String op, CodegenContext ctx) {
        List<ExprResult> parts = emitChildren(ctx);
        if (parts.isEmpty()) return ExprResult.of("");
        List<String> pre = concatPre(parts);
        List<String> post = concatPost(parts);
        String glue = " " + op + " ";
        String code = parts.stream().map(p -> p.code).collect(Collectors.joining(glue));
        return new ExprResult(pre, code, post);
    }

    private static String normalizePrimary(String v) {
        if (v == null) return "";
        switch (v) {
            case "this": return "self";
            case "true": return "True";
            case "false": return "False";
            case "nullptr":
            case "NULL": return "None";
            default: return v;
        }
    }

    @Override
    public String toPython(int indent) {
        switch (safeType()) {
            case "PrimaryExpression": {
                return normalizePrimary(value);
            }

            case "LambdaExpression": {
                String params = (value == null ? "" : value);
                String body = childInline(0);
                return "(lambda " + params + ": " + body + ")";
            }

            case "PostfixExpression": {
                if (children.isEmpty()) return "";
                if (operator != null && children.size() == 1) {
                    String inner = childInline(0);
                    String op = ConvertOperator.convert(operator == null ? "" : operator);
                    return  op + inner;
                }

                String code = childInline(0);
                for (int i = 1; i < children.size(); i++) {
                    ASTNode ch = children.get(i);
                    if (ch instanceof LiteralNode lit) {
                        code = code + "." + lit.getValue();
                        continue;
                    }
                    if (ch instanceof ExpressionNode en) {
                        String t = en.getType();
                        if ("LIST".equals(t) || "LIST_IDX".equals(t)) {
                            String tail = en.getRawInlineOrValue();
                            code = code + (tail == null ? "" : tail);
                            continue;
                        }
                        code = code + en.toPython(0);
                    } else {
                        code = code + ch.toPython(0);
                    }
                }
                return code;
            }

            case "prefixIncrement":
            case "prefixDecrement": {
                String base = childInline(0);
                String op = "++".equals(operator) ? "+= 1" : "-= 1";
                return base;
            }

            case "PostfixIncrement":
            case "PostfixDecrement": {
                return childInline(0);
            }

            case "UnaryOperator":
            case "unaryOperator": {
                String a = childInline(0);
                String op = ConvertOperator.convert(operator == null ? "" : operator);
                return "(" + op + a + ")";
            }

            case "PointerMemberExpression": {
                return ConvertFunctionCall.convert(value == null ? "" : value);
            }

            case "AssignmentExpression": {
                String right = childInline(1);
                return "(" + right + ")";
            }

            case "AdditiveExpression":
            case "MultiplicativeExpression":
            case "RelationalExpression":
            case "EqualityExpression":
            case "LogicalAndExpression":
            case "LogicalOrExpression":
            case "ShiftExpression": {
                String joinOp =  ConvertOperator.convert(operator);
                return foldNAryInline(joinOp);
            }

            case "InitializerList": {
                List<String> parts = inlineChildren();
                return parts.stream().collect(Collectors.joining(", "));
            }

            case "Conditional": {
                String c = childInline(0);
                String a = childInline(1);
                String b = childInline(2);
                return "(" + a + " if " + c + " else " + b + ")";
            }

            default:
                if (children.isEmpty()) return normalizePrimary(value);
                List<String> parts = inlineChildren();
                return parts.stream().collect(Collectors.joining(" "));
        }
    }
    private String getRawInlineOrValue() {
        if (value != null) return value;
        return toPython(0);
    }

    private List<String> inlineChildren() {
        List<String> out = new ArrayList<>(children.size());
        for (ASTNode c : children) out.add(c.toPython(0));
        return out;
    }
    private String childInline(int idx) {
        if (idx < 0 || idx >= children.size()) return "";
        return children.get(idx).toPython(0);
    }
    private String foldNAryInline(String op) {
        List<String> parts = inlineChildren();
        if (parts.isEmpty()) return "";
        String glue = " " + op + " ";
        return parts.stream().collect(Collectors.joining(glue));
    }
    private String guessReceiverType(ASTNode base, CodegenContext ctx){
        if (base instanceof ExpressionNode en && "PrimaryExpression".equals(en.getType())) {
            String name = en.getValue();
            String ty = ctx.syms.lookupVarType(name);
            if (ty != null) return ty;
        }
        return null;
    }




}
