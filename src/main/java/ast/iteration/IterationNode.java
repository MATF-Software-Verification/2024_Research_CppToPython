package ast.iteration;

import ast.ASTNode;

import java.util.List;

public abstract class IterationNode extends ASTNode {


    public ASTNode body;

    public void setBody(ASTNode body) {
        this.body = body;
    }

}
