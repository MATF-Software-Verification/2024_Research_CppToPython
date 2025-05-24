package ast;

import java.util.ArrayList;
import java.util.List;

public class ExpressionNode extends ASTNode {

    private String type;
    private String operator;
    private List<ASTNode> children = new ArrayList<ASTNode>();
    private String value;


    public ExpressionNode(){}
    public ExpressionNode(List<ASTNode> children, String type){
        this.children = children;
        this.type = type;
    }


    public List<ASTNode> getChildren() { return children; }
    public String getType() { return type; }
    public String getOperator() {return operator; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public void setType(String type) { this.type = type; }
    public void setOperator(String operator) { this.operator = operator; }
    public void setChildren(List<ASTNode> children) { this.children = children; }
    public void addChild(ASTNode child) { children.add(child); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Expression:");
        if(type != null) sb.append("[ type : ").append(type).append(" ]");
        if(operator != null){
            sb.append("[ operator: ").append(operator).append(" ]");
        }
        if(value != null){
            sb.append(" [value: ").append(value).append("]");
        }
        if(!children.isEmpty()){

            sb.append(" children: ").append(children).append("]");
        }

        return sb.toString();
    }

    @Override
    public String toPython(int indent) {


        StringBuilder sb = new StringBuilder();

        if(type != null && type.equals("AdditiveExpression")){
            sb.append(value);
        }
        if(type != null && type.equals("RelationalExpression")){
            sb.append(value);
        }

        if (type != null && type.equals("LogicalAndExpression")){
            String formatted = value.replace("&&", " and ");
            sb.append(formatted);
        }

        if (type != null && type.equals("LogicalOrExpression")){
            String formatted = value.replace("||", " or ");
            sb.append(formatted);
        }

        if (type != null && type.equals("ShiftExpression")){
            String formatted = value.replace("std::cout<<", "").trim();

            // Check for std::endl and remove it
            boolean endsWithNewline = formatted.contains("std::endl");
            formatted = formatted.replace("std::endl", "").trim();
            formatted = formatted.replace("<<", ";").trim();
            String[] parts = formatted.split(";");

            StringBuilder content = new StringBuilder();
            boolean needsFString = false;

            for (String part : parts) {
                part = part.trim();

                if ((part.startsWith("\"") && part.endsWith("\"")) ||
                        (part.startsWith("'") && part.endsWith("'"))) {

                    // Remove quotes and append directly
                    String unquoted = part.substring(1, part.length() - 1)
                            .replace("\"", "\\\"")
                            .replace("\\n", "\n");
                    content.append(unquoted);
                } else if (!part.isEmpty()) {
                    // Otherwise treat as a variable/expression
                    needsFString = true;
                    content.append("{").append(part).append("}");
                }
            }

            String finalContent = content.toString();
            String pyStatement = needsFString
                    ? "print(f\"" + finalContent + "\")"
                    : "print(\"" + finalContent + "\")";
            sb.append(pyStatement);

        }

        if (type != null && type.equals("EqualityExpression")){
            String formatted = value.replace("==", " == ");
            sb.append(formatted);
        }
        if(type != null && type.equals("PostfixExpression")){
            sb.append(value);

        }

        if (type != null && type.equals("MultiplicativeExpression")){
            sb.append(value);
        }
        if (type != null && type.equals("PointerMemberExpression")){
            sb.append(value);
        }

        if (type != null && type.equals("postfixIncrement")){
            sb.append(value).append("+=1");
        }

        if (type != null && type.equals("postfixDecrement")){
            sb.append(value).append("-=1");
        }

        if (type != null && type.equals("prefixDecrement")){
            sb.append(value).append("-=1");
        }

        if (type != null && type.equals("prefixIncrement")){
            sb.append(value).append("+=1");
        }

        return sb.toString();
    }
}
