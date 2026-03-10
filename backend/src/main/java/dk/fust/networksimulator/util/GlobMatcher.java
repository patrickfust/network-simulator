package dk.fust.networksimulator.util;

/**
 * Glob-style path matching that supports {@code *} (any characters) and
 * {@code ?} (any single character) wildcards.
 *
 * <p>All regular-expression metacharacters in the pattern are escaped before
 * compilation so stored patterns are treated as literals with {@code *} and
 * {@code ?} wildcards. Matching is still performed using Java's regular
 * expression engine, so pathological patterns and very long inputs may
 * still lead to slow matches.</p>
 */
public final class GlobMatcher {

    private GlobMatcher() {}

    /**
     * Returns {@code true} if {@code path} matches the {@code glob} pattern.
     *
     * <ul>
     *   <li>{@code null} or empty glob matches everything.</li>
     *   <li>{@code *} matches any sequence of characters (including {@code /}).</li>
     *   <li>{@code ?} matches exactly one character.</li>
     *   <li>All other regex metacharacters are treated as literals.</li>
     * </ul>
     */
    public static boolean matches(String glob, String path) {
        if (glob == null || glob.isEmpty()) {
            return true;
        }
        if (path == null) {
            return false;
        }
        return wildcardMatch(glob, path);
    }

    /**
     * Performs glob-style matching supporting '*' and '?' only.
     * All other characters are treated literally.
     */
    private static boolean wildcardMatch(String pattern, String text) {
        int p = 0;              // index in pattern
        int t = 0;              // index in text
        int starIndex = -1;     // most recent '*' position in pattern
        int matchIndex = 0;     // index in text corresponding to starIndex + 1

        while (t < text.length()) {
            if (p < pattern.length()
                    && (pattern.charAt(p) == '?' || pattern.charAt(p) == text.charAt(t))) {
                // current characters match, or pattern has '?'
                p++;
                t++;
            } else if (p < pattern.length() && pattern.charAt(p) == '*') {
                // record position of '*' and the match position in text
                starIndex = p;
                matchIndex = t;
                p++;
            } else if (starIndex != -1) {
                // backtrack: extend the match for the previous '*'
                p = starIndex + 1;
                matchIndex++;
                t = matchIndex;
            } else {
                return false;
            }
        }

        // consume remaining '*' in pattern
        while (p < pattern.length() && pattern.charAt(p) == '*') {
            p++;
        }

        return p == pattern.length();
    }
    static String globToRegex(String glob) {
        StringBuilder sb = new StringBuilder("^");
        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            switch (c) {
                case '*' -> sb.append(".*");
                case '?' -> sb.append('.');
                // Escape all regex metacharacters so stored patterns are always literal
                case '.', '+', '(', ')', '[', ']', '{', '}', '^', '$', '|', '\\' -> {
                    sb.append('\\');
                    sb.append(c);
                }
                default -> sb.append(c);
            }
        }
        sb.append('$');
        return sb.toString();
    }
}
