package testEngine.core;

import testEngine.utils.OutputComperator;

public class TestResult {
    private final String name;
    private final boolean passed;
    private final String expectedOutput;
    private final String actualOutput;
    private final Exception error;
    private final long executionTimeMillis;

    public TestResult(String name, boolean passed, String expectedOutput, String actualOutput, Exception error, long executionTimeMillis) {
        this.name = name;
        this.passed = passed;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.error = error;
        this.executionTimeMillis = executionTimeMillis;
    }

    public static TestResult ofSuccess(String name, String expected, String actual, long time) {
        return new TestResult(name, true, expected, actual, null, time);
    }

    public static TestResult ofFailure(String name, String expected, String actual, Exception e, long time) {
        return new TestResult(name, false, expected, actual, e, time);
    }


    public void printReport() {
        System.out.println("Test: " + name);
        System.out.printf("Time: %d ms\n", executionTimeMillis);
        if (passed) {
            System.out.println("Passed\n");
        } else {
            System.out.println("Failed");
            if (error != null) {
                System.out.println("Error: " + error.getMessage());
                error.printStackTrace(System.out);
            }  else {
                System.out.println("Expected:\n" + expectedOutput);
                System.out.println("Actual:\n" + actualOutput);
                System.out.println("Diff:\n" + OutputComperator.getDiff(expectedOutput, actualOutput));
            }
            System.out.println();
        }
    }

    public boolean isPassed() {
        return passed;
    }

    public String getName() {
        return name;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public String getActualOutput() {
        return actualOutput;
    }

    public Exception getError() {
        return error;
    }
}

