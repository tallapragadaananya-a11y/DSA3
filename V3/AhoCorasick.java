import java.util.*;

/**
 * Aho-Corasick multi-pattern matching engine.
 *
 * Insert every disease marker into a trie, run a BFS to build failure links
 * (converting the trie into a finite-state automaton), then scan the DNA
 * sample ONCE regardless of how many markers are in the panel. This is what
 * should show FLAT/near-constant scan time in your benchmark graph as the
 * panel grows — the core proof-of-concept result for the project.
 */
public class AhoCorasick {

    /** One trie node = one automaton state. */
    private static class Node {
        // Children keyed by base character. A HashMap is used instead of a
        // fixed 4-slot array so the engine tolerates ambiguity codes (N, R,
        // Y, etc.) that show up in real reference sequences, not just ACGT.
        final Map<Character, Node> children = new HashMap<>();
        Node fail;
        // Patterns that end AT this state, inherited from failure links too,
        // so a single visit to a node reports every marker it terminates.
        final List<PatternEntry> output = new ArrayList<>();
    }

    private static class PatternEntry {
        final String markerId;
        final String pattern;

        PatternEntry(String markerId, String pattern) {
            this.markerId = markerId;
            this.pattern = pattern;
        }
    }

    private final Node root = new Node();
    private boolean built = false;

    /**
     * Adds a marker to the panel. Call build() after adding markers and
     * before calling search(). Adding markers again after a rebuild lets you
     * grow the panel without re-scanning samples already processed with the
     * previous automaton.
     */
    public void addPattern(String markerId, String pattern) {
        if (pattern == null || pattern.isEmpty()) return;
        Node cur = root;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            cur = cur.children.computeIfAbsent(c, k -> new Node());
        }
        cur.output.add(new PatternEntry(markerId, pattern));
        built = false;
    }

    public void addPatterns(Map<String, String> markers) {
        for (Map.Entry<String, String> e : markers.entrySet()) {
            addPattern(e.getKey(), e.getValue());
        }
    }

    /** Convenience overload for loading a panel straight from MarkerPanel.load(). */
    public void addPatterns(List<Marker> markers) {
        for (Marker m : markers) {
            addPattern(m.id, m.pattern);
        }
    }

    /**
     * Builds failure links via BFS, turning the trie into an automaton.
     * O(total characters across all patterns) — independent of sample length.
     */
    public void build() {
        Deque<Node> queue = new ArrayDeque<>();

        // Depth-1 nodes fail back to root.
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            for (Map.Entry<Character, Node> e : cur.children.entrySet()) {
                char c = e.getKey();
                Node child = e.getValue();

                Node f = cur.fail;
                while (f != null && !f.children.containsKey(c)) {
                    f = f.fail;
                }
                child.fail = (f == null) ? root : f.children.get(c);
                if (child.fail == child) child.fail = root;

                // Inherit output from the failure link so one visit to
                // `child` reports every pattern ending there, including
                // shorter markers that are suffixes of longer ones.
                child.output.addAll(child.fail.output);

                queue.add(child);
            }
        }
        built = true;
    }

    /**
     * Scans the sample in a single pass and returns every marker match.
     * O(n + total matches) where n = sample length.
     */
    public List<Match> search(String sample) {
        if (!built) build();

        List<Match> results = new ArrayList<>();
        Node cur = root;

        for (int i = 0; i < sample.length(); i++) {
            char c = sample.charAt(i);

            while (cur != root && !cur.children.containsKey(c)) {
                cur = cur.fail;
            }
            cur = cur.children.getOrDefault(c, root);

            for (PatternEntry pe : cur.output) {
                int start = i - pe.pattern.length() + 1;
                results.add(new Match(pe.markerId, start, pe.pattern));
            }
        }
        return results;
    }
}
