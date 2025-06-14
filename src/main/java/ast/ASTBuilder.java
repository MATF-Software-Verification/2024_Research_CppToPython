package ast;

import antlr.CPP14Parser;
import antlr.CPP14ParserBaseVisitor;
import ast.condition.SelectionNode;
import ast.iteration.ForNode;
import ast.iteration.ForRangeNode;
import ast.iteration.WhileNode;

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
            return visitFunctionDefinition(ctx.functionDefinition(), false );
        }
        else if(ctx.blockDeclaration() != null) {
            VariableDeclarationNode variableDeclaration = new VariableDeclarationNode();
            visitBlockDeclaration(ctx.blockDeclaration(), variableDeclaration);
            return variableDeclaration;
        }

        return null;
    }


    public ASTNode visitFunctionDefinition(CPP14Parser.FunctionDefinitionContext ctx, boolean from_class) {

        String return_value = "";
        if(ctx.declSpecifierSeq() != null) {
            return_value = ctx.declSpecifierSeq().getText();
        }
        DeclaratorNode decl = new DeclaratorNode();
        visitDeclarator(ctx.declarator(), decl);
        FunctionNode functionNode = new FunctionNode(return_value, decl,from_class);

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
            CompoundNode cn = visitCompoundStatement(ctx.compoundStatement());
            functionNode.setBody(cn);
        }

        if (ctx.constructorInitializer() != null) {
            var oldName = functionNode.getFunc_declarator();
            oldName.setDeclaratorId("__init__");

            var memList = ctx.constructorInitializer().memInitializerList();
            CompoundNode cn = new CompoundNode();
            for (var mem : memList.memInitializer()){
                DeclaratorNode decl = new DeclaratorNode();
                decl.setDeclaratorId("self."+mem.meminitializerid().getText());

                ExpressionNode expr = new ExpressionNode();
                expr.setValue(mem.expressionList().getText());

                VariableDeclarationNode vd = new VariableDeclarationNode();
                vd.setName(decl);
                vd.setExpression(expr);

                cn.add(vd);
            }

            functionNode.setBody(cn);

        }

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
            ASTNode statement = visitJumpStatement(ctx.jumpStatement());
