package ast;

import utils.ClassStorage;
import utils.ConvertFunctionCall;
import utils.ConvertOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private List<String> getChildrenStrings(List<ASTNode> childrens, int indent){
        List<String> childStrings = children.stream()
                .map(node -> {
                    if (node instanceof ExpressionNode) {
                        return ((ExpressionNode) node).toPython(indent);
                    } else {
                        return node.toPython(indent);
                    }
                })
                .collect(Collectors.toList());
        return childStrings;
    }

    @Override
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        if (children.isEmpty()) {
            return sb.append(value).toString();
        }
        if (this.children.size() == 1) {
            if(type != null){
                if(type.equals("NormalFunction")){
                    sb.append(value);
                    return sb.toString();
                }
                if(type.equals("PointerMemberExpression")){
                    String formated = ConvertFunctionCall.convert(value);
                    sb.append(formated).append(" ");
                    return sb.toString();
                }
                if(this.type.equals("PostfixExpression")){
                    sb.append(children.get(0).toPython(indent));
                    return sb.toString();
                }
                if(type.equals("LIST_IDX")){
                    sb.append(value);
                    return sb.toString();
                }
            }
            String childString = this.children.get(0).toPython(indent);
            sb.append(childString);

            return sb.toString();
        }
        if(type != null){
            if(type.equals("AdditiveExpression")){
                List<String> childStrings = getChildrenStrings(children, indent);
                if (this.operator != null && !childStrings.isEmpty()) {
                    String operatorString = ConvertOperator.convert(this.operator);
                    System.out.println("+=====[ "+this.operator + " ]====== "+operatorString);
                    sb.append(String.join(operatorString, childStrings));
                }
                return sb.toString();
            }
            if(type.equals("ShiftExpression")){
                List<String> childStrings = getChildrenStrings(children, indent);
                System.out.println(childStrings);
                if(!childStrings.isEmpty() && ConvertFunctionCall.hasValue(childStrings.get(0).toString().trim())){
                    sb.append(childStrings.get(0));
                    sb.append("(");
                    sb.append(String.join(",", childStrings.subList(1, childStrings.size()-1)));
                    sb.append(")");
                }
                return sb.toString();

            }
            if(type.equals("RelationalExpression")){
                List<String> childStrings = getChildrenStrings(children, indent);
                if (this.operator != null && !childStrings.isEmpty()) {
                    String operatorString = ConvertOperator.convert(this.operator);
                    System.out.println("+=====[ "+this.operator + " ]====== "+operatorString);
                    sb.append(String.join(operatorString, childStrings));
                }
                return sb.toString();
            }
            if(this.type.equals("PostfixExpression")){

                if(children.size()> 1){
                    sb.append(value);
                }
                return sb.toString();
            }
            if(type.equals("InitializerList")){
                List<String> childStrings = getChildrenStrings(children, indent);
                sb.append(String.join(",", childStrings));
                return sb.toString();
            }
        }

        if (this.type != null && this.type.equals("NormalFunction")){
            System.out.println("THIS IS NORMAL:" + this.getChildren().toString());
            sb.append(this.getValue());
            return sb.toString();
        }
        List<String> childStrings = children.stream()
                .map(node -> {
                    if (node instanceof ExpressionNode) {
                        return ((ExpressionNode) node).toPython(indent);
                    } else {
                        return node.toPython(indent);
                    }
                })
                .collect(Collectors.toList());


        if (this.operator != null && !childStrings.isEmpty()) {
            String operatorString = ConvertOperator.convert(this.operator);
            System.out.println("+=====[ "+this.operator + " ]====== "+operatorString);
            sb.append(String.join(operatorString, childStrings));
        }
        else if (!childStrings.isEmpty()) {
            sb.append(childStrings.get(0));
        }

        return sb.toString();
    }



}
