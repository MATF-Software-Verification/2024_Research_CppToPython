package ast.codegen;

import java.util.*;

public final class CodegenContext {
    public final CodegenOptions options;
    public final CodeWriter out = new CodeWriter();
    public final CodegenMeta meta = new CodegenMeta();
    public final Symbols syms = new Symbols();
    public final ChainHandlers chains = ChainHandlers.defaults();
    public final CallRewriteRegistry rewrites = CallRewriteRegistry.pythonDefaults();


    private final Deque<Set<String>> scopes = new ArrayDeque<>();

    private int tempCounter = 0;

    public CodegenContext(CodegenOptions options) {
        this.options = options;
        scopes.push(new HashSet<>());
    }

    public static CodegenContext pythonDefaults() {
        return new CodegenContext(CodegenOptions.defaults());
    }


    public void enterScope() {
        scopes.push(new HashSet<>());
        syms.enterScopeVars();
    }

    public void exitScope() {
        scopes.pop();
        syms.exitScopeVars();
    }
    public void declare(String name) {
        scopes.peek().add(name);
    }

    // print(i++)
    // print(i)
    // t_0 = i + 1
    public String freshTemp(String hint) {
        return "_" + (hint == null ? "t" : hint) + "_" + (tempCounter++);
    }
}