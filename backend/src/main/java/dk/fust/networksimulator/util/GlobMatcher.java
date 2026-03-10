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
        String regex = globToRegex(glob);
        return path != null && path.matches(regex);
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
