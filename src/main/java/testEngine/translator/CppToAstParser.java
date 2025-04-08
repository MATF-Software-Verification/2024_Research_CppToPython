package testEngine.translator;

import ast.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import antlr.CPP14Parser;


import java.io.File;
import java.io.IOException;

public class CppToAstParser {

    public static ASTNode parse(File cppFile) throws IOException {
        CharStream charStream = CharStreams.fromFileName(cppFile.getAbsolutePath());
        antlr.CPP14Lexer lexer = new antlr.CPP14Lexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        antlr.CPP14Parser parser = new CPP14Parser(tokens);

        // Printout of my AST
        ParseTree tree = parser.translationUnit();
        ASTBuilder ast = new ASTBuilder();
        ASTNode root = ast.visit(tree);

        return root;
    }
}
