package ast;

public class IterationNode extends ASTNode{

    private String type; // "while", "do-while", "for"
    private ExpressionNode condition;
    private ASTNode body;
    private ASTNode init; // basic "for"
    private ExpressionNode update; // basic "for"
    private ASTNode rangeDeclaration; // range-based "for"
    private ExpressionNode rangeInitializer; // range-based "for"

    public IterationNode() {}
    public IterationNode(String type, ExpressionNode condition, ASTNode body) {
        this.type = type;
        this.condition = condition;
        this.body = body;
    }

    public IterationNode(String type, ASTNode init, ExpressionNode condition, ExpressionNode update, ASTNode body) {
        this.type = type;
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    // Constructor for range-based for
    public IterationNode(String type, ASTNode rangeDeclaration, ExpressionNode rangeInitializer, ASTNode body) {
        this.type = type;
        this.rangeDeclaration = rangeDeclaration;
        this.rangeInitializer = rangeInitializer;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    public ASTNode getInit() {
        return init;
    }

    public void setInit(ASTNode init) {
        this.init = init;
    }

    public ExpressionNode getUpdate() {
        return update;
    }

    public void setUpdate(ExpressionNode update) {
        this.update = update;
    }

    public ASTNode getBody() {
        return body;
    }

    public void setBody(ASTNode body) {
        this.body = body;
    }
    public ASTNode getRangeDeclaration() {
        return rangeDeclaration;
    }

    public void setRangeDeclaration(ASTNode rangeDeclaration) {
        this.rangeDeclaration = rangeDeclaration;
    }

    public ExpressionNode getRangeInitializer() {
        return rangeInitializer;
    }

    public void setRangeInitializer(ExpressionNode rangeInitializer) {
        this.rangeInitializer = rangeInitializer;
    }

    @Override
    public String toString() {
        if(type.equals("for")){
            StringBuffer sb = new StringBuffer();
            sb.append("IterationNode{");
            sb.append("type='"+type+"',");
            sb.append("condition="+condition+",");
            sb.append("init="+init+",");
            sb.append("update="+update+",");
            sb.append("rangeDeclaration="+rangeDeclaration+",");
            sb.append("rangeInitializer="+rangeInitializer+",");
            sb.append("}");


            return sb.toString();
        }
        return "Iteration Node:";
    }

    @Override
    public String toPython(int ident) {

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < ident; i++){
            sb.append("\t");
        }


        return "";
    }
}
