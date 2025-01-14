package ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends ASTNode{

    private final String return_value;
    private  ASTNode func_declarator; // Name of function and parameters
    private final List<ASTNode> body;

    public FunctionNode(String return_value, ASTNode declarator) {
        this.body = new ArrayList<>();
        this.return_value = return_value;
        this.func_declarator = declarator;
    }

    public void addBodyNode(ASTNode node){
        body.add(node);
    }

    public ASTNode getFunc_declarator() {
        return func_declarator;
    }
    public void setFunc_declarator(ASTNode func_declarator) {
        this.func_declarator = func_declarator;
    }

    public String getReturn_value() {
        return return_value;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Function{"+"return_value=" + return_value + ", name='" + func_declarator.toString() + '\'' + ", body=" + body +  + '}';
    }
}
