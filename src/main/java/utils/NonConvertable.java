package utils;

import java.util.ArrayList;
import java.util.List;

public class NonConvertable {

    private static final List<String> NonConvertableString = new ArrayList<>();

    static {
        NonConvertableString.add("std::endl");
        NonConvertableString.add("endl");
        NonConvertableString.add("\\n");
    }

    private NonConvertable(){}

    public static boolean hasValue(String value) {
        String normalized = value.trim();

        // Strip surrounding quotes if present
        if (normalized.startsWith("\"") && normalized.endsWith("\"") && normalized.length() >= 2) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }

        return NonConvertableString.contains(normalized.toLowerCase());
    }
}
