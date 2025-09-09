package ast.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExprResult {
    public final List<String> pre;
    public final String code;
    public final List<String> post;

    public ExprResult(List<String> pre, String code, List<String> post) {
        this.pre = pre == null ? List.of() : List.copyOf(pre);
        this.code = code == null ? "" : code;
        this.post = post == null ? List.of() : List.copyOf(post);
    }

    public static ExprResult of(String code) {
        return new ExprResult(List.of(), code, List.of());
    }

    public static List<String> concat(List<String> a, List<String> b) {
        if ((a == null || a.isEmpty()) && (b == null || b.isEmpty())) return List.of();
        List<String> out = new ArrayList<>();
        if (a != null) out.addAll(a);
        if (b != null) out.addAll(b);
        return Collections.unmodifiableList(out);
    }

    public void emitAsStatement(CodeWriter out) {
        for (String l : pre) out.writeln(l);
        if (!code.isBlank()) out.writeln(code);
        for (String l : post) out.writeln(l);
    }
}

