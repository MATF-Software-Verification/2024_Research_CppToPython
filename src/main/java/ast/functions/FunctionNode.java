package ast.functions;

import ast.ASTNode;
import ast.CompoundNode;
import ast.DeclaratorNode;
import ast.VariableDeclarationNode;
import ast.codegen.CodegenContext;
import utils.TypeMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionNode extends ASTNode {

    private final String return_value;
    private DeclaratorNode func_declarator;
    private CompoundNode body;
    private boolean in_class;
    private boolean isConstructor;
    private String ownerClassName;


    public FunctionNode(String return_value, DeclaratorNode declarator, boolean in_class) {
        this.return_value = return_value;
        this.func_declarator = declarator;
        this.in_class = in_class;
        this.isConstructor = false;
    }

    public String getName() {
        return func_declarator != null ? func_declarator.getDeclaratorId() : null;
    }


    public DeclaratorNode getFunc_declarator() {
        return func_declarator;
    }


    public void setBody(CompoundNode body) {
        if (this.body == null)
            this.body = body;
        else{
            for (var stmt : body.getStatements()){
                this.body.add(stmt);
            }
        }
    }


    public void setInClass(boolean in_class) {
        this.in_class = in_class;
    }


    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }

    public void setOwnerClassName(String ownerClassName) {
        this.ownerClassName = ownerClassName;
    }


    @Override
    protected String nodeLabel() {
        String name = getName();
        int paramCount = func_declarator != null && func_declarator.getParameters() != null
                ? func_declarator.getParameters().size() : 0;

        StringBuilder sb = new StringBuilder("Function(");
        sb.append("name=").append(name);
        if (in_class) sb.append(", inClass=true");
        if (isConstructor) sb.append(", ctor=true");
        if (ownerClassName != null) sb.append(", owner=").append(ownerClassName);
        if (return_value != null && !return_value.isBlank()) sb.append(", ret=").append(return_value);
        sb.append(", params=").append(paramCount);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        if (body != null) {
            sb.append(body.toTree(indent + 1));
        } else {
            sb.append(line(indent + 1, "(no body)"));
        }
        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String cppName = getName();
        String pyName = (isConstructor || "__init__".equals(cppName)) ? "__init__" : cppName;
        if (pyName == null || pyName.isBlank()) pyName = "fn";

        String retVal = TypeMapper.mapCppTypeToPython(return_value);
        String typing = (!Objects.equals(retVal, "unknown") ? " -> " + retVal : "");
        String signature = "def " + pyName + "(" + buildParamListPython() + ")" + (!Objects.equals(pyName, "__init__") ? typing : "") + ":";
        ctx.out.writeln(signature);
        ctx.out.indent();
        ctx.enterScope();

        if (in_class) ctx.declare("self");
        if (func_declarator != null && func_declarator.getParameters() != null) {
            for (VariableDeclarationNode p : func_declarator.getParameters()) {
                if (p.getName() != null && p.getName().getDeclaratorId() != null) {
                    ctx.declare(p.getName().getDeclaratorId());
                }
            }
        }

        if (in_class && ownerClassName != null) {
            ctx.syms.declareVar("self", ownerClassName);
        }
        if (func_declarator != null && func_declarator.getParameters() != null) {
            for (VariableDeclarationNode p : func_declarator.getParameters()) {
                var n = (p.getName() != null ? p.getName().getDeclaratorId() : null);
                if (n != null && p.getType() != null && !p.getType().isBlank()) {
                    ctx.syms.declareVar(n, p.getType());
                }
            }
        }

        if (body == null || body.getStatements().isEmpty()) {
            ctx.out.writeln("pass");
        } else {
            body.toPython(indent + 1, ctx);
        }
        ctx.exitScope();
        ctx.out.dedent();
        return "";
    }


    private String buildParamListPython() {

        List<String> params = List.of();
        if (func_declarator != null && func_declarator.getParameters() != null) {
            params = func_declarator.getParameters().stream()
                    .map(vd -> vd.getName() != null ? vd.getName().getDeclaratorId() : "_")
                    .collect(Collectors.toList());
        }
        if (in_class) {
            if (params.isEmpty()) return "self";
            return "self, " + String.join(", ", params);
        }
        return String.join(", ", params);
    }


    @Override
    public void discover(CodegenContext ctx) {
        String n = getName();
        if (in_class) {
            if (ownerClassName != null && n != null) {
                ctx.syms.addMethod(ownerClassName, n);
            }
        } else {
            if (n != null) ctx.syms.addFreeFunction(n);
            if ("main".equals(n)) ctx.meta.hasMain = true;
        }
        if (body != null) body.discover(ctx);
    }

}
