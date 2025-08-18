package ast;

import antlr.CPP14Parser;
import antlr.CPP14ParserBaseVisitor;
import ast.condition.SelectionNode;
import ast.functions.FunctionNode;
import ast.iteration.ForNode;
import ast.iteration.ForRangeNode;
import ast.iteration.WhileNode;
import org.antlr.v4.codegen.model.decl.Decl;
import utils.ClassStorage;
import utils.ConvertFunctionCall;
import utils.ConvertOperator;
import utils.FunctionStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
            return visitFunctionDefinition(ctx.functionDefinition(), false ,"");
        }
        else if(ctx.blockDeclaration() != null) {
            VariableDeclarationNode variableDeclaration = new VariableDeclarationNode();
            visitBlockDeclaration(ctx.blockDeclaration(), variableDeclaration);
            return variableDeclaration;
        }
        // TODO: Missing templateDeclaration, explicitInstantiation, explicitSpecialization
        // TODO: LinkageSpecification, namespaceDefinition, emptyDeclaration_, attributeDeclaration
        return null;
    }


    public ASTNode visitFunctionDefinition(CPP14Parser.FunctionDefinitionContext ctx, boolean from_class, String class_name) {

        String return_value = "";
        if(ctx.declSpecifierSeq() != null) {
            return_value = ctx.declSpecifierSeq().getText();
        }
        DeclaratorNode decl = new DeclaratorNode();
        visitDeclarator(ctx.declarator(), decl);

//        if(from_class) {
//            ClassStorage.getInstance().addFunction(class_name, decl.getDeclaratorId());
//        }
//        FunctionStorage.getInstance().addFunction(decl.getDeclaratorId());

        FunctionNode functionNode = new FunctionNode(return_value, decl, from_class);

        if (from_class && decl.getDeclaratorId() != null && decl.getDeclaratorId().equals(class_name)) {
            functionNode.setConstructor(true);
            functionNode.setOwnerClassName(class_name);
        }

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

        if (ctx.declaratorid() != null) {
            decl.setDeclaratorId(ctx.declaratorid().getText());
        }

        if (ctx.noPointerDeclarator() != null) {

            visitNoPointerDeclarator(ctx.noPointerDeclarator(), decl);
            if (ctx.LeftBracket() != null) {
                decl.setDeclaratorId(decl.getDeclaratorId() + "[]");
            }
        }

        if (ctx.parametersAndQualifiers() != null) {
            visitParametersAndQualifiers(ctx.parametersAndQualifiers(), decl);
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
            var memList = ctx.constructorInitializer().memInitializerList();
            CompoundNode cn = new CompoundNode();
            for (var mem : memList.memInitializer()) {
                DeclaratorNode d = new DeclaratorNode();
                d.setDeclaratorId("self." + mem.meminitializerid().getText());

                ExpressionNode expr = new ExpressionNode();
                expr.setValue(mem.expressionList() != null ? mem.expressionList().getText() : "None");

                VariableDeclarationNode vd = new VariableDeclarationNode();
                vd.setName(d);
                vd.setExpression(expr);

                cn.add(vd);
            }
            functionNode.setBody(cn);
        }


        //TODO: look at functionTryBlock

        return functionNode;
    }


    @Override
    public ASTNode visitStatement(CPP14Parser.StatementContext ctx) {

        if (ctx.labeledStatement() != null) {
            //TODO: look at this
        }
        else if (ctx.declarationStatement() != null) {

            VariableDeclarationNode variableDeclaration = new VariableDeclarationNode();
            visitDeclarationStatement(ctx.declarationStatement(), variableDeclaration);
            return variableDeclaration;
        }
        else{
            //TODO; attributeSpecifierSeq?
            if (ctx.expressionStatement() != null) {

                CPP14Parser.ExpressionStatementContext expression = ctx.expressionStatement();
                CPP14Parser.ExpressionContext expr = expression.expression();

                return visitExpression(expr);

            }else if(ctx.compoundStatement() != null) {

                return visitCompoundStatement(ctx.compoundStatement());

            }else if (ctx.iterationStatement() != null) {

                return visitIterationStatement(ctx.iterationStatement());

            }else if (ctx.selectionStatement() != null) {

                return visitSelectionStatement(ctx.selectionStatement());

            }
            else if (ctx.jumpStatement() != null) {
                ASTNode statement = visitJumpStatement(ctx.jumpStatement());
//            if (statement instanceof ExpressionNode) {
//                ExpressionNode expr = (ExpressionNode) visitJumpStatement(ctx.jumpStatement());
//                expr.setType("return");
//            }
                return statement;
            }

        }




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
            ForRangeNode forRangeNode = new ForRangeNode();

            if(ctx.forInitStatement() != null) {
                // Basic for
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
                // for-each
                visitForRangeDeclaration(ctx.forRangeDeclaration(), forRangeNode);
                visitForRangeInitializer(ctx.forRangeInitializer(), forRangeNode);
                forRangeNode.setBody(visitStatement(ctx.statement()));
            }

            return forNode.getCondition() != null ? forNode : forRangeNode;
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


    private void visitForRangeDeclaration(CPP14Parser.ForRangeDeclarationContext ctx, ForRangeNode forNode) {
        DeclaratorNode decl = new DeclaratorNode();
        visitDeclarator(ctx.declarator(), decl);
        VariableDeclarationNode var = new VariableDeclarationNode();
        var.setName(decl);
        visitDeclSpecifierSeq(ctx.declSpecifierSeq(), var);

        forNode.setRangeDeclaration(var);

    }
    private void visitForRangeInitializer(CPP14Parser.ForRangeInitializerContext ctx, ForRangeNode forNode) {
        if(ctx.expression() != null) {
            ExpressionNode expr = (ExpressionNode) visitExpression(ctx.expression());
            forNode.setRangeInitializer(expr);
        }

    }
    private void visitForInitStatement(CPP14Parser.ForInitStatementContext ctx, ForNode forNode) {
        if (ctx.expressionStatement() != null) {
            ExpressionNode expr = (ExpressionNode) visitExpression(ctx.expressionStatement().expression());
            forNode.setInitialStatement(expr);
        }else if(ctx.simpleDeclaration() != null) {
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
        //TODO check for other: asmDefinition, namespaceAliasDefinition
        //TODO using Declaration, usingDirective staticAssertDeclaration
        //TODO alias Declaration opaqueEnumDeclaration
    }
    private void visitSimpleDeclaration(CPP14Parser.SimpleDeclarationContext ctx, VariableDeclarationNode variable) {

        if(ctx.attributeSpecifierSeq() != null) {
            //TODO: maybe add this
        }
        if(ctx.declSpecifierSeq() != null) {
            visitDeclSpecifierSeq(ctx.declSpecifierSeq(), variable);
        }
        if(ctx.initDeclaratorList() != null) {
            for(var c : ctx.initDeclaratorList().initDeclarator()) {

                DeclaratorNode node = new DeclaratorNode();
                visitDeclarator(c.declarator(), node);
                if (node.getDeclaratorId().contains("[]")){
                    variable.setType("array");
                    node.setDeclaratorId(node.getDeclaratorId().replace("[]", ""));
                }
                variable.setName(node);
                // Checking if a variable is base type or class
                List<String> typeList = Arrays.asList("int","string","vector","bool","float","double");
                if(!typeList.contains(variable.getType())){
                    ClassStorage.getInstance().addVariable(variable.getType(),node.getDeclaratorId());
                }
                if(c.initializer() != null) {
                    var expression = visitInitializer(c.initializer());
                    variable.setExpression(expression);
                }
            }
        }
    }

    public ASTNode visitInitializer(CPP14Parser.InitializerContext ctx) {
        if(ctx.braceOrEqualInitializer() != null) {

            if(ctx.braceOrEqualInitializer().initializerClause() != null) {
                return visitInitializerClause(ctx.braceOrEqualInitializer().initializerClause());
            }else{
                //:TODO fix std::string t{"wird"}
                return null;
            }
        }
        if (ctx.expressionList() != null) {
            return visitInitializerList(ctx.expressionList().initializerList());
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
        exp.setValue("[" + ctx.getText() + "]");
        exp.setType("InitializerList");
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

        // TODO: declSpecifier+? attributeSpecifierSeq?
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

        String name = ctx.classHead().classHeadName().getText();
        List<ASTNode> params = new ArrayList<>();
        mclass.setClassName(name);
        for(CPP14Parser.MemberdeclarationContext member : ctx.memberSpecification().memberdeclaration()){

            if(member.functionDefinition()!=null){
                ASTNode func = visitFunctionDefinition(member.functionDefinition(),true, name);
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
            expr.setType("AssignmentExpression");
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
            operator = ConvertOperator.convert(ctx.Minus().getFirst().getText());
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
            operator = ConvertOperator.convert(ctx.Div().getFirst().getText());
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
            if(ctx.PlusPlus() != null){
                expression.setOperator(ctx.PlusPlus().getText());
                expression.setType("prefixIncrement");
            }
            else if (ctx.MinusMinus() != null){
                expression.setOperator(ctx.MinusMinus().getText());
                expression.setType("prefixDecrement");
            }
            else if (ctx.unaryOperator() != null){
                expression.setOperator(ctx.unaryOperator().getText());
                expression.setType("unaryOperator");
            }
            expression.setValue(ctx.unaryExpression().getText());
            visitUnaryExpression(ctx.unaryExpression(), expression);
        }
        if(ctx.newExpression_() != null){
            expression.setValue(ctx.newExpression_().getText());
            visitNewExpression_(ctx.newExpression_(),expression);
        }


    }
    private void visitNewExpression_(CPP14Parser.NewExpression_Context ctx, ExpressionNode expression) {

        String newsType = "new";
        ASTNode exp = visitExpressionList(ctx.newInitializer_().expressionList());
        String value = ctx.newInitializer_().getText();
        expression.setType(newsType);
        expression.setValue(value);
        expression.addChild(exp);
    }

    // p1-> nesto() ==> (), p1->nesto ==> (), p1, -> , nesto
    // p1->nesto(30) ==> (), p1->nesto, list ==> (), p1, ->, nesto, list
    //
    private void    visitPostfixExpression(CPP14Parser.PostfixExpressionContext ctx, ExpressionNode expression) {

        expression.setType("PostfixExpression");

        if(ctx.primaryExpression() != null){
            visitPrimaryExpression(ctx.primaryExpression(), expression);
        }else if(ctx.postfixExpression() != null){

            ExpressionNode postfixChild = new ExpressionNode();
            visitPostfixExpression(ctx.postfixExpression(), postfixChild);
            expression.addChild(postfixChild);

        }
        if(ctx.Dot() != null || ctx.Arrow() != null){
            LiteralNode literal = new LiteralNode();
            literal.setValue(ctx.idExpression().getText());//TODO add some changes from String->value or something
            expression.addChild(literal);
        }
        if (ctx.expressionList() != null) {
            ExpressionNode args = new ExpressionNode();
            args.setType("LIST");
            args.setValue("(" + ctx.expressionList().getText() + ")");
            expression.addChild(args);
        } else {
            String raw = ctx.getText();
            if (raw.endsWith("()")) {
                ExpressionNode args = new ExpressionNode();
                args.setType("LIST");
                args.setValue("()");
                expression.addChild(args);
            }
        }
        if (ctx.expression() != null && rawHasBracket(ctx)) {
            ExpressionNode idx = new ExpressionNode();
            idx.setType("LIST_IDX");
            idx.setValue("[" + ctx.expression().getText() + "]");
            expression.addChild(idx);
        }
        if (ctx.primaryExpression() != null){
            expression.setType("PrimaryExpression");
            if(ConvertFunctionCall.hasValue(ctx.primaryExpression().getText())){
                expression.setType("NormalFunction");
                expression  .setValue(ctx.primaryExpression().getText());
            }else {
                visitPrimaryExpression(ctx.primaryExpression(), expression);
            }
        }

        if (ctx.PlusPlus() != null) {
            expression.setType("PostfixIncrement");
            expression.setOperator("+=");
            String baseText = expression.getChildren().isEmpty() ? "" : expression.getChildren().get(0).toPython(0);
            expression.setValue(baseText + " += 1");
            return;
        }
        if (ctx.MinusMinus() != null) {
            expression.setType("PostfixDecrement");
            expression.setOperator("-=");
            String baseText = expression.getChildren().isEmpty() ? "" : expression.getChildren().get(0).toPython(0);
            expression.setValue(baseText + " -= 1");
            return;
        }
    }

    private static boolean rawHasBracket(CPP14Parser.PostfixExpressionContext ctx) {
        String t = ctx.getText();
        int lb = t.lastIndexOf('['), rb = t.lastIndexOf(']');
        return lb >= 0 && rb > lb;
    }
    private void visitPrimaryExpression(CPP14Parser.PrimaryExpressionContext ctx, ExpressionNode expression) {
        //TODO: This, lambdaExpression
        expression.setType("PrimaryExpression");
        if (ctx.lambdaExpression() != null) {
            expression.setType("LambdaExpression");
            visitLambdaExpression(ctx.lambdaExpression(), expression);
        }
        else {
            expression.setValue(ctx.getText());
        }
    }

    private void visitLambdaExpression(CPP14Parser.LambdaExpressionContext ctx, ExpressionNode expression) {

        if (ctx.lambdaDeclarator() != null) {
            DeclaratorNode decl = new DeclaratorNode();
            visitParameterDeclarationClause(ctx.lambdaDeclarator().parameterDeclarationClause(), decl);
            expression.setValue(
                    decl.getParameters().stream()
                            .map(VariableDeclarationNode::getNameOut)
                            .collect(Collectors.joining(","))
            );
        }

        TermNode term = (TermNode) visitCompoundStatement(ctx.compoundStatement()).getStatements().getFirst();

        expression.addChild(term.getExpression());
    }
}

