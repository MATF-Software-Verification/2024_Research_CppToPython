package testEngine.io;

import testEngine.core.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestCaseLoader {
    private static final String INPUT_DIR = "src/main/tests/input";
    private static final String EXPECTED_DIR = "src/main/tests/expected";

    public static List<TestCase> loadAllTests() {
        List<TestCase> testCases = new ArrayList<>();
        File inputDir = new File(INPUT_DIR);
        System.out.println(inputDir.getAbsolutePath());
        for (File cppFile : Objects.requireNonNull(inputDir.listFiles((dir, name) -> name.endsWith(".cpp")))) {
            System.out.println(cppFile.getAbsolutePath());
            String base = cppFile.getName().replace(".cpp", "");
            File expected = new File(EXPECTED_DIR, base + "_expected_output.txt");
//            if (!expected.exists()){
//                System.out.println(expected.getAbsolutePath());
//                continue;
//            }
            testCases.add(new TestCase(base, cppFile, expected));
        }

        return testCases;
    }
}