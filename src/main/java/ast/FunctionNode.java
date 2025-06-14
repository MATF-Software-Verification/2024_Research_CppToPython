package ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends ASTNode{

    private final String return_value;
    private DeclaratorNode func_declarator; // Name of function and parameters
    private CompoundNode body; //TODO change to CompoundNode later.
    private boolean in_class;

    public FunctionNode(String return_value, DeclaratorNode declarator, boolean in_class) {
//        this.body = new ArrayList<>();
        this.return_value = return_value;
        this.func_declarator = declarator;
        this.in_class = in_class;
    }

    public String getName(){
        return func_declarator.getDeclaratorId();
    }
    public void addBodyNode(ASTNode node){
        body.add(node);
    }

    public DeclaratorNode getFunc_declarator() {
        return func_declarator;
    }

    public void setFunc_declarator(DeclaratorNode func_declarator) {
        this.func_declarator = func_declarator;
    }

    public String getReturn_value() {
        return return_value;
    }

    public CompoundNode getBody() {
        return body;
    }

    public void setBody(CompoundNode body) {
        this.body = body;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Function{");
        if(return_value != null){
            sb.append("return_value=").append(return_value);
        }

        if(func_declarator.getDeclaratorId() != null){
            sb.append(", name: ").append(func_declarator.getDeclaratorId());
        }

        if(func_declarator.getParameters() != null){
            sb.append(", parameters: ").append(func_declarator.getParameters());
        }

        if(body != null ){
            sb.append(", body: ").append(body);
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toPython(int indent) {

        StringBuilder sb = new StringBuilder();
        StringBuilder code = new StringBuilder();
        code.append("def ");
        code.append(func_declarator.getDeclaratorId());
        code.append("(");
        if(in_class){
            code.append("self");
            if (func_declarator.getParameters() != null){
                code.append(",");
            }
        }
        if(func_declarator.getParameters() != null) {
            for (int i = 0; i < func_declarator.getParameters().size(); i++) {

                VariableDeclarationNode param = func_declarator.getParameters().get(i);
                String name = param.getName().getDeclaratorId();
                if (i < func_declarator.getParameters().size() - 1) {
                    code.append(name + ",");
                } else {
                    code.append(name);
                }
            }
        }
        code.append("):");
        sb.append(getIndentedPythonCode(indent, code.toString()));
        sb.append(body.toPython(indent+1));

        return sb.toString();
    }


}
