import java.util.*;

/**
 * Knuth-Morris-Pratt baseline engine.
 *
 * This is deliberately the "naive-but-optimal-per-pattern" approach your
 * Problem Statement slide describes: the DNA sample is re-scanned once for
 * every marker in the panel, so total work is O(n * k) for k markers of
 * average length m (ignoring m, since KMP itself is O(n+m) per pattern).
 * This is what should show LINEAR growth in your benchmark graph.
 */
public class KMPMatcher {

    /**
     * Searches the sample for every marker in the panel, one marker at a time.
     *
     * @param sample  the DNA sequence to scan (e.g. "ACGTACGT...")
     * @param markers map of markerId -> pattern (the mutation signature)
     */
    public static List<Match> search(String sample, Map<String, String> markers) {
        List<Match> results = new ArrayList<>();
        for (Map.Entry<String, String> entry : markers.entrySet()) {
            results.addAll(searchSingle(sample, entry.getValue(), entry.getKey()));
        }
        return results;
    }

    /** Convenience overload for loading a panel straight from MarkerPanel.load(). */
    public static List<Match> search(String sample, List<Marker> markers) {
        List<Match> results = new ArrayList<>();
        for (Marker m : markers) {
            results.addAll(searchSingle(sample, m.pattern, m.id));
        }
        return results;
    }

    /** Classic single-pattern KMP search. */
    public static List<Match> searchSingle(String text, String pattern, String markerId) {
        List<Match> matches = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int[] lps = buildLPS(pattern);
        int i = 0; // index into text
        int j = 0; // index into pattern

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(new Match(markerId, i - j, pattern));
                    j = lps[j - 1]; // continue looking for overlapping matches
                }
            } else if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }

    /** Builds the "longest proper prefix which is also a suffix" table. */
    private static int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }
}