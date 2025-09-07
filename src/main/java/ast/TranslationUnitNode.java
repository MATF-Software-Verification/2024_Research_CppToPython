package ast;

import ast.codegen.CodegenContext;
import ast.functions.FunctionNode;

import java.util.List;

public class TranslationUnitNode extends ASTNode {
    private final List<ASTNode> declarations;

    public TranslationUnitNode(List<ASTNode> declarations) {
        this.declarations = List.copyOf(declarations);
    }

    public List<ASTNode> getDeclarations() {
        return declarations;
    }

    @Override
    protected String nodeLabel() {
        return "TranslationUnit(decls=" + declarations.size() + ")";
    }

    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent,nodeLabel()));
        for (ASTNode declaration : declarations) {
            sb.append(declaration.toTree(indent+1));
        }
        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        boolean first = true;
        for (ASTNode d : declarations) {
            if (!first && ctx.options.blankLineBetweenDecls) ctx.out.writeln("");
            d.toPython(0, ctx);
            first = false;
        }
        return ctx.out.toString();
    }
    @Override
    public void discover(CodegenContext ctx) {
        for (ASTNode d : declarations) {
            if (d instanceof FunctionNode fn) {
                var decl = fn.getFunc_declarator();
                String name = (decl != null) ? decl.getDeclaratorId() : null;
                if ("main".equals(name)) ctx.meta.hasMain = true;
            }
            d.discover(ctx);
        }
    }

}