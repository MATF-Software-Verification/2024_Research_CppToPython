package ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends ASTNode{

    private final String return_value;
    private final String name;
    private final List<ASTNode> body;
    private List<ASTNode> parameters;

    public FunctionNode(String name, String return_value) {
        this.body = new ArrayList<>();
        this.name = name;
        this.return_value = return_value;
    }

    public String getName() {
        return name;
    }

    public String getReturn_value() {
        return return_value;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    public List<ASTNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<ASTNode> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Function{"+"return_value=" + return_value + ", name='" + name + '\'' + ", body=" + body + ", parameters=" + parameters + '}';
    }
}
