package ast;

public abstract class ASTNode {

    public abstract String toString();
    public abstract String toPython(int indent);
    public  String getIndentedPythonCode(int indent, String code){
        return  "\t".repeat(Math.max(0, indent)) + code + "\n";
    }
}