//            if (statement instanceof ExpressionNode) {
//                ExpressionNode expr = (ExpressionNode) visitJumpStatement(ctx.jumpStatement());
//                expr.setType("return");
//            }
            return statement;
        }

        if (ctx.iterationStatement() != null) {
            return visitIterationStatement(ctx.iterationStatement());
        }

        if (ctx.selectionStatement() != null) {
            return visitSelectionStatement(ctx.selectionStatement());
        }
        // TODO add other statements
        return null;
    }

    @Override
    public CompoundNode visitCompoundStatement(CPP14Parser.CompoundStatementContext ctx) {

        CompoundNode cn = new CompoundNode();

        if (ctx.statementSeq() != null) {
            List<CPP14Parser.StatementContext> stmts = ctx.statementSeq().statement();
            for (CPP14Parser.StatementContext stmt : stmts) {
                ASTNode node = visit(stmt);
                if (node != null) {
                    cn.add(node);
                }
            }
        }

        return cn;
    }

    @Override
    public ASTNode visitIterationStatement(CPP14Parser.IterationStatementContext ctx) {

        var type = ctx.children.getFirst().getText();
        if (type.equals("for")) {
            ForNode forNode = new ForNode();
            if(ctx.forInitStatement() != null) {
                visitForInitStatement(ctx.forInitStatement(), forNode);
                if(ctx.condition() != null) {
                    visitCondition(ctx.condition(),forNode);
                }
                if(ctx.expression() != null) {
                    ASTNode expr = visitExpression(ctx.expression());
                    forNode.setExpression(expr);
                }
                forNode.setBody(visitStatement(ctx.statement()));

            }else{
                ForRangeNode forRangeNode = new ForRangeNode();
                visitForRangeDeclaration(ctx.forRangeDeclaration(), forNode);
                visitForRangeInitializer(ctx.forRangeInitializer(), forRangeNode);
            }
            return forNode;
        }
        if(type.equals("while")) {
            WhileNode whileNode = new WhileNode();
            visitCondition(ctx.condition(),whileNode);
            whileNode.setBody(visitStatement(ctx.statement()));

            return whileNode;
        }
        return null;
    }

    private void visitCondition(CPP14Parser.ConditionContext ctx, ForNode forNode) {

        if(ctx.expression() != null) {
            ASTNode expr = visitExpression(ctx.expression());
            forNode.setCondition(expr);
        }
    }
    private void visitCondition(CPP14Parser.ConditionContext ctx, WhileNode whileNode) {

        if(ctx.expression() != null) {
            ASTNode expr = visitExpression(ctx.expression());
            whileNode.setCondition(expr);
        }
    }


    private void visitForRangeDeclaration(CPP14Parser.ForRangeDeclarationContext ctx, ForNode forNode) {
        //TODO change this
        System.out.println("This is  for range declaration");
        //iterationNode.setRangeDeclaration(new ExpressionNode(ctx.getText()));
    }
    private void visitForRangeInitializer(CPP14Parser.ForRangeInitializerContext ctx, ForRangeNode forNode) {
        //TODO Fix this
        System.out.println("This is  for range initializer: ");
        System.out.println(ctx.getText());
        if(ctx.expression() != null) {
            ExpressionNode expr = (ExpressionNode) visitExpression(ctx.expression());
            forNode.setRangeInitializer(expr);
        }

    }
    private void visitForInitStatement(CPP14Parser.ForInitStatementContext ctx, ForNode forNode) {
        if (ctx.expressionStatement() != null) {
            System.out.println("Setting condition");
            ExpressionNode expr = (ExpressionNode) visitExpression(ctx.expressionStatement().expression());
            forNode.setInitialStatement(expr);
        }else if(ctx.simpleDeclaration() != null) {
            System.out.println("Setting simple declaration");
            VariableDeclarationNode var = new VariableDeclarationNode();
            visitSimpleDeclaration(ctx.simpleDeclaration(),var);
            forNode.setInitialStatement(var);
        }
    }

    @Override
    public ASTNode visitSelectionStatement(CPP14Parser.SelectionStatementContext ctx) {

        String type = ctx.children.getFirst().getText();
        ExpressionNode condition = (ExpressionNode)visitExpression(ctx.condition().expression());
        ASTNode then = visitStatement(ctx.statement().getFirst());

        SelectionNode selection = new SelectionNode(type, condition, then);

        if (ctx.Else() != null) {
            ASTNode elseNode = visitSelectionStatementLevels(ctx.statement().get(1), 1);
            selection.setElseBranch(elseNode);
        }
        return selection;
    }

    private ASTNode visitSelectionStatementLevels(CPP14Parser.StatementContext ctx, int lvl){

        SelectionNode selection = new SelectionNode();

        if (ctx.selectionStatement() != null) {
            CPP14Parser.SelectionStatementContext context = ctx.selectionStatement();
            ExpressionNode condition = (ExpressionNode)visitExpression(context.condition().expression());
            System.out.println(condition);
            selection.setType("elseif");
            selection.setCondition(condition);
            if(context.statement() != null) {
                ASTNode then = visitStatement(context.statement().getFirst());
                selection.setThenBranch(then);
            }
            if(context.Else() != null) {
                ASTNode elseNode = visitSelectionStatementLevels(context.statement().get(1),lvl+1);
                selection.setElseBranch(elseNode);
            }
        }else{
            selection.setType("else");
            selection.setCondition(null);
            selection.setThenBranch(visitStatement(ctx));
            return selection;
        }

        return selection;
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
        if (ctx.expressionList() != null) {
            // expressionList basically has only `initializerList`
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
        exp.setValue(ctx.getText());

        List<CPP14Parser.InitializerClauseContext> list = ctx.initializerClause();

        for(CPP14Parser.InitializerClauseContext elem : list){
            exp.addChild(visitInitializerClause(elem));
        }
        return exp;
    }

    public void visitTrailingTypeSpecifier(CPP14Parser.TrailingTypeSpecifierContext ctx, VariableDeclarationNode variable) {
        if (ctx.simpleTypeSpecifier() != null) {

        }
    }

    private void visitDeclSpecifierSeq(CPP14Parser.DeclSpecifierSeqContext ctx, VariableDeclarationNode variable) {

        //TODO this should be refactored
        //DeclaratorTypeNode node = DeclaratorTypeNode();
        //visitDeclSpecifier(ctx.declSpecifier(0), variable);
        for( CPP14Parser.DeclSpecifierContext item : ctx.declSpecifier()){
            if(item.typeSpecifier() != null) {

                if(item.typeSpecifier().classSpecifier() != null) {
                    variable.setType("class");
                    ClassNode myClass = new ClassNode();
                    visitClassNode(item.typeSpecifier().classSpecifier(), myClass);
                    variable.setExpression(myClass);
                }

                if(item.typeSpecifier().trailingTypeSpecifier() != null) {
                    // setting the variable type here.
                    visitTrailingTypeSpecifier(item.typeSpecifier().trailingTypeSpecifier(), variable);
                }
            }

        }
        if(variable.getType() == null) {
            variable.setType(ctx.declSpecifier(0).getText());
        }

    }

    public void visitClassNode(CPP14Parser.ClassSpecifierContext ctx, ClassNode mclass ){

        String name = ctx.classHead().classHeadName().getText(); //TODO // done
        List<ASTNode> params = new ArrayList<>();
        mclass.setClassName(name);
        for(CPP14Parser.MemberdeclarationContext member : ctx.memberSpecification().memberdeclaration()){

            if(member.functionDefinition()!=null){
                ASTNode func = visitFunctionDefinition(member.functionDefinition(),true);
                if(func!=null){

                    params.add(func);
                }
            }
            else if(member.memberDeclaratorList() != null){
                ASTNode decl = visitMemberDeclaratorList(member.memberDeclaratorList());
                if(decl!=null){
                    params.add(decl);
                }
            }
        }
        mclass.setMembers(params);

    }

    @Override
    public ASTNode visitJumpStatement(CPP14Parser.JumpStatementContext ctx) {

        if (ctx.Return() != null) {
            TermNode term = new TermNode("return");

            if (ctx.expression() != null) {
                ASTNode expression = visitExpression(ctx.expression());
                term.setExpression(expression);
            }

            return term;
        }

        if(ctx.Break() != null) {

            return new TermNode("break");
        }

        return null;
    }

    public ASTNode visitExpression(CPP14Parser.ExpressionContext ctx) {
        //TODO check if this should change
        if(ctx.assignmentExpression() != null) {
            return visitAssignmentExpression(ctx.assignmentExpression(0));
        }
        return null;
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
            expr.setValue(ctx.getText());
            return expr;
        }
    }
    public ASTNode visitConditionalExpression(CPP14Parser.ConditionalExpressionContext ctx) {

        ExpressionNode exp = new ExpressionNode();
        if(ctx.logicalOrExpression() != null) {
            exp.setValue(ctx.logicalOrExpression().getText());
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
            String operator,
            ExpressionNode expression,
            BiConsumer<T, ExpressionNode> visitor
    ){
        /*
            Template for going through Expressions
         */
        if (list.size() > 1){
            expression.setType(type);
            expression.setValue(value);
            expression.setOperator(operator);
            for(T item : list){
                ExpressionNode tmp = new ExpressionNode();
                visitor.accept(item, tmp);
                expression.addChild(tmp);
            }
        }else{
            expression.setType(type);
            visitor.accept(list.getFirst(), expression);
        }
    }

    private void visitLogicalOrExpression(CPP14Parser.LogicalOrExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        String operator = !ctx.OrOr().isEmpty() ? ctx.OrOr().getFirst().getText() : null;
        visitExpression_template(
                ctx.logicalAndExpression(),
                "LogicalOrExpression",
                value,
                operator,
                expression,
                this::visitLogicalAndExpression
        );
    }
    private void visitLogicalAndExpression(CPP14Parser.LogicalAndExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        String operator = !ctx.AndAnd().isEmpty() ? ctx.AndAnd().getFirst().getText() : null;
        visitExpression_template(
                ctx.inclusiveOrExpression(),
                "LogicalAndExpression",
                value,
                operator,
                expression,
                this::visitInclusiveOrExpression
        );
    }
    private void visitInclusiveOrExpression(CPP14Parser.InclusiveOrExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        String operator = !ctx.Or().isEmpty() ? ctx.Or().getFirst().getText() : null;
        visitExpression_template(
                ctx.exclusiveOrExpression(),
                "InclusiveOrExpression",
                value,
                operator,
                expression,
                this::visitExclusiveOrExpression
        );
    }
    private void visitExclusiveOrExpression(CPP14Parser.ExclusiveOrExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        String operator = !ctx.Caret().isEmpty() ? ctx.Caret().getFirst().getText() : null;
        visitExpression_template(
                ctx.andExpression(),
                "ExclusiveOrExpression",
                value,
                operator,
                expression,
                this::visitAndExpression
        );
    }
    private void visitAndExpression(CPP14Parser.AndExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        String operator = !ctx.And().isEmpty() ? ctx.And().getFirst().getText() : null;
        visitExpression_template(
                ctx.equalityExpression(),
                "AndExpression",
                value,
                operator,
                expression,
                this::visitEqualityExpression
        );
    }

    private void visitEqualityExpression(CPP14Parser.EqualityExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        String operator = null;
        if (!ctx.Equal().isEmpty())
            operator = ctx.Equal().getFirst().getText();
        else if (!ctx.NotEqual().isEmpty())
            operator = ctx.NotEqual().getFirst().getText();

        visitExpression_template(
                ctx.relationalExpression(),
                "EqualityExpression",
                value,
                operator,
                expression,
                this::visitRelationalExpression
        );
    }
    private void visitRelationalExpression(CPP14Parser.RelationalExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        String operator = null;
        if (!ctx.Less().isEmpty())
            operator = ctx.Less().getFirst().getText();
        else if (!ctx.LessEqual().isEmpty())
            operator = ctx.LessEqual().getFirst().getText();
        else if (!ctx.Greater().isEmpty())
            operator = ctx.Greater().getFirst().getText();
        else if (!ctx.GreaterEqual().isEmpty())
            operator = ctx.GreaterEqual().getFirst().getText();
        visitExpression_template(
                ctx.shiftExpression(),
                "RelationalExpression",
                value,
                operator,
                expression,
                this::visitShiftExpression
        );
    }
    private void visitShiftExpression(CPP14Parser.ShiftExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        String operator = !ctx.shiftOperator().isEmpty() ? ctx.shiftOperator().getFirst().getText() : null;
        visitExpression_template(
                ctx.additiveExpression(),
                "ShiftExpression",
                value,
                operator,
                expression,
                this::visitAdditiveExpression
        );
    }

    private void visitAdditiveExpression(CPP14Parser.AdditiveExpressionContext ctx, ExpressionNode expression) {
        String value = ctx.getText();
        String operator = null;
        if (!ctx.Minus().isEmpty())
            operator = ctx.Minus().getFirst().getText();
        else if (!ctx.Plus().isEmpty())
            operator = ctx.Plus().getFirst().getText();
        visitExpression_template(
                ctx.multiplicativeExpression(),
                "AdditiveExpression",
                value,
                operator,
                expression,
                this::visitMultiplicativeExpression
        );
    }

    private void visitMultiplicativeExpression(CPP14Parser.MultiplicativeExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        String operator = null;
        if (!ctx.Div().isEmpty())
            operator = ctx.Div().getFirst().getText();
        else if (!ctx.Mod().isEmpty())
            operator = ctx.Mod().getFirst().getText();
        else if (!ctx.Star().isEmpty())
            operator = ctx.Star().getFirst().getText();
        visitExpression_template(
                ctx.pointerMemberExpression(),
                "MultiplicativeExpression",
                value,
                operator,
                expression,
                this::visitPointerMemberExpression
        );
    }
    private void visitPointerMemberExpression(CPP14Parser.PointerMemberExpressionContext ctx, ExpressionNode expression) {

        String value = ctx.getText();
        String operator = null;
        if (!ctx.DotStar().isEmpty())
            operator = ctx.DotStar().getFirst().getText();
        else if (!ctx.ArrowStar().isEmpty())
            operator = ctx.ArrowStar().getFirst().getText();
        visitExpression_template(
                ctx.castExpression(),
                "PointerMemberExpression",
                value,
                operator,
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
            expression.setValue(ctx.unaryExpression().getText());
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
            if(ctx.postfixExpression().Dot() != null){

                String s = ctx.postfixExpression().idExpression().getText();
                String e = ctx.postfixExpression().postfixExpression().getText();
                expression.setValue(s+"("+e+")");
            }
            else{
                expression.setValue(ctx.getText());
            }

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

        if(ctx!= null) {
            expression.setValue(ctx.getText());
            if (ctx.literal() != null) {
                var l = new LiteralNode();
                l.setValue(ctx.getText());

                expression.addChild(l);
            }
        }
    }
}
