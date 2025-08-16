package utils;

import java.util.ArrayList;
import java.util.List;

public class FunctionStorage {

    private static final FunctionStorage INSTANCE = new FunctionStorage();
    private final List<String> functions = new ArrayList<>();

    private FunctionStorage() {}

    public static FunctionStorage getInstance() {
        return INSTANCE;
    }

    public void addFunction(String functionName) {
        functions.add(functionName);
    }
    public boolean hasFunction(String functionName) {
        return functions.contains(functionName);
    }
    public List<String> getFunctions() {
        return functions;
    }
}
