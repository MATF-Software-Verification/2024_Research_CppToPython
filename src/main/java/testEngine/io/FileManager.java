package testEngine.io;

import testEngine.core.TestCase;

import java.io.*;
import java.nio.file.Files;

public class FileManager {

    public static void compileCpp(File cppFile, File execFile) throws IOException,InterruptedException{
        ProcessBuilder compile = new ProcessBuilder("g++", cppFile.getAbsolutePath(), "-o", execFile.getAbsolutePath());
        compile.redirectErrorStream(true);
        Process process = compile.start();

        String output = readStream(process.getInputStream());
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            System.err.println("Compilation failed for file: " + cppFile.getName());
            System.err.println("Compiler Output:\n" + output);
            throw new RuntimeException("Compilation failed for: " + cppFile.getName());
        }
    }
    public static String runBinary(File execFile) throws IOException,InterruptedException{
        Process run = new ProcessBuilder(execFile.getAbsolutePath())
                .redirectErrorStream(true)
                .start();
        return readStream(run.getInputStream());
    }

    public static String runPython(File pyFile) throws IOException,InterruptedException{
        Process process = new ProcessBuilder("python3", pyFile.getAbsolutePath())
                .redirectErrorStream(true)
                .start();
        String output = readStream(process.getInputStream());
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Python execution failed:\n" + output);
        }

        return output;
    }

    public static String readFile(File file) throws IOException{
        return Files.readString(file.toPath()).trim();
    }
    public static void writeToFile(File file, String content) throws IOException{
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), content);
    }
    public static void cleanupGenerated(TestCase testCase){
        deleteIfExists(testCase.getCompiledBinary());
        //deleteIfExists(testCase.getGeneratedPythonFile());
    }

    private static void deleteIfExists(File file) {
        try {
            if (file.exists() && !file.delete()) {
                System.err.println("Failed to delete: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("⚠Exception while deleting: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private static String readStream(InputStream inputStream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) out.append(line).append("\n");
        return out.toString();
    }

}
