package testEngine.core;

import java.io.File;

public class TestCase {

    private final String name;
    private final File cppSource;
    private final File expectedOutput;

    public TestCase(String name, File cppSource, File expectedOutput) {
        this.name = name;
        this.cppSource = cppSource;
        this.expectedOutput = expectedOutput;
    }
    public String getName() {return name;}
    public File getCppSource() {return cppSource;}

    public File getExpectedOutput() { return expectedOutput; }

    public File getCompiledBinary() {
        return new File(getWorkingDirectory(), name + "_exec");
    }

    public File getGeneratedPythonFile() {
        return new File(getWorkingDirectory().getParent()+"/pythonGenerated", name + ".py");
    }

    public File getWorkingDirectory() {
        return expectedOutput.getParentFile();
    }
}
