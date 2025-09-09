package ast.codegen;

import ast.ASTNode;
import ast.ExpressionNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ChainHandlers {

    public interface Handler {
        void emit(ExpressionNode chainRoot, ASTNode lhs, List<ASTNode> rhs, CodegenContext ctx);
    }

    private final Map<String, Handler> byOp = new HashMap<>();
    public void register(String op, Handler h){ byOp.put(op, h); }
    public Handler get(String op){ return byOp.get(op); }

    public static ChainHandlers defaults() {
        ChainHandlers ch = new ChainHandlers();
        ch.register("<<", ChainHandlers::emitStreamLikePrint);
        return ch;
    }

    private static String basePrimaryValue(ASTNode n) {
        if (n instanceof ExpressionNode en) {
            String t = en.getType();
            if ("PrimaryExpression".equals(t)) return en.getValue();
            if ("PostfixExpression".equals(t)
                    && en.getChildren()!=null
                    && !en.getChildren().isEmpty()
                    && en.getChildren().get(0) instanceof ExpressionNode b
                    && "PrimaryExpression".equals(((ExpressionNode) b).getType())) {
                return ((ExpressionNode) b).getValue();
            }
        }
        return null;
    }
    private static boolean isCout(ASTNode n) {
        String v = basePrimaryValue(n);
        return "std::cout".equals(v) || "cout".equals(v);
    }

    private static boolean isEndl(ASTNode n) {
        String v = basePrimaryValue(n);
        return "std::endl".equals(v) || "endl".equals(v);
    }
    private static void emitStreamLikePrint(ExpressionNode root, ASTNode lhs, List<ASTNode> rhs, CodegenContext ctx) {
        var seq = Effects.sequence(rhs, ctx);
        for (var l : seq.pre) ctx.out.writeln(l);

        int start = 0;
        for (int i = 0; i <= rhs.size(); i++) {
            boolean cut = (i == rhs.size()) || isEndl(rhs.get(i));
            if (!cut) continue;
            List<String> codes = seq.codes.subList(start, i);
            boolean endedByEndl = (i < rhs.size()) && isEndl(rhs.get(i));

            if (codes.isEmpty()) {
                ctx.out.writeln("print()");
            } else {
                String args = String.join(", ", codes);
                if (endedByEndl) {
                    ctx.out.writeln("print(" + args + ", sep=\"\")");
                } else {
                    ctx.out.writeln("print(" + args + ", sep=\"\", end=\"\")");
                }
            }
            start = i + 1;
        }

        for (var l : seq.post) ctx.out.writeln(l);
    }
}
