package ast;

import ast.codegen.CodegenContext;
import utils.ClassStorage;
import utils.FunctionStorage;
import utils.TypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private static boolean isVectorLike(String t) {
        if (t == null) return false;
        String s = t.toLowerCase();
        return s.contains("vector") || s.contains("array");
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

    @Override
    public String toPython(int indent) {
        String id = getNameOut();
        String target = classMember ? "self." + id : id;

        if ("class".equals(type) && expression != null) {
            return expression.toPython(indent);
        }

        if (type != null) {
            if (isVectorLike(type)) {
                String contents = (expression == null) ? "" : expression.toPython(0);
                contents = (contents == null) ? "" : contents.strip();
                return target + " = [" + contents + "]";
            }
            String args = (expression == null) ? "" : String.valueOf(expression.toPython(0)).strip();
            return target + " = " + type + "(" + args + ")";
        }

        if (expression != null) {
            String rhs = expression.toPython(indent);
            rhs = (rhs == null) ? "" : rhs.strip();
            return target + " = " + rhs;
        }
        return target + " = None";
    }


    @Override
    public String toPython(int indent, CodegenContext ctx) {
        final String id = getNameOut();

        if (!Boolean.TRUE.equals(classMember) && id != null && type != null && !type.isBlank()) {
            ctx.syms.declareVar(id, type);
        }
        if ("class".equals(type) && expression != null) {
            String forwarded = expression.toPython(indent, ctx);
            if (forwarded != null && !forwarded.isEmpty()) ctx.out.write(forwarded);
            return "";
        }
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

        String target = classMember ? "self." + id : id;

        if (ctx.syms.hasClass(type)) {
            String args = "";
            if (expression != null) {
                args = argsFromExpr(expression, ctx).strip();
            }
            ctx.out.writeln(target + " = " + type + "(" + args + ")");
            return "";
        }
        if (isVectorLike(type)) {
            String contents = "";
            if (expression != null) {
                contents = argsFromExpr(expression, ctx).strip();
            }
            String typing = TypeMapper.mapCppTypeToPython(type);
            ctx.out.writeln(target + (!Objects.equals(typing, "unknown") ? ": " + typing : "") + " = [" + contents + "]");
            return "";
        }

        if (type == null && name != null && ctx.syms.isFreeFunction(name.getDeclaratorId())
                && !ctx.syms.hasClass(name.getDeclaratorId())) {
            String args = (expression == null) ? "" : argsFromExpr(expression, ctx).strip();
            ctx.out.writeln(name.getDeclaratorId() + "(" + args + ")");
            return "";
        }
        if (expression != null) {
            String rhs;
            if (expression instanceof ExpressionNode en) {
                rhs = en.emitExpr(ctx).code.strip();
            } else {
                String s = expression.toPython(0);
                rhs = (s == null ? "" : s.strip());
            }
            String typing = null;
            if (type != null) {
                typing = TypeMapper.mapCppTypeToPython(type);
            }
            ctx.out.writeln(target + (!Objects.equals(typing, "unknown") ? ": " + typing : "") + " = " + (rhs == null ? "" : rhs.strip()));
        } else {
            ctx.out.writeln(target + " = None");
        }
        return "";
    }

    @Override
    public void discover(CodegenContext ctx) {
        if (getName() != null) {
            String varName = getNameOut();
            String varType = (getType() == null) ? "" : getType();
            ctx.syms.declareVar(varName, varType);
        }
        if (getExpression() != null) getExpression().discover(ctx);
    }

    private static String argsFromExpr(ASTNode e, CodegenContext ctx) {
        if (!(e instanceof ExpressionNode en)) {
            return (e == null) ? "" : e.toPython(0);
        }

        String typ = en.getType();
        if ("InitializerList".equals(typ) || "LIST".equals(typ)) {
            List<String> parts = new ArrayList<>();
            if (en.getChildren() != null) {
                for (ast.ASTNode ch : en.getChildren()) {
                    if (ch instanceof ast.ExpressionNode ce) {
                        parts.add(ce.emitExpr(ctx).code);
                    } else {
                        parts.add(ch.toPython(0));
                    }
                }
            }
            return String.join(", ", parts);
        }
        if (en.getChildren() != null) {
            for (ast.ASTNode ch : en.getChildren()) {
                if (ch instanceof ast.ExpressionNode childEn) {
                    String ctyp = childEn.getType();
                    if ("InitializerList".equals(ctyp) || "LIST".equals(ctyp)) {
                        List<String> parts = new ArrayList<>();
                        if (childEn.getChildren() != null) {
                            for (ast.ASTNode gch : childEn.getChildren()) {
                                if (gch instanceof ast.ExpressionNode gce) {
                                    parts.add(gce.emitExpr(ctx).code);
                                } else {
                                    parts.add(gch.toPython(0));
                                }
                            }
                        }
                        return String.join(", ", parts);
                    }
                }
            }
        }

        return en.emitExpr(ctx).code;
    }
}
