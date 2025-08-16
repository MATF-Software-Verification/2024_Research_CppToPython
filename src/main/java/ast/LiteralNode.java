package ast;

import ast.codegen.CodegenContext;

public class LiteralNode extends ASTNode {

    private String value;


    public LiteralNode(String value) {
        this.value = value;
    }
    public LiteralNode() {}


    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected String nodeLabel() {
        String v = value == null ? "null" : value;
        return "Literal(" + v + ")";
    }

    @Override
    public String toTree(int indent) {
        return line(indent, nodeLabel());
    }

    @Override
    public String toString() {
        return "LiteralNode( " + value + " )";
    }

    @Override
    public String toPython(int indent) {
        return normalizeLiteral(value);
    }
    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String s = toPython(indent);
        if (s != null && !s.isEmpty()) ctx.out.write(s);
        return "";
    }

    private static String normalizeLiteral(String v) {
        if (v == null) return "";

        if ("true".equals(v))  return "True";
        if ("false".equals(v)) return "False";
        if ("nullptr".equals(v) || "NULL".equals(v)) return "None";

        return v;
    }
}
