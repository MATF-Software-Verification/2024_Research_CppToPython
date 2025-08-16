package ast;

import ast.codegen.CodegenContext;
import utils.*;

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
    protected String nodeLabel() {
        String t = (type == null ? "" : "type=" + type);
        String op = (operator == null ? "" : ", op=" + operator);
        String v = (value == null ? "" : ", val=" + value);
        return "Expr(" + t + op + v + ")";
    }
    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        for (ASTNode c : children) {
            sb.append(c.toTree(indent + 1));
        }
        return sb.toString();
    }

    private static int prec(String kind) {
        if (kind == null) return 100;
        return switch (kind) {
            case "PrimaryExpression"        -> 100;
            case "PostfixExpression"        -> 90;  // call, index, attribute
            case "MultiplicativeExpression" -> 70;
            case "AdditiveExpression"       -> 60;
            case "ShiftExpression"          -> 50;
            case "RelationalExpression"     -> 40;
            case "EqualityExpression"       -> 30;
            case "AndExpression"            -> 25;  // bitwise &
            case "ExclusiveOrExpression"    -> 22;  // ^
            case "InclusiveOrExpression"    -> 21;  // |
            case "LogicalAndExpression"     -> 15;  // &&
            case "LogicalOrExpression"      -> 10;  // ||
            case "AssignmentExpression"     -> 0;
            default                         -> 100;
        };
    }
    private static String exprString(ASTNode node, int parentPrec) {
        String s = node.toPython(0);
        if (node instanceof ExpressionNode en) {
            int cp = prec(en.getType());
            if (cp < parentPrec) return "(" + s + ")";
        }
        return s;
    }
    private static String joinWith(List<ASTNode> kids, String op, int parentPrec) {
        List<String> parts = new ArrayList<>(kids.size());
        for (ASTNode k : kids) parts.add(exprString(k, parentPrec));
        String glue = " " + op + " ";
        return String.join(glue, parts);
    }

    private List<String> childExprs(int parentPrec) {
        return children.stream().map(c -> exprString(c, parentPrec)).collect(Collectors.toList());
    }

    @Deprecated
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

        String leaf = renderLeaf();
        if (leaf != null) return leaf;

        if (children.size() == 1) {
            if (type != null) {
                switch (type) {
                    case "PostfixIncrement":
                    case "PostfixDecrement":
                        return value == null ? "" : value;

                    case "PointerMemberExpression": {
                        String conv = ConvertFunctionCall.convert(value == null ? "" : value);
                        return conv;
                    }

                    case "PrefixExpression":
                        return value == null ? "" : value;

                    case "PostfixExpression": {
                        return children.get(0).toPython(0);
                    }
                    case "PrimaryExpression":
                    {
                        if("this".equals(value)){
                            return "self";
                        }
                    }

                    default:
                        return children.get(0).toPython(0);
                }
            }
            return children.get(0).toPython(0);
        }

        // Multi-child
        if (type != null) {
            switch (type) {
                case "AssignmentExpression": {
                    List<String> parts = childExprs(prec("AssignmentExpression"));
                    if (operator != null && parts.size() == 2) {
                        String left  = (children.size() > 0) ? children.get(0).toPython(0) : "";
                        String right = (children.size() > 1) ? children.get(1).toPython(0) : "";
                        String op = mapAssignmentOperator(operator);
                        return left + " " + op + " " + right;
                    }
                    return String.join(" ", parts);
                }

                case "AdditiveExpression": {
                    String op = mapAssignmentOperator(operator);
                    return joinWith(children, op, prec("AdditiveExpression"));
                }

                case "MultiplicativeExpression": {
                    String op = mapAssignmentOperator(operator);
                    return joinWith(children, op, prec("MultiplicativeExpression"));
                }

                case "ShiftExpression": {
                    List<String> parts = childExprs(prec("ShiftExpression"));
                    if (!parts.isEmpty()) {
                        String first = parts.get(0).trim();
                        String key = ConvertFunctionCall.convert(first);
                        if (ConvertFunctionCall.hasValue(first) && FunctionRegistry.hasHandler(key)) {
                            return FunctionRegistry.handle(key, parts);
                        } else if (ConvertFunctionCall.hasValue(first)) {
                            String args = parts.size() > 1 ? String.join(", ", parts.subList(1, parts.size())) : "";
                            return key + "(" + args + ")";
                        }
                    }
                    String op = ConvertOperator.convert(operator == null ? "<<" : operator);
                    return joinWith(children, op, prec("ShiftExpression"));
                }

                case "RelationalExpression": {
                    String op = ConvertOperator.convert(operator == null ? "<" : operator);
                    return joinWith(children, op, prec("RelationalExpression"));
                }

                case "EqualityExpression": {
                    String op = ConvertOperator.convert(operator == null ? "==" : operator);
                    return joinWith(children, op, prec("EqualityExpression"));
                }

                case "LogicalAndExpression": {
                    String op = ConvertOperator.convert("&&");
                    return joinWith(children, op, prec("LogicalAndExpression"));
                }

                case "LogicalOrExpression": {
                    String op = ConvertOperator.convert("||");
                    return joinWith(children, op, prec("LogicalOrExpression"));
                }

                case "InitializerList": {
                    List<String> parts = childExprs(100);
                    return String.join(", ", parts);
                }

                case "Constructor": {
                    if (children.size() > 1) return "self." + children.get(1).toPython(0);
                    return value == null ? "" : value;
                }

                case "PostfixExpression": {
                    if (children.size() == 2
                            && children.get(0) instanceof ExpressionNode en0
                            && "PostfixExpression".equals(en0.getType())
                            && en0.getChildren().size() >= 2
                            && en0.getChildren().get(1) instanceof LiteralNode lit
                            && children.get(1) instanceof ExpressionNode argsEn
                            && "LIST".equals(((ExpressionNode) children.get(1)).getType())) {

                        String base   = en0.getChildren().get(0).toPython(0);
                        String member = ((LiteralNode) en0.getChildren().get(1)).getValue();
                        String args   = stripParens(((ExpressionNode) children.get(1)).getValue());

                        String mapped = ConvertFunctionCall.convert(member == null ? "" : member);

                        if ("len".equals(mapped)) {
                            return "len(" + base + ")";
                        }

                        return base + "." + mapped + "(" + args + ")";
                    }

                    StringBuilder out = new StringBuilder();
                    out.append(children.get(0).toPython(0));
                    for (int i = 1; i < children.size(); i++) {
                        ASTNode ch = children.get(i);

                        if (ch instanceof LiteralNode lit) {
                            out.append(".").append(lit.getValue());
                            continue;
                        }
                        if (ch instanceof ExpressionNode en) {
                            String t = en.getType();
                            String v = en.getValue();
                            if ("LIST".equals(t) || "LIST_IDX".equals(t)) {
                                out.append(v == null ? "" : v);
                                continue;
                            }
                        }
                        out.append(ch.toPython(0));
                    }
                    return out.toString();
                }
            }
        }

        if (operator != null) {
            String op = ConvertOperator.convert(operator);
            return joinWith(children, op, 50);
        }
        return children.get(0).toPython(0);
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String s = toPython(indent);
        if (s != null && !s.isEmpty()) ctx.out.writeln(s);
        return "";
    }

    private static String normalizePrimary(String v) {
        if (v == null) return "";
        if ("this".equals(v)) return "self";
        if ("true".equals(v)) return "True";
        if ("false".equals(v)) return "False";
        if ("nullptr".equals(v) || "NULL".equals(v)) return "None";
        return v;
    }

    private static String mapAssignmentOperator(String op) {
        if (op == null) return "=";
        switch (op) {
            case "=":   case "+=": case "-=": case "*=": case "/=": case "%=":
            case "<<=": case ">>=": case "&=": case "^=": case "|=":
                return op;
            default:
                return op;
        }
    }
    private String renderLeaf() {
        if (!children.isEmpty()) return null;

        if (type == null) {
            return value == null ? "" : value;
        }

        switch (type) {
            case "PrimaryExpression":
                return normalizePrimary(value);

            case "LIST":
            case "LIST_IDX":
            case "NormalFunction":
            case "PostfixIncrement":
            case "PostfixDecrement":
                return value == null ? "" : value;

            case "PointerMemberExpression":
                return ConvertFunctionCall.convert(value == null ? "" : value);

            default:
                return value == null ? "" : value;
        }
    }
    private static String stripParens(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.length() >= 2 && s.charAt(0) == '(' && s.charAt(s.length()-1) == ')') {
            return s.substring(1, s.length()-1).trim();
        }
        return s;
    }
}
