import ast.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends CPP14ParserBaseVisitor<ASTNode>{
    @Override
    public ASTNode visitTranslationUnit(CPP14Parser.TranslationUnitContext ctx) {

        List<ASTNode> declarations = new ArrayList<>();

        if (ctx.declarationseq() != null) {
            for (CPP14Parser.DeclarationContext declaration : ctx.declarationseq().declaration()){
                ASTNode node = visit(declaration);
                if (node != null)
                    declarations.add(node);
            }
        }
        return new TranslationUnitNode(declarations);
    }

    @Override
    public ASTNode visitDeclaration(CPP14Parser.DeclarationContext ctx) {

        if(ctx.functionDefinition() != null) {
            return visitFunctionDefinition(ctx.functionDefinition());
        }

        return null;
    }

    @Override
    public ASTNode visitFunctionDefinition(CPP14Parser.FunctionDefinitionContext ctx) {

        String return_value = ctx.declSpecifierSeq().getText();
        DeclaratorNode decl = new DeclaratorNode();
        visitDeclarator(ctx.declarator(), decl);

        if(ctx.functionBody() != null) {

        }
        return super.visitFunctionDefinition(ctx);
    }

    public void visitDeclarator(CPP14Parser.DeclaratorContext ctx, DeclaratorNode decl) {

        if (ctx.noPointerDeclarator() != null) {
            visitNoPointerDeclarator(ctx.noPointerDeclarator(), decl);
        }

        if (ctx.pointerDeclarator() != null) {
            visitPointerDeclarator(ctx.pointerDeclarator(), decl);
        }
    }

    public void visitPointerDeclarator(CPP14Parser.PointerDeclaratorContext ctx, DeclaratorNode decl) {

        if (ctx.noPointerDeclarator() != null) {
            visitNoPointerDeclarator(ctx.noPointerDeclarator(), decl);
        }

    }

    public void visitNoPointerDeclarator(CPP14Parser.NoPointerDeclaratorContext ctx, DeclaratorNode decl) {

        if (ctx.noPointerDeclarator() != null) {
            visitNoPointerDeclarator(ctx.noPointerDeclarator(), decl);
        }

        if (ctx.parametersAndQualifiers() != null) {

        }

        if (ctx.declaratorid() != null) {
            decl.setDeclaratorId(ctx.declaratorid().getText());
        }

    }


    public void visitParametersAndQualifiers(CPP14Parser.ParametersAndQualifiersContext ctx, DeclaratorNode decl) {
        // TODO params;
    }

    @Override
    public ASTNode visitFunctionBody(CPP14Parser.FunctionBodyContext ctx) {

        if (ctx.compoundStatement() != null) {
            return visitCompoundStatement(ctx.compoundStatement());
        }

        return null;
    }
}
