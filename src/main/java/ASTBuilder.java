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
        FunctionNode functionNode = new FunctionNode(return_value, decl);

        if(ctx.functionBody() != null) {
            visitFunctionBody(ctx.functionBody(), functionNode);
        }
        return functionNode;
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

    /*
        This function adds statements into Function Node without need for CompoundStatementNode (because it is a list)

     */
    public ASTNode visitFunctionBody(CPP14Parser.FunctionBodyContext ctx, FunctionNode functionNode) {
        if (ctx.compoundStatement() != null) {
            if(ctx.compoundStatement().statementSeq() != null) {
                List<CPP14Parser.StatementContext> list = ctx.compoundStatement().statementSeq().statement();
                for (CPP14Parser.StatementContext stmt : list){
                    ASTNode node = visit(stmt);
                    if( node != null ){
                        functionNode.addBodyNode(node);
                    }
                }
            }
        }
        // TODO check if we need anything else in this function instead of compoundStatement

        return functionNode;
    }

    @Override
    public ASTNode visitStatement(CPP14Parser.StatementContext ctx) {
        if (ctx.compoundStatement() != null) {
            return  visitCompoundStatement(ctx.compoundStatement());
        }

        if (ctx.declarationStatement() != null) {

            VariableDeclarationNode variableDeclaration = new VariableDeclarationNode();
            visitDeclarationStatement(ctx.declarationStatement(), variableDeclaration);
            return variableDeclaration;
        }
        // TODO add other statements
        return null;
    }

    private void visitDeclarationStatement(CPP14Parser.DeclarationStatementContext ctx, VariableDeclarationNode variable) {
        if(ctx.blockDeclaration() != null) {
            visitBlockDeclaration(ctx.blockDeclaration(), variable);
        }
    }

    private void visitBlockDeclaration(CPP14Parser.BlockDeclarationContext ctx, VariableDeclarationNode variable) {
        if(ctx.simpleDeclaration() != null) {
            visitSimpleDeclaration(ctx.simpleDeclaration(), variable);
        }
        //TODO check for other
    }
    private void visitSimpleDeclaration(CPP14Parser.SimpleDeclarationContext ctx, VariableDeclarationNode variable) {

        if(ctx.declSpecifierSeq() != null) {
            visitDeclSpecifierSeq(ctx.declSpecifierSeq(), variable);
        }
        if(ctx.initDeclaratorList() != null) {
            for(var c : ctx.initDeclaratorList().initDeclarator()) {

                DeclaratorNode node = new DeclaratorNode();
                visitDeclarator(c.declarator(), node);
                variable.setName(node);

                if(c.initializer() != null) {
                    var expression = visitInitializer(c.initializer());
                    variable.setExpression(expression);
                }
            }
        }
    }

    private void visitDeclSpecifierSeq(CPP14Parser.DeclSpecifierSeqContext ctx, VariableDeclarationNode variable) {

        //TODO this should be refactored
        //DeclaratorTypeNode node = DeclaratorTypeNode();
        //visitDeclSpecifier(ctx.declSpecifier(0), variable);
        variable.setType(ctx.declSpecifier(0).getText());
    }
}
