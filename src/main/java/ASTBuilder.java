import ast.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends CPP14ParserBaseVisitor<ASTNode>{
    @Override
    public ASTNode visitTranslationUnit(CPP14Parser.TranslationUnitContext ctx) {
        return super.visitTranslationUnit(ctx);
    }
}
