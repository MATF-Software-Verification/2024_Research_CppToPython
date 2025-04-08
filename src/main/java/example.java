import antlr.CPP14Lexer;
import antlr.CPP14Parser;
import ast.ASTBuilder;
import ast.ASTNode;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;

public class example {

    public static void main(String[] args) throws IOException {

        CharStream charStream = CharStreams.fromFileName("src/main/tests/input/multiple_ifs.cpp");

        CPP14Lexer lexer = new CPP14Lexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CPP14Parser parser = new CPP14Parser(tokens);
//        parser.setTrace(true);

        // Printout of my AST
        ParseTree tree = parser.translationUnit();
        ASTBuilder ast = new ASTBuilder();
        ASTNode root = ast.visit(tree);
        System.out.println(root);
        System.out.println("------------------------");


        System.out.println("============= Python End ===========");
        String endString = root.toPython(  0);
        System.out.println(endString);
        System.out.println("=====================================");
        JFrame frame = new JFrame("ANTLR");
        TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        viewer.setScale(1.0); // Adjust the scale as needed
        JScrollPane scrollPane = new JScrollPane(viewer);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);


        System.out.println(tree.toStringTree(parser));
    }

}