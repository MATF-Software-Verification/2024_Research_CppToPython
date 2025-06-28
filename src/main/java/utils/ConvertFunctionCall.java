package utils;

import java.util.HashMap;
import java.util.Map;

public class ConvertFunctionCall {


    private static final Map<String, String> FUNCTION_MAPPINGS = new HashMap<>();

    static {
        FUNCTION_MAPPINGS.put("size","len");
        FUNCTION_MAPPINGS.put("std::cout","print");
        FUNCTION_MAPPINGS.put("cout","print");
        FUNCTION_MAPPINGS.put("std::endl","");
        FUNCTION_MAPPINGS.put("endl","");
    }

    private ConvertFunctionCall() {}

    public static String convert(String input){
        return FUNCTION_MAPPINGS.getOrDefault(input.toLowerCase(),input);
    }
    public static void addMapping(String input, String function){
        FUNCTION_MAPPINGS.put(input.toLowerCase(),function);
    }
    public static boolean hasValue(String value){
        return FUNCTION_MAPPINGS.containsValue(value.toLowerCase());
    }
}
