package ast.iteration;

import ast.ASTNode;
import ast.ExpressionNode;

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
    public String toString() {
        return "";
    }

    @Override
    public String toPython(int indent) {
        return "";
    }
}
