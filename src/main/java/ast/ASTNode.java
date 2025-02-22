package ast;

public abstract class ASTNode {

    public abstract String toString();
    public abstract String toPython(int indent);
}