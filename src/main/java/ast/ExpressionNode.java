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
    public String toPython(int ident) {


        StringBuilder sb = new StringBuilder();
        if(type == null && operator == null){

            for(int i=0; i < children.size(); i++){
                sb.append(children.get(i).toPython(0));
            }
        }
        if(type != null && type.equals("return")){
            for(int i=0; i<ident; i++){
                sb.append("\t");
            }
            sb.append("return ");

            if(operator == null){
                for(int i=0; i < children.size(); i++){
                    sb.append(children.get(i).toPython(0));
                }
            }
        }
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
            String formatted = value.replace("std::cout<<", "").replace("<<std::endl", "").trim();
            formatted = formatted.replace("\\s*<<\\s*", ", ");

            String pyStatement = "print(" + formatted + ")";
            sb.append(pyStatement);

        }

        if (type != null && type.equals("EqualityExpression")){
            String formatted = value.replace("==", " == ");
            sb.append(formatted);
        }
        return sb.toString();
    }
}
