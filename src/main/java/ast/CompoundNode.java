    package ast;

    import ast.codegen.CodegenContext;

    import java.util.ArrayList;
    import java.util.List;

    public class CompoundNode extends ASTNode{

        private List<ASTNode> statements;

        public CompoundNode() {
            this.statements = new ArrayList<ASTNode>();
        }

        public List<ASTNode> getStatements() {
            return statements;
        }

        public void setStatements(List<ASTNode> statements) {
            this.statements = statements;
        }

        public void add(ASTNode statement) {
            statements.add(statement);
        }

        @Override
        protected String nodeLabel() {
            return "Compound(" + statements.size() + " stmts)";
        }

        @Override
        public String toTree(int indent) {
            StringBuilder sb = new StringBuilder();
            sb.append(line(indent, nodeLabel()));
            for (ASTNode s : statements) {
                sb.append(s.toTree(indent + 1));
            }
            return sb.toString();
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append("CompoundNode{\n");
            for (ASTNode statement : statements) {
                sb.append(statement);
                sb.append("\n");
            }
            sb.append("}");

            return sb.toString();
        }

        @Override
        public String toPython(int indent) {

            StringBuilder sb = new StringBuilder();

            for (ASTNode statement : statements) {
                StringBuilder line = new StringBuilder();
                line.append(getIndentedPythonCode(indent,statement.toPython(indent)));
                sb.append(line);
            }

            return sb.toString();
        }

        @Override
        public String toPython(int indent, CodegenContext ctx) {
            if (statements.isEmpty()) {
                ctx.out.writeln("pass");
                return "";
            }

            for (ASTNode s : statements) {
                String maybe = s.toPython(indent, ctx);
                if (maybe != null && !maybe.isEmpty()) {
                    ctx.out.write(maybe);
                }
            }
            return "";
        }
        @Override
        public void collectImports(CodegenContext ctx) {
            for (ASTNode s : statements) s.collectImports(ctx);
        }

        @Override
        public void discover(CodegenContext ctx) {
            for (ASTNode s : statements) s.discover(ctx);
        }
    }
