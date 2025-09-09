import antlr.CPP14Lexer;
import antlr.CPP14Parser;
import ast.ASTBuilder;
import ast.ASTNode;
import ast.codegen.CodeGenerator;
import ast.TranslationUnitNode;
import ast.codegen.CodegenOptions;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;

public class example {

    public static void main(String[] args) throws IOException {

        CharStream charStream = CharStreams.fromFileName("src/main/tests/input/simpleDeclaration.cpp");

        CPP14Lexer lexer = new CPP14Lexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CPP14Parser parser = new CPP14Parser(tokens);
//        parser.setTrace(true);

        // Printout of my AST
        ParseTree tree = parser.translationUnit();
        ASTBuilder ast = new ASTBuilder();
        ASTNode root = ast.visit(tree);
        System.out.println(root);
        CodegenOptions opts = CodegenOptions.defaults()
                .withEmitEntryPoint(true)
                .withBlankLineBetweenDecls(true);

        String python = new CodeGenerator(opts).generate((TranslationUnitNode) root);
        System.out.println("=================== PYTHON =======================");
        System.out.println(python);
        System.out.println("=====================================");
        JFrame frame = new JFrame("ANTLR");
        TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        viewer.setScale(1.0); // Adjust the scale as needed
        JScrollPane scrollPane = new JScrollPane(viewer);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

}