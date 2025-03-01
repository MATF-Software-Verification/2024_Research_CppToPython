package ast.iteration;

import ast.ASTNode;

import java.util.List;

public abstract class IterationNode extends ASTNode {


    public List<ASTNode> body;

    public List<ASTNode> getBody() {
        return body;
    }

    public void setBody(List<ASTNode> body) {
        this.body = body;
    }
    public void addBody(ASTNode node) {
        this.body.add(node);
    }



}
