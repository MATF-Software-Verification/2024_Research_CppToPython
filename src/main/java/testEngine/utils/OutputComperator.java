package testEngine.utils;

public class OutputComperator {

    public static boolean compare(String expected, String actual) {
        return normalize(expected).equals(normalize(actual));
    }

    public static String getDiff(String expected, String actual) {
        String[] expectedLines = normalize(expected).split("\n");
        String[] actualLines = normalize(actual).split("\n");

        StringBuilder diff = new StringBuilder();
        int max = Math.max(expectedLines.length, actualLines.length);

        for (int i = 0; i < max; i++) {
            String eLine = i < expectedLines.length ? expectedLines[i] : "<missing>";
            String aLine = i < actualLines.length ? actualLines[i] : "<missing>";

            if (!eLine.equals(aLine)) {
                diff.append(String.format("Line %d:\n", i + 1));
                diff.append(String.format("Expected: %s\n", eLine));
                diff.append(String.format("Actual:   %s\n\n", aLine));
            }
        }

        return diff.toString().isEmpty() ? "No diff — outputs match." : diff.toString();
    }

    private static String normalize(String s) {
        return s.replaceAll("[ \\t]+", " ")     // normalize spacing
                .replaceAll("\\r\\n?", "\n")    // normalize line endings
                .trim();
    }
}
