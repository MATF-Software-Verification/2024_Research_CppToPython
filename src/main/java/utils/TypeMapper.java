package utils;
import java.util.HashMap;
import java.util.Map;

public class TypeMapper {

    private static final Map<String, String> typeMap = new HashMap<>();

    static{
        typeMap.put("std::string", "str");
        typeMap.put("std::vector", "list");
        typeMap.put("std::list", "list");
        typeMap.put("std::array", "list");
        typeMap.put("std::map", "dict");
        typeMap.put("std::unordered_map", "dict");
        typeMap.put("std::set", "set");
        typeMap.put("int", "int");
        typeMap.put("long", "int");
        typeMap.put("long long", "int");
        typeMap.put("unsigned int", "int");
        typeMap.put("unsigned long", "int");
        typeMap.put("float", "float");
        typeMap.put("double", "float");
        typeMap.put("char", "str");

    }

    public static String mapCppTypeToPython(String cppType) {
        for (String prefix : typeMap.keySet()) {
            if (cppType.equals(prefix) || cppType.startsWith(prefix + "<")) {
                return typeMap.get(prefix);
            }
        }
        return "unknown";
    }


    public static String sanitizeType(String cppType) {
        String cleaned = cppType
                .replaceAll("\\bconst\\b", "")
                .replaceAll("\\bvolatile\\b", "")
                .replaceAll("[*&]", "")
                .trim();

        // Step 2: Normalize spacing
        cleaned = cleaned.replaceAll("\\s+", " ");

        // Step 3: Use the mapping
        return mapCppTypeToPython(cleaned);
    }

}
