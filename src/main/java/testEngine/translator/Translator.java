package testEngine.translator;

import ast.ASTNode;
import ast.CodeGenerator;
import ast.TranslationUnitNode;
import ast.codegen.CodegenOptions;

import java.io.File;
import java.io.IOException;

public class Translator {

    public static String translate(File cppFile) throws IOException {
        ASTNode root = CppToAstParser.parse(cppFile);
        CodegenOptions opts = CodegenOptions.defaults()
                .withEmitEntryPoint(true)
                .withBlankLineBetweenDecls(true);

        return new CodeGenerator(opts).generate((TranslationUnitNode) root);
    }
}
