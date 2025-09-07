package ast.codegen;

import java.util.*;

public final class Symbols {
    public static final class ClassInfo {
        public final Set<String> methods = new HashSet<>();
        public final Set<String> fields  = new HashSet<>();
        public final Set<String> statics = new HashSet<>();
    }

    private final Set<String> freeFunctions = new HashSet<>();
    private final Map<String, ClassInfo> classes = new HashMap<>();
    private final Deque<String> classScope = new ArrayDeque<>();
    private final Deque<Map<String,String>> varScopes = new ArrayDeque<>();

    public Symbols() {
        varScopes.push(new HashMap<>());
    }

    public void addFreeFunction(String name){ if (name!=null && !name.isBlank()) freeFunctions.add(name); }
    public boolean isFreeFunction(String name){ return name!=null && freeFunctions.contains(name); }

    public void addClass(String name){ classes.computeIfAbsent(name, k -> new ClassInfo()); }
    public boolean hasClass(String name){ return name!=null && classes.containsKey(name); }
    public ClassInfo info(String name){ return classes.get(name); }

    public void addMethod(String cls, String m){ addClass(cls); classes.get(cls).methods.add(m); }
    public boolean classHasMethod(String cls, String m){
        return hasClass(cls) && classes.get(cls).methods.contains(m);
    }

    public void enterScopeVars(){ varScopes.push(new HashMap<>()); }
    public void exitScopeVars(){ if (!varScopes.isEmpty()) varScopes.pop(); }
    public void enterClass(String cls){ classScope.push(cls); }
    public void exitClass(){ if(!classScope.isEmpty()) classScope.pop(); }
    public String currentClass(){ return classScope.isEmpty()? null : classScope.peek(); }
    public boolean inClassScope(){ return !classScope.isEmpty(); }


    public void declareVar(String name, String type){
        if (name == null) return;

        varScopes.peek().put(name, type);
    }

    public String lookupVarType(String name){
        if (name == null) return null;
        for (var scope : varScopes) {
            if (scope.containsKey(name)) {
                String t = scope.get(name);
                return t;
            }
        }
        return null;
    }
}
