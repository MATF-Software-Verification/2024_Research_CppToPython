package ast.functions;

import ast.ASTNode;
import ast.CompoundNode;
import ast.DeclaratorNode;
import ast.VariableDeclarationNode;
import ast.codegen.CodegenContext;
import utils.ClassStorage;
import utils.TypeMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionNode extends ASTNode {

    private final String return_value;
    private DeclaratorNode func_declarator;
    private CompoundNode body; //TODO change to CompoundNode later.
    private boolean in_class;
    private boolean isConstructor;
    private String ownerClassName;


    public FunctionNode(String return_value, DeclaratorNode declarator, boolean in_class) {
//        this.body = new ArrayList<>();
        this.return_value = return_value;
        this.func_declarator = declarator;
        this.in_class = in_class;
        this.isConstructor = false;
    }

    public String getName() {
        return func_declarator != null ? func_declarator.getDeclaratorId() : null;
    }

    public void addBodyNode(ASTNode node) {
        if (body == null) body = new CompoundNode();
        body.add(node);
    }

    public DeclaratorNode getFunc_declarator() {
        return func_declarator;
    }

    public void setFunc_declarator(DeclaratorNode func_declarator) {
        this.func_declarator = func_declarator;
    }


    public CompoundNode getBody() {
        return body;
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

    public boolean isInClass() {
        return in_class;
    }

    public void setInClass(boolean in_class) {
        this.in_class = in_class;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }

    public String getOwnerClassName() {
        return ownerClassName;
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

    @Deprecated
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Function{");
        if (return_value != null) {
            sb.append("return_value=").append(return_value);
        }

        if (func_declarator.getDeclaratorId() != null) {
            sb.append(", name: ").append(func_declarator.getDeclaratorId());
        }

        if (func_declarator.getParameters() != null) {
            sb.append(", parameters: ").append(func_declarator.getParameters());
        }

        if (body != null) {
            sb.append(", body: ").append(body);
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toPython(int indent) {

        StringBuilder sb = new StringBuilder();
        StringBuilder code = new StringBuilder();
        code.append("def ");
        if (ClassStorage.getInstance().getClassNames().contains(func_declarator.getDeclaratorId())) {
            code.append("__init__");
        } else
            code.append(func_declarator.getDeclaratorId());

        code.append("(");
        if (in_class) {
            code.append("self");
            if (func_declarator.getParameters() != null) {
                code.append(",");
            }
        }
        if (func_declarator.getParameters() != null) {
            for (int i = 0; i < func_declarator.getParameters().size(); i++) {

                VariableDeclarationNode param = func_declarator.getParameters().get(i);
                String name = param.getName().getDeclaratorId();
                if (i < func_declarator.getParameters().size() - 1) {
                    code.append(name + ",");
                } else {
                    code.append(name);
                }
            }
        }
        code.append("):");
        sb.append(getIndentedPythonCode(indent, code.toString()));
        sb.append(body.toPython(indent + 1));

        return sb.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String cppName = getName();
        String pyName = (isConstructor || "__init__".equals(cppName)) ? "__init__" : cppName;

        String retVal = TypeMapper.mapCppTypeToPython(return_value);
        String typing = (!Objects.equals(retVal, "unknown") ? " -> " + retVal : "");
        String signature = "def " + pyName + "(" + buildParamListPython() + ")" + (!Objects.equals(pyName, "__init__") ? typing : "") + ":";
        ctx.out.writeln(signature);
        ctx.out.indent();

        if (body == null) {
            ctx.out.writeln("pass");
        } else {
            ctx.out.write(body.toPython(indent + 1,ctx));
        }

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
    public void collectImports(CodegenContext ctx) {
        if (body != null) body.collectImports(ctx);
    }

    @Override
    public void discover(CodegenContext ctx) {
        String n = getName();
        System.out.println("============================= discover: " + n);
        if (isInClass()) {
            if (getOwnerClassName() != null && n != null) ctx.syms.addMethod(getOwnerClassName(), n);
        } else {
            if (n != null) ctx.syms.addFreeFunction(n);
            if ("main".equals(n)) ctx.meta.hasMain = true;
        }
        if (getBody() != null) getBody().discover(ctx);
    }

}
