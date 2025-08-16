package ast.codegen;

import java.util.*;

public final class CodegenContext {
    public final CodegenOptions options;
    public final CodeWriter out = new CodeWriter();
    public final CodegenMeta meta = new CodegenMeta();
    public final Symbols syms = new Symbols();



    private final LinkedHashSet<String> imports = new LinkedHashSet<>();
    private final Deque<Set<String>> scopes = new ArrayDeque<>();

    private int tempCounter = 0;

    public CodegenContext(CodegenOptions options) {
        this.options = options;
        scopes.push(new HashSet<>());
    }

    public static CodegenContext pythonDefaults() {
        return new CodegenContext(CodegenOptions.defaults());
    }

    public void requireImport(String line) {
        if (line != null && !line.isBlank()) imports.add(line);
    }

    public String emitImports() {
        if (imports.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String i : imports) sb.append(i).append("\n");
        sb.append("\n");
        return sb.toString();
    }

    public void enterScope() {
        scopes.push(new HashSet<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public void declare(String name) {
        scopes.peek().add(name);
    }

    public boolean isDeclared(String name) {
        return scopes.stream().anyMatch(s -> s.contains(name));
    }

    public String freshTemp(String hint) {
        return "_" + (hint == null ? "t" : hint) + "_" + (tempCounter++);
    }
}