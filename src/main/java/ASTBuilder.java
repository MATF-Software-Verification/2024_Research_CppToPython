import ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ASTBuilder extends CPP14ParserBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitTranslationUnit(CPP14Parser.TranslationUnitContext ctx) {

        List<ASTNode> declarations = new ArrayList<>();

        if (ctx.declarationseq() != null) {
            for (CPP14Parser.DeclarationContext declaration : ctx.declarationseq().declaration()) {
                ASTNode node = visit(declaration);
                if (node != null)
                    declarations.add(node);
            }
        }
        return new TranslationUnitNode(declarations);
    }

    @Override
    public ASTNode visitDeclaration(CPP14Parser.DeclarationContext ctx) {

        if (ctx.functionDefinition() != null) {
            return visitFunctionDefinition(ctx.functionDefinition());
        }

        return null;
    }

    @Override
    public ASTNode visitFunctionDefinition(CPP14Parser.FunctionDefinitionContext ctx) {

        String return_value = ctx.declSpecifierSeq().getText(); //TODO: maybe need to optimize
        DeclaratorNode decl = new DeclaratorNode();
        visitDeclarator(ctx.declarator(), decl);
        FunctionNode functionNode = new FunctionNode(return_value, decl);

        if (ctx.functionBody() != null) {
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

        if (ctx.pointerOperator() != null) {
            StringBuilder sb = new StringBuilder();
            for (CPP14Parser.PointerOperatorContext op : ctx.pointerOperator()) {
                sb.append(op.getText());
            }

            decl.setPointer(sb.toString());
        }
    }

    public void visitNoPointerDeclarator(CPP14Parser.NoPointerDeclaratorContext ctx, DeclaratorNode decl) {

        if (ctx.noPointerDeclarator() != null) {
            visitNoPointerDeclarator(ctx.noPointerDeclarator(), decl);
        }

        if (ctx.parametersAndQualifiers() != null) {
            visitParametersAndQualifiers(ctx.parametersAndQualifiers(), decl);
        }

        if (ctx.declaratorid() != null) {
            decl.setDeclaratorId(ctx.declaratorid().getText());
        }

    }


    public void visitParametersAndQualifiers(CPP14Parser.ParametersAndQualifiersContext ctx, DeclaratorNode decl) {

        if (ctx.parameterDeclarationClause() != null) {
            visitParameterDeclarationClause(ctx.parameterDeclarationClause(), decl);
        }
    }

    private void visitParameterDeclarationClause(CPP14Parser.ParameterDeclarationClauseContext ctx, DeclaratorNode decl) {

        CPP14Parser.ParameterDeclarationListContext paramList = ctx.parameterDeclarationList();
        if (paramList != null) {
            ArrayList<VariableDeclarationNode> list = new ArrayList<>();
            for (CPP14Parser.ParameterDeclarationContext elem : paramList.parameterDeclaration()) {

                String type = elem.declSpecifierSeq().getText();
                DeclaratorNode name = new DeclaratorNode();
                visitDeclarator(elem.declarator(), name);

                if (name.getPointer() != null) {
                    type = type.concat(name.getPointer());
                }
                VariableDeclarationNode vd = new VariableDeclarationNode(type, name);
                list.add(vd);
            }
            decl.setParameters(list);
        }
    }

    /*
        This function adds statements into Function Node without need for CompoundStatementNode (because it is a list)

     */
    public ASTNode visitFunctionBody(CPP14Parser.FunctionBodyContext ctx, FunctionNode functionNode) {
        if (ctx.compoundStatement() != null) {
            if (ctx.compoundStatement().statementSeq() != null) {
                List<CPP14Parser.StatementContext> list = ctx.compoundStatement().statementSeq().statement();
                for (CPP14Parser.StatementContext stmt : list) {
                    ASTNode node = visit(stmt);
                    if (node != null) {
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
            return visitCompoundStatement(ctx.compoundStatement());
        }

        if (ctx.declarationStatement() != null) {

            VariableDeclarationNode variableDeclaration = new VariableDeclarationNode();
            visitDeclarationStatement(ctx.declarationStatement(), variableDeclaration);
            return variableDeclaration;
        }
        if (ctx.expressionStatement() != null) {
            CPP14Parser.ExpressionStatementContext expression = ctx.expressionStatement();
            CPP14Parser.ExpressionContext expr = expression.expression();
            return visitExpression(expr);
        }

        if (ctx.jumpStatement() != null) {
            ExpressionNode expr = (ExpressionNode) visitJumpStatement(ctx.jumpStatement());
            expr.setType("return");
            return expr;
        }

        if (ctx.iterationStatement() != null) {
            IterationNode iterationNode = new IterationNode();
            visitIterationStatement(ctx.iterationStatement(), iterationNode);
            return iterationNode;
        }
        // TODO add other statements
        return null;
    }

    private void visitIterationStatement(CPP14Parser.IterationStatementContext ctx, IterationNode iterationNode) {

        var type = ctx.children.getFirst().getText();
        iterationNode.setType(type);

        if (type.equals("for")) {

            visitForInitStatement(ctx.forInitStatement(), iterationNode);

            if (ctx.condition() != null) {
                ExpressionNode condition = (ExpressionNode) visitExpression(ctx.condition().expression());
                iterationNode.setCondition(condition);
            }
            if (ctx.expression() != null) {
                ExpressionNode exp = (ExpressionNode) visitExpression(ctx.expression());
                iterationNode.setUpdate(exp);
            }

            if (ctx.statement() != null) {

                if (ctx.statement().compoundStatement().statementSeq() != null) {
                    List<CPP14Parser.StatementContext> list = ctx.statement().compoundStatement().statementSeq().statement();
                    for (CPP14Parser.StatementContext stmt : list) {
                        ASTNode node = visit(stmt);
                        if (node != null) {
                            iterationNode.addBody(node);
                        }
                    }
                }
            }
        }
    }


    private void visitForRangeDeclaration(CPP14Parser.ForRangeDeclarationContext ctx, IterationNode iterationNode) {
        //TODO change this
        System.out.println("This is  for range declaration");
        //iterationNode.setRangeDeclaration(new ExpressionNode(ctx.getText()));
    }
    private void visitForRangeInitializer(CPP14Parser.ForRangeInitializerContext ctx, IterationNode iterationNode) {
        //TODO Fix this
        System.out.println("This is  for range initializer: ");
        System.out.println(ctx.getText());
        if(ctx.expression() != null) {
            ExpressionNode expr = (ExpressionNode) visitExpression(ctx.expression());
            iterationNode.setRangeInitializer(expr);
        }

    }
    private void visitForInitStatement(CPP14Parser.ForInitStatementContext ctx, IterationNode iterationNode) {
        if (ctx.expressionStatement() != null) {
            ExpressionNode expr = (ExpressionNode) visitExpression(ctx.expressionStatement().expression());
            iterationNode.setCondition(expr);
        }else if(ctx.simpleDeclaration() != null) {
            VariableDeclarationNode var = new VariableDeclarationNode();
            visitSimpleDeclaration(ctx.simpleDeclaration(),var);
            iterationNode.setInit(var);
        }
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
    public ASTNode visitInitializer(CPP14Parser.InitializerContext ctx) {
        if(ctx.braceOrEqualInitializer() != null) {
            return visitInitializerClause(ctx.braceOrEqualInitializer().initializerClause());
        }
        return null;
    }

    public ASTNode visitInitializerClause(CPP14Parser.InitializerClauseContext ctx) {
        if(ctx.assignmentExpression() != null) {
            return visitAssignmentExpression(ctx.assignmentExpression());
        }else if(ctx.bracedInitList() != null) {
            return visitInitializerList(ctx.bracedInitList().initializerList());
        }
        return null;
    }
    public ASTNode visitInitializerList(CPP14Parser.InitializerListContext ctx) {
        ExpressionNode exp = new ExpressionNode();
        List<CPP14Parser.InitializerClauseContext> list = ctx.initializerClause();

        for(CPP14Parser.InitializerClauseContext elem : list){
            exp.addChild(visitInitializerClause(elem));
        }
        return exp;
    }

    private void visitDeclSpecifierSeq(CPP14Parser.DeclSpecifierSeqContext ctx, VariableDeclarationNode variable) {

        //TODO this should be refactored
        //DeclaratorTypeNode node = DeclaratorTypeNode();
        //visitDeclSpecifier(ctx.declSpecifier(0), variable);
        variable.setType(ctx.declSpecifier(0).getText());
    }

    @Override
    public ASTNode visitJumpStatement(CPP14Parser.JumpStatementContext ctx) {

        ExpressionNode expr = (ExpressionNode) visitExpression(ctx.expression());

        return expr;
    }

    public ASTNode visitExpression(CPP14Parser.ExpressionContext ctx) {
        //TODO check if this should change
        return visitAssignmentExpression(ctx.assignmentExpression(0));
    }

    public ASTNode visitAssignmentExpression(CPP14Parser.AssignmentExpressionContext ctx) {

        if(ctx.conditionalExpression() != null) {
            return visitConditionalExpression(ctx.conditionalExpression());
        }else{
            ExpressionNode left = new ExpressionNode();
            visitLogicalOrExpression(ctx.logicalOrExpression(), left);
            String operator = ctx.assignmentOperator().getText();
            ExpressionNode right = (ExpressionNode) visitInitializerClause(ctx.initializerClause());
            ExpressionNode expr = new ExpressionNode();
            expr.addChild(left);
            expr.addChild(right);
            expr.setOperator(operator);
            return expr;
        }
    }
    public ASTNode visitConditionalExpression(CPP14Parser.ConditionalExpressionContext ctx) {

        ExpressionNode exp = new ExpressionNode();
        if(ctx.logicalOrExpression() != null) {
            visitLogicalOrExpression(ctx.logicalOrExpression(), exp);
            return exp;
        }else{
            return visitAssignmentExpression(ctx.assignmentExpression());
        }
    }



    private <T> void visitExpression_template(
            List<T> list,
            String type,
            String value,
            ExpressionNode expression,
            BiConsumer<T, ExpressionNode> visitor
    ){
        /*
            Template for going through Expressions
         */
        if (list.size() > 1){
            expression.setType(type);
            expression.setValue(value);
            for(T item : list){
                ExpressionNode tmp = new ExpressionNode();
                visitor.accept(item, tmp);
                expression.addChild(tmp);
            }
        }else{
            visitor.accept(list.getFirst(), expression);
        }
    }

    private void visitLogicalOrExpression(CPP14Parser.LogicalOrExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        visitExpression_template(
                ctx.logicalAndExpression(),
                "LogicalOrExpression",
                value,
                expression,
                this::visitLogicalAndExpression
        );
    }
    private void visitLogicalAndExpression(CPP14Parser.LogicalAndExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        visitExpression_template(
                ctx.inclusiveOrExpression(),
                "LogicalAndExpression",
                value,
                expression,
                this::visitInclusiveOrExpression
        );
    }
    private void visitInclusiveOrExpression(CPP14Parser.InclusiveOrExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        visitExpression_template(
                ctx.exclusiveOrExpression(),
                "InclusiveOrExpression",
                value,
                expression,
                this::visitExclusiveOrExpression
        );
    }
    private void visitExclusiveOrExpression(CPP14Parser.ExclusiveOrExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        visitExpression_template(
                ctx.andExpression(),
                "ExclusiveOrExpression",
                value,
                expression,
                this::visitAndExpression
        );
    }
    private void visitAndExpression(CPP14Parser.AndExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        visitExpression_template(
                ctx.equalityExpression(),
                "AndExpression",
                value,
                expression,
                this::visitEqualityExpression
        );
    }

    private void visitEqualityExpression(CPP14Parser.EqualityExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        visitExpression_template(
                ctx.relationalExpression(),
                "EqualityExpression",
                value,
                expression,
                this::visitRelationalExpression
        );
    }
    private void visitRelationalExpression(CPP14Parser.RelationalExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        visitExpression_template(
                ctx.shiftExpression(),
                "RelationalExpression",
                value,
                expression,
                this::visitShiftExpression
        );
    }
    private void visitShiftExpression(CPP14Parser.ShiftExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        visitExpression_template(
                ctx.additiveExpression(),
                "ShiftExpression",
                value,
                expression,
                this::visitAdditiveExpression
        );
    }

    private void visitAdditiveExpression(CPP14Parser.AdditiveExpressionContext ctx, ExpressionNode expression) {
        System.out.println("Visiting AdditiveExpression");
        String value = ctx.getText();
        visitExpression_template(
                ctx.multiplicativeExpression(),
                "AdditiveExpression",
                value,
                expression,
                this::visitMultiplicativeExpression
        );
    }

    private void visitMultiplicativeExpression(CPP14Parser.MultiplicativeExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        visitExpression_template(
                ctx.pointerMemberExpression(),
                "MultiplicativeExpression",
                value,
                expression,
                this::visitPointerMemberExpression
        );
    }
    private void visitPointerMemberExpression(CPP14Parser.PointerMemberExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        visitExpression_template(
                ctx.castExpression(),
                "PointerMemberExpression",
                value,
                expression,
                this::visitCastExpression
        );
    }
    private void visitCastExpression(CPP14Parser.CastExpressionContext ctx, ExpressionNode expression) {
        if(ctx.unaryExpression() != null){
            visitUnaryExpression(ctx.unaryExpression(), expression);
        }
    }
    private void visitUnaryExpression(CPP14Parser.UnaryExpressionContext ctx, ExpressionNode expression) {
        if(ctx.postfixExpression() != null){
            visitPostfixExpression(ctx.postfixExpression(),expression);
        }

        if(ctx.unaryExpression() != null){
            visitUnaryExpression(ctx.unaryExpression(), expression);
        }

        if(ctx.PlusPlus() != null){
            expression.setOperator(ctx.PlusPlus().getText());
            expression.setType("prefixIncrement");
        }

        if (ctx.MinusMinus() != null){
            expression.setOperator(ctx.MinusMinus().getText());
            expression.setType("prefixDecrement");
        }
    }

    private void visitPostfixExpression(CPP14Parser.PostfixExpressionContext ctx, ExpressionNode expression) {

        if(ctx.postfixExpression() != null){
            expression.setType("PostfixExpression");
            expression.setValue(ctx.postfixExpression().getText());

            if(ctx.expressionList() != null){
                visitPrimaryExpression(ctx.primaryExpression(), expression);
            }
        }
        if (ctx.primaryExpression() != null){
            visitPrimaryExpression(ctx.primaryExpression(), expression);
        }

        if (ctx.PlusPlus() != null){
            expression.setOperator(ctx.PlusPlus().getText());
            expression.setType("postfixIncrement");
        }

        if (ctx.MinusMinus() != null){
            expression.setOperator(ctx.MinusMinus().getText());
            expression.setType("postfixDecrement");
        }
    }
    private void visitPrimaryExpression(CPP14Parser.PrimaryExpressionContext ctx, ExpressionNode expression) {

        if(ctx.literal() != null){
            var l = new LiteralNode();
            l.setValue(ctx.getText());

            expression.addChild(l);
        }
    }
}
