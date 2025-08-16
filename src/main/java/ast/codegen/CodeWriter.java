package ast.codegen;

public final class CodeWriter {

    private final StringBuilder sb = new StringBuilder();
    private int indent = 0;
    private static final String INDENT = "    ";

    public void indent() { indent++; }
    public void dedent() { if (indent > 0) indent--; }

    public CodeWriter writeln(String s) {
        sb.append(INDENT.repeat(indent)).append(s).append("\n");
        return this;
    }
    public CodeWriter write(String s) {
        sb.append(s);
        return this;
    }
    @Override public String toString() { return sb.toString(); }
}
