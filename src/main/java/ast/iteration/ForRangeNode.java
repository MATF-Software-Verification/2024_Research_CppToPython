package ast.iteration;

import ast.ASTNode;
import ast.ExpressionNode;
import ast.VariableDeclarationNode;
import ast.codegen.CodegenContext;

public class ForRangeNode extends IterationNode{

    private ASTNode rangeDeclaration;
    private ASTNode rangeInitializer;

    public ForRangeNode() {}
    public ForRangeNode(ASTNode rangeDeclaration, ASTNode rangeInitializer){
        super();
        this.rangeDeclaration = rangeDeclaration;
        this.rangeInitializer = rangeInitializer;
    }
    public ASTNode getRangeDeclaration() {
        return rangeDeclaration;
    }

    public void setRangeDeclaration(ASTNode rangeDeclaration) {
        this.rangeDeclaration = rangeDeclaration;
    }

    public ASTNode getRangeInitializer() {
        return rangeInitializer;
    }

    public void setRangeInitializer(ExpressionNode rangeInitializer) {
        this.rangeInitializer = rangeInitializer;
    }

    public void setRangeInitializer(ASTNode rangeInitializer) {
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

    @Deprecated
    @Override
    public String toString() {
        return "";
    }

    @Override
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder line = new StringBuilder();

        if (rangeDeclaration != null){
            line.append("for ");
            line.append(((VariableDeclarationNode)rangeDeclaration).getNameOut());
            line.append(" in ");
            line.append(((ExpressionNode)rangeInitializer).getValue());
            line.append(":");
        }
        sb.append(getIndentedPythonCode(indent-1,line.toString()));
        if (body != null) {
            sb.append(body.toPython(indent+1));
        }
        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String s = toPython(indent);
        if (s != null && !s.isEmpty()) ctx.out.writeln(s);
        return "";
    }
}
