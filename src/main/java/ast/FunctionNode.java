package ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends ASTNode{

    private final String return_value;
    private  DeclaratorNode func_declarator; // Name of function and parameters
    private final List<ASTNode> body;

    public FunctionNode(String return_value, DeclaratorNode declarator) {
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
    public void setFunc_declarator(DeclaratorNode func_declarator) {
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

        StringBuilder sb = new StringBuilder();
        sb.append("Function{");
        sb.append("return_value=").append(return_value);

        if(func_declarator.getDeclaratorId() != null){
            sb.append(", name: ").append(func_declarator.getDeclaratorId());
        }

        if(func_declarator.getParameters() != null){
            sb.append(", parameters: ").append(func_declarator.getParameters());
        }

        if(body.size() > 0){
            sb.append(", body: ").append(body);
        }

        sb.append("}");

        return sb.toString();
    }
}
