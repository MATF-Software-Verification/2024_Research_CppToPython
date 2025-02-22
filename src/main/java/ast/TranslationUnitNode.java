package ast;

import java.util.List;

public class TranslationUnitNode extends ASTNode {
    private final List<ASTNode> declarations;

    public TranslationUnitNode(List<ASTNode> declarations) {
        this.declarations = declarations;
    }

    public List<ASTNode> getDeclarations() {
        return declarations;
    }
    @Override
    public String toString() {
        return "TranslationUnit{" + declarations + "}";
    }
    @Override
    public  String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode declaration : declarations) {
            sb.append(declaration.toPython(indent));
        }
        return sb.toString();
    }
}