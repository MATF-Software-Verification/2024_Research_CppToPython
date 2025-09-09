package ast.codegen;

import ast.ASTNode;
import ast.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

public final class Effects {
    public static final class Seq {
        public final List<String> pre = new ArrayList<>();
        public final List<String> codes = new ArrayList<>();
        public final List<String> post = new ArrayList<>();
    }

    public static Seq sequence(List<? extends ASTNode> nodes, CodegenContext ctx) {
        Seq s = new Seq();
        for (ASTNode n : nodes) {
            var r = (n instanceof ExpressionNode en) ? en.emitExpr(ctx)
                    : ast.codegen.ExprResult.of(n.toPython(0));
            s.pre.addAll(r.pre);
            s.codes.add(r.code);
            s.post.addAll(r.post);
        }
        return s;
    }
}