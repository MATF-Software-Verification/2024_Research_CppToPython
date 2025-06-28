package utils;

import java.util.HashMap;
import java.util.Map;

public class ConvertOperator {

    private static  final Map<String,String> OPERATOR_MAPPINGS = new HashMap<>();

    static{
        OPERATOR_MAPPINGS.put("+", "+");
        OPERATOR_MAPPINGS.put("-", "-");
        OPERATOR_MAPPINGS.put("*", "*");
        OPERATOR_MAPPINGS.put("/", "/");
        OPERATOR_MAPPINGS.put("%", "%");
        OPERATOR_MAPPINGS.put("^", "^");
        OPERATOR_MAPPINGS.put("&", "&");
        OPERATOR_MAPPINGS.put("&&", " and ");
        OPERATOR_MAPPINGS.put("||", "or");
        OPERATOR_MAPPINGS.put("==", "==");
        OPERATOR_MAPPINGS.put("=", "=");
        OPERATOR_MAPPINGS.put("<", "<");
        OPERATOR_MAPPINGS.put(">", ">");
        OPERATOR_MAPPINGS.put(">=", ">=");
        OPERATOR_MAPPINGS.put("<=", "<=");
        OPERATOR_MAPPINGS.put("<<", "");


    }

    private ConvertOperator(){

    }

    public static String convert(String input){
        return OPERATOR_MAPPINGS.getOrDefault(input.toLowerCase(),input);
    }
    public static void addMapping(String input, String operator){
        OPERATOR_MAPPINGS.put(input.toLowerCase(), operator);
    }
}
