package utils;

import java.util.List;

public class ConvertStreamOutput {

    public static String convert(List<String> children){

        if(children == null || children.isEmpty()){
            return "";
        }
        StringBuilder fstring = new StringBuilder("print(f\"");
        for(int i = 1; i < children.size(); i++){
            String part = children.get(i).trim();
            if(NonConvertable.hasValue(part)){
                continue;
            }
            if(isStringLiteral(part)){
                fstring.append(stripQuotes(part));
            }else{
                fstring.append("{").append(part).append("}");
            }
        }
        fstring.append("\")");
        return fstring.toString();
    }

    private static boolean isStringLiteral(String string){
        return string.startsWith("\"") && string.endsWith("\"");
    }
    private static String stripQuotes(String string){
        return string.substring(1, string.length() - 1);
    }
}
