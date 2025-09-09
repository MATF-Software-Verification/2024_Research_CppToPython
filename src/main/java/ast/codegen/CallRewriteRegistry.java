package ast.codegen;

import java.util.*;

public final class CallRewriteRegistry {

    @FunctionalInterface
    public interface MethodRewriter {
        ExprResult rewrite(String recvCode, List<ExprResult> args, CodegenContext ctx);
    }

    @FunctionalInterface
    public interface FreeRewriter {
        ExprResult rewrite(List<ExprResult> args, CodegenContext ctx);
    }

    private final Map<String, Map<String, MethodRewriter>> byTypeThenMethod = new HashMap<>();
    private final Map<String, FreeRewriter> freeFuncs = new HashMap<>();

    public void registerMethod(String typeKey, String method, MethodRewriter r){
        byTypeThenMethod.computeIfAbsent(typeKey, k -> new HashMap<>()).put(method, r);
    }
    public void registerFree(String funcName, FreeRewriter r){
        freeFuncs.put(funcName, r);
    }

    public MethodRewriter findMethod(String resolvedType, String method){
        if (resolvedType == null || method == null) return null;

        var mm = byTypeThenMethod.get(resolvedType);
        if (mm != null && mm.containsKey(method)) {
            return mm.get(method);
        }

        for (var e : byTypeThenMethod.entrySet()){
            String k = e.getKey();
            if (k.endsWith("<*>")) {
                String base = k.substring(0, k.length()-3);
                if (resolvedType.startsWith(base) && e.getValue().containsKey(method)) {
                    return e.getValue().get(method);
                }
            }
        }
        return null;
    }

    public FreeRewriter findFree(String name){
        return freeFuncs.get(name);
    }

    public static CallRewriteRegistry pythonDefaults(){
        CallRewriteRegistry r = new CallRewriteRegistry();

        r.registerMethod("std::vector<*>", "size",
                (recv, args, ctx) -> ExprResult.of("len(" + recv + ")"));
        r.registerMethod("vector<*>", "size",
                (recv, args, ctx) -> ExprResult.of("len(" + recv + ")"));

        r.registerMethod("std::vector<*>", "empty",
                (recv, args, ctx) -> ExprResult.of("(len(" + recv + ") == 0)"));

        r.registerMethod("std::vector<*>", "push_back",
                (recv, args, ctx) -> {
                    String x = args.isEmpty()? "" : args.get(0).code;
                    return ExprResult.of(recv + ".append(" + x + ")");
                });

        for (String strTy : List.of("std::string", "string")){
            r.registerMethod(strTy, "size",   (recv, a, c) -> ExprResult.of("len(" + recv + ")"));
            r.registerMethod(strTy, "length", (recv, a, c) -> ExprResult.of("len(" + recv + ")"));
            r.registerMethod(strTy, "empty",  (recv, a, c) -> ExprResult.of("(len(" + recv + ") == 0)"));
            r.registerMethod(strTy, "substr", (recv, a, c) -> {
                String i = a.size() >= 1 ? a.get(0).code : "0";
                String n = a.size() >= 2 ? a.get(1).code : null;
                return ExprResult.of(n == null ? (recv + "[" + i + ":]") : (recv + "[" + i + ":" + i + "+" + n + "]"));
            });
        }

        for (String mapTy : List.of("std::map<*>", "map<*>", "std::unordered_map<*>", "unordered_map<>")){
            r.registerMethod(mapTy, "size", (recv, a, c) -> ExprResult.of("len(" + recv + ")"));
            r.registerMethod(mapTy, "empty",(recv, a, c) -> ExprResult.of("(len(" + recv + ") == 0)"));
        }


        return r;
    }
}
