package ast;

import ast.codegen.CodegenContext;
import ast.functions.FunctionNode;

import java.util.ArrayList;
import java.util.List;

public class ClassNode extends ASTNode {

    String className;
    private final List<ASTNode> members = new ArrayList<>();
    public ClassNode() {

    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public void setMembers(List<ASTNode> newMembers) {
        members.clear();
        if (newMembers == null) return;
        for (ASTNode m : newMembers) addMember(m);
    }

    public void addMember(ASTNode member) {
        if (member == null) return;

        if (member instanceof FunctionNode fn) {
            fn.setInClass(true);
            fn.setOwnerClassName(className);
            if (fn.getName() != null && fn.getName().equals(className)) {
                fn.setConstructor(true);
            }
        }

        if (member instanceof VariableDeclarationNode vd) {
            vd.setClassMember();
        }

        members.add(member);
    }

    @Override
    protected String nodeLabel() {
        return "Class(" + (className == null ? "<unnamed>" : className) + "), members=" + members.size();
    }

    @Override
    public String toTree(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(indent, nodeLabel()));
        for (ASTNode m : members) {
            sb.append(m.toTree(indent + 1));
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CLASS [");
        if(className!=null) {
            sb.append("class_name = ").append(className).append("\n");
        }

        if(members!=null) {
            for (ASTNode member : members) {

                if(member!=null) {
                    sb.append(member.toString() + "\n");
                }
            }
        }
        sb.append("]\n");

        return sb.toString();
    }

    @Override
    public String toPython(int indent) {
        CodegenContext ctx = CodegenContext.pythonDefaults();
        toPython(indent, ctx);
        return ctx.out.toString();
    }

    @Override
    public String toPython(int indent, CodegenContext ctx) {
        String name = (className == null || className.isBlank()) ? "Unnamed" : className;

        ctx.out.writeln("class " + name + ":");
        ctx.out.indent();

        if (members.isEmpty()) {
            ctx.out.writeln("pass");
            ctx.out.dedent();
            return "";
        }

        for (int i = 0; i < members.size(); i++) {
            ASTNode m = members.get(i);

            String maybe = m.toPython(indent + 1, ctx);
            if (maybe != null && !maybe.isEmpty()) {
                ctx.out.write(maybe);
            }

            if (i < members.size() - 1) ctx.out.writeln("");
        }

        ctx.out.dedent();
        return "";
    }
    @Override
    public void discover(CodegenContext ctx) {
        if (className != null) {
            ctx.syms.addClass(className);
            ctx.syms.enterClass(className);
        }
        if (members != null) for (ASTNode m : members) m.discover(ctx);
        if (className != null) ctx.syms.exitClass();
    }
}
