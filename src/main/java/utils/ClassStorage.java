package utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.util.*;

public class ClassStorage {

    private static final ClassStorage INSTANCE = new ClassStorage();
    private final Map<String, Set<String>> classFunctionMap = new HashMap<>();

    private ClassStorage() {}

    public static ClassStorage getInstance() {
        return INSTANCE;
    }

    public void addClass(String className) {
        classFunctionMap.putIfAbsent(className, new HashSet<>());
    }

    public void addFunction(String className, String functionName) {
        classFunctionMap.computeIfAbsent(className, k -> new HashSet<>()).add(functionName);
    }

    public boolean hasClass(String className) {
        return classFunctionMap.containsKey(className);
    }

    public boolean hasFunction(String className, String functionName) {
        return classFunctionMap.containsKey(className) &&
                classFunctionMap.get(className).contains(functionName);
    }

    public Set<String> getFunctions(String className) {
        return classFunctionMap.containsKey(className)
                ? Collections.unmodifiableSet(classFunctionMap.get(className))
                : Collections.emptySet();
    }

    public Set<String> getClassNames() {
        return Collections.unmodifiableSet(classFunctionMap.keySet());
    }
    public Map<String, Set<String>> getAll() {
        Map<String, Set<String>> copy = new HashMap<>();
        for (var entry : classFunctionMap.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }
}
