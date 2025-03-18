package ast;

import java.util.ArrayList;
import java.util.List;

public class CompoundNode extends ASTNode{

    private List<ASTNode> statements;

    public CompoundNode() {
        this.statements = new ArrayList<ASTNode>();
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public void setStatements(List<ASTNode> statements) {
        this.statements = statements;
    }

    public void add(ASTNode statement) {
        statements.add(statement);
    }

   // body= CompoundNode{statements=[Expression:[ operator: += ] [value: x+=i]
    // children: [Expression: [value: x] children: [LiteralNode( x )]],
    // Expression: [value: i] children: [LiteralNode( i )]]]],
    // Expression:[ type : ShiftExpression ] [value: std::cout<<'Pardon'<<endl]
    // children: [Expression: [value: std::cout] children: [LiteralNode( std::cout )]],
    // Expression: [value: 'Pardon'] children: [LiteralNode( 'Pardon' )]],
    // Expression: [value: endl] children: [LiteralNode( endl )]]]]]}
    // , Expression:[ type : return ] [value: x] children: [LiteralNode( x )]]]}]}
    //    ------------------------

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("CompoundNode{\n");
        for (ASTNode statement : statements) {
            sb.append(statement);
            sb.append("\n");
        }
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toPython(int indent) {

        StringBuilder sb = new StringBuilder();

        for (ASTNode statement : statements) {
            StringBuilder line = new StringBuilder();
            System.out.println("Compound = " + indent);
            line.append(getIndentedPythonCode(indent,statement.toPython(indent)));
            sb.append(line);
        }

        return sb.toString();
    }
}
