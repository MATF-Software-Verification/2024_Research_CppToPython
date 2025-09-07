package ast.iteration;

import ast.ASTNode;
import ast.ExpressionNode;
import ast.VariableDeclarationNode;
import ast.codegen.CodegenContext;

public class ForRangeNode extends IterationNode{

    private ASTNode rangeDeclaration;
    private ASTNode rangeInitializer;

    public ForRangeNode() {}

    public void setRangeDeclaration(ASTNode rangeDeclaration) {
        this.rangeDeclaration = rangeDeclaration;
    }

    public void setRangeInitializer(ExpressionNode rangeInitializer) {
        this.rangeInitializer = rangeInitializer;
    }

    @Override
    protected String nodeLabel() {
        VariableDeclarationNode declaration = (VariableDeclarationNode) rangeDeclaration;
        ExpressionNode initializer = (ExpressionNode) rangeInitializer;
        return "ForRange(" + declaration.getNameOut() + ":" + initializer.getValue() + ")";
    }

    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        if (body != null){
            sb.append(body.toTree(indent + 1));
        }
        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {

        String var = (rangeDeclaration instanceof VariableDeclarationNode v)
                ? v.getNameOut()
                : "_";

        String iter = (rangeInitializer == null) ? "[]" : rangeInitializer.toPython(0);
        if (iter == null || iter.isBlank()) iter = "[]";

        ctx.out.writeln("for " + var + " in " + iter + ":");
        ctx.out.indent();
        if (body != null) {
            body.toPython(0, ctx);
        } else {
            ctx.out.writeln("pass");
        }
        ctx.out.dedent();
        return "";
    }
}
