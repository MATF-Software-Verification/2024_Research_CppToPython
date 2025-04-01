package ast;

import java.util.List;

public class ClassNode extends ASTNode {

    String className;
    List<ASTNode> members;

    public ClassNode() {

    }

    public List<ASTNode> getMembers() {
        return members;
    }

    public void setMembers(List<ASTNode> members) {
        this.members = members;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
        StringBuilder sb = new StringBuilder();
        StringBuilder line = new StringBuilder();
        line.append("class ").append(className).append(":");
        sb.append(getIndentedPythonCode(indent,line.toString()));

        for (ASTNode member : members) {
            if(member!=null) {
                sb.append(getIndentedPythonCode(indent,member.toPython(indent+1)));
            }
        }

        return sb.toString();
    }
}
