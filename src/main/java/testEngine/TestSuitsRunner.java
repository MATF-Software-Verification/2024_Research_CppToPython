package testEngine;

import testEngine.core.TestCase;
import testEngine.core.TestResult;
import testEngine.core.TestRunner;
import testEngine.io.FileManager;
import testEngine.io.TestCaseLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestSuitsRunner {
    public static void main(String[] args) {

        try {
            List<TestCase> tests = TestCaseLoader.loadAllTests();
            int passed = 0;

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<TestResult>> futures = new ArrayList<>();

            for (TestCase test : tests) {
                futures.add(executor.submit(() -> {
                    System.out.printf("[Thread: %s] Running %s\n", Thread.currentThread().getName(), test.getName());
                    return TestRunner.runTest(test);
                }));
            }

            for (Future<TestResult> future : futures) {
                try {
                    TestResult result = future.get();
                    result.printReport();
                    if (result.isPassed()) passed++;
                } catch (Exception e) {
                    System.err.println("Error during test execution: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            executor.shutdown();
            System.out.printf("Total Passed: %d / %d\n", passed, tests.size());

        } catch (Exception ex) {
            System.err.println("FATAL error in main()");
            ex.printStackTrace();
        }
    }
}
