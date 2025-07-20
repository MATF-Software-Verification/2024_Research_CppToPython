package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionRegistry {

    private static final Map<String, FunctionHandler> HANDLERs = new HashMap<>();


    static {
        HANDLERs.put("print",ConvertStreamOutput::convert);
    }

    public static boolean   hasHandler(String name){
        return HANDLERs.containsKey(name);
    }

    public static String handle(String name, List<String> children){
        FunctionHandler handler = HANDLERs.get(name);
        if(handler == null){
            return "";
        }
        return handler.handle(children);
    }
    public static void register(String name, FunctionHandler handler){
        HANDLERs.put(name, handler);
    }
}
