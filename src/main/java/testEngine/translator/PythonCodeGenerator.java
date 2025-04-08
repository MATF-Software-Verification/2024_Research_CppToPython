package testEngine.translator;

import ast.ASTNode;

public class PythonCodeGenerator {

    public static String generate(ASTNode root) {
        return root.toPython(0); 
    }
}
