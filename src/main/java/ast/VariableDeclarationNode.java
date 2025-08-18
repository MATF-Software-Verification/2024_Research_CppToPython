package ast;

import ast.codegen.CodegenContext;
import utils.ClassStorage;
import utils.FunctionStorage;


public class VariableDeclarationNode extends ASTNode {

    private String type;
    private DeclaratorNode name;
    private ASTNode expression;
    private Boolean classMember = Boolean.FALSE;

    public VariableDeclarationNode(String type, DeclaratorNode name, ASTNode expression) {
        this.name = name;
        this.expression = expression;
        this.type = type;

    }

    public VariableDeclarationNode(String type, DeclaratorNode name) {
        this.name = name;
        this.type = type;
    }

    public VariableDeclarationNode(){};

    public String getType() {
        return type;
    }

    public Boolean getClassMember() {
        return classMember;
    }
    public void setClassMember() {
        this.classMember = Boolean.TRUE;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeclaratorNode getName() {
        return name;
    }

    public String getNameOut() {
        return name != null ? name.getDeclaratorId() : "_";
    }
    public void setName(DeclaratorNode name) {
        this.name = name;
    }

    public ASTNode getExpression() {
        return expression;
    }
    public String getExpressionOut(){
        //TODO fix probably needed
        ExpressionNode exp = (ExpressionNode) expression;
        return exp.getValue();
    }

    public void setExpression(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    protected String nodeLabel() {
        String id = getNameOut();
        String t = (type == null ? "" : ", type=" + type);
        String cm = classMember ? ", member" : "";
        String init = (expression != null) ? ", init" : "";
        return "VarDecl(" + id + t + cm + init + ")";
    }

    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        if (expression != null) sb.append(expression.toTree(indent + 1));
        return sb.toString();
    }
    @Deprecated
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("VariableDeclaration{");
        if (type != null) {
            sb.append("type: ").append(type).append(" ");
        }

        if (name != null  && name.getDeclaratorId() != null) {
            sb.append("name: ").append(name.getDeclaratorId()).append(" ");
        }

        if ( name != null && name.getParameters() != null) {
            sb.append("parameters: ").append(name.getParameters()).append(" ");
        }

        if (expression != null) {
            sb.append("expression: ").append(expression).append(" ");
        }

        sb.append("}");
        return sb.toString();
    }
    public String toPython(int indent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder line = new StringBuilder();

        if(type != null && type.equals("class")) {
            sb.append(line);
            sb.append(expression.toPython(indent));

        }
        else if(name != null && FunctionStorage.getInstance().hasFunction(name.getDeclaratorId()) && !ClassStorage.getInstance().hasClass(name.getDeclaratorId())) {
            line.append(name.getDeclaratorId()).append("(");
            if(expression != null) {
                line.append(expression.toPython(0));
            }
            line.append(")");
            sb.append(line);
        }
        else if (type != null && ClassStorage.getInstance().hasClass(type)) {

            line.append(getNameOut());
            line.append("=").append(getType()).append("(");
            if(expression != null) {
                line.append(((ExpressionNode)expression).toPython(0));
            }
            line.append(")");
            sb.append(line);
            //TODO: this should be fixed crucial;
        }
        else if (type != null && type.contains("vector")){
            line.append(getNameOut());
            line.append(" = ");
            line.append("[");
            line.append(expression.toPython(indent));
            line.append("]");
            sb.append(line);
        }
        else {
            if (expression != null) {
                line.append(getNameOut());
                line.append(" = ");
                line.append(expression.toPython(indent));
            }
            sb.append(line);
        }

        return sb.toString();
    }


    @Override
    public String toPython(int indent, CodegenContext ctx) {
        if ("class".equals(type) && expression != null) {
            String forwarded = expression.toPython(indent, ctx);
            if (forwarded != null && !forwarded.isEmpty()) ctx.out.write(forwarded);
            return "";
        }

        final String id = getNameOut();
        System.out.println(" MY NAME "+ id);
        if (type == null && expression == null && id != null) {

            if (ctx.syms.inClassScope() && ctx.syms.classHasMethod(ctx.syms.currentClass(), id)) {
                ctx.out.writeln("self." + id + "()");
                return "";
            }
            if (ctx.syms.isFreeFunction(id)) {
                ctx.out.writeln(id + "()");
                return "";
            }
            ctx.out.writeln(id + " = None");
            return "";
        }
        // ---------------------------------------------------------------

        String target = id;
        if (classMember) target = "self." + target;

        if (type != null && ctx.syms.hasClass(type)) {
            String args = "";
            if (expression != null) {
                String init = expression.toPython(0);
                args = (init == null ? "" : init.strip());
            }
            ctx.out.writeln(target + " = " + type + "(" + args + ")");
            return "";
        }

        if (type != null && (type.contains("vector") || type.contains("array"))) {
            String contents = "";
            if (expression != null) {
                String s = expression.toPython(0);
                if (s != null) contents = s.strip();
            }
            ctx.out.writeln(target + " = [" + contents + "]");
            return "";
        }

        if (expression != null) {
            String rhs = expression.toPython(indent);
            ctx.out.writeln(target + " = " + (rhs == null ? "" : rhs.strip()));
        } else {
            ctx.out.writeln(target + " = None");
        }
        return "";
    }
    @Override
    public void collectImports(CodegenContext ctx) {
        if (expression != null) expression.collectImports(ctx);
    }

    @Override
    public void discover(CodegenContext ctx) {
        if (expression != null) expression.discover(ctx);
    }
}
