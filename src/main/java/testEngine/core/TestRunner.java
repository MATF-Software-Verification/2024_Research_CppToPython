package testEngine.core;

import testEngine.io.FileManager;
import testEngine.utils.OutputComperator;
import testEngine.translator.Translator;

import java.io.File;

public class TestRunner {

    public static TestResult runTest(TestCase testCase) {

        long startTime = System.currentTimeMillis();
        try {
            // Compilation and making binary files
            FileManager.compileCpp(testCase.getCppSource(), testCase.getCompiledBinary());
            String cppOutput = FileManager.runBinary(testCase.getCompiledBinary());
            FileManager.writeToFile(testCase.getExpectedOutput(), cppOutput);

            // Dealing with python code
            String pythonCode = Translator.translate(testCase.getCppSource());
            FileManager.writeToFile(testCase.getGeneratedPythonFile(), pythonCode);
            String pythonOutput = FileManager.runPython(testCase.getGeneratedPythonFile());

            boolean passed = OutputComperator.compare(cppOutput, pythonOutput);

            return TestResult.ofSuccess(testCase.getName(), cppOutput, pythonOutput,
                    System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            System.err.println("Test failed internally: " + testCase.getName());
            e.printStackTrace();
            return TestResult.ofFailure(testCase.getName(), "", "", e, System.currentTimeMillis() - startTime);

        } finally {
            FileManager.cleanupGenerated(testCase);
        }
    }
}
