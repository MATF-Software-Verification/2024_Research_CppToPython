package ast;

import ast.codegen.CodegenContext;

public abstract class ASTNode {

    protected static String indent(int n) {
        return "    ".repeat(Math.max(0, n));
    }

    protected abstract String nodeLabel();

    public String toString() {
        return toTree(0);
    }

    public String toTree(int indent) {
        return line(indent, nodeLable());
    }

    protected String nodeLable() {
        return getClass().getSimpleName();
    }

    public String toPython(int indent) {
        return "Basic Node without override";
    }

    public void discover(CodegenContext ctx) {
    }

    public abstract String toPython(int indent, CodegenContext ctx);

    protected final String line(int indent, String code) {
        return indent(indent) + code + "\n";
    }

    public String getIndentedPythonCode(int indent, String code) {
        return line(indent, code);
    }
}