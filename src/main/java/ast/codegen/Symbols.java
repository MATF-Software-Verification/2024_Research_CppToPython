package ast.codegen;

public final class Symbols {
    public static final class ClassInfo {
        public final java.util.Set<String> methods = new java.util.HashSet<>();
        public final java.util.Set<String> fields  = new java.util.HashSet<>();
        public final java.util.Set<String> statics = new java.util.HashSet<>();
    }

    private final java.util.Set<String> freeFunctions = new java.util.HashSet<>();
    private final java.util.Map<String, ClassInfo> classes = new java.util.HashMap<>();
    private final java.util.Deque<String> classScope = new java.util.ArrayDeque<>();

    public void addFreeFunction(String name){ if (name!=null && !name.isBlank()) freeFunctions.add(name); }
    public boolean isFreeFunction(String name){ return name!=null && freeFunctions.contains(name); }

    public void addClass(String name){ classes.computeIfAbsent(name, k -> new ClassInfo()); }
    public boolean hasClass(String name){ return name!=null && classes.containsKey(name); }
    public ClassInfo info(String name){ return classes.get(name); }

    public void addMethod(String cls, String m){ addClass(cls); classes.get(cls).methods.add(m); }
    public boolean classHasMethod(String cls, String m){
        return hasClass(cls) && classes.get(cls).methods.contains(m);
    }

    public void enterClass(String cls){ classScope.push(cls); }
    public void exitClass(){ if(!classScope.isEmpty()) classScope.pop(); }
    public String currentClass(){ return classScope.isEmpty()? null : classScope.peek(); }
    public boolean inClassScope(){ return !classScope.isEmpty(); }
}
