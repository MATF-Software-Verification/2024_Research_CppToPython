package testEngine.translator;

import ast.ASTNode;

import java.io.File;
import java.io.IOException;

public class Translator {

    public static String translate(File cppFile) throws IOException {
        ASTNode root = CppToAstParser.parse(cppFile);
        return PythonCodeGenerator.generate(root);
    }
}
