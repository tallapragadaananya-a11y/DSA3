import java.util.*;

/**
 * Demonstrates correctness (both engines agree) and scaling (Aho-Corasick
 * flat vs KMP linear) as described in the Methodology / Expected Outcome
 * slides.
 *
 * Run: javac *.java && java BenchmarkDemo
 */
public class BenchmarkDemo {

    public static void main(String[] args) {
        String sample = generateSyntheticSample(50_000);
        Map<String, String> fullPanel = generateSyntheticMarkers(80, 12);

        // ---- 1. Correctness check on the full panel ----
        AhoCorasick ac = new AhoCorasick();
        ac.addPatterns(fullPanel);
        ac.build();
        List<Match> acMatches = ac.search(sample);

        List<Match> kmpMatches = KMPMatcher.search(sample, fullPanel);

        Set<Match> acSet = new HashSet<>(acMatches);
        Set<Match> kmpSet = new HashSet<>(kmpMatches);
        System.out.println("Aho-Corasick matches: " + acMatches.size());
        System.out.println("KMP matches:          " + kmpMatches.size());
        System.out.println("Engines agree:        " + acSet.equals(kmpSet));
        System.out.println();

        // ---- 2. Scaling benchmark: time vs panel size ----
        int[] panelSizes = {5, 10, 20, 40, 80};
        System.out.printf("%-12s%-18s%-18s%n", "PanelSize", "AhoCorasick(ms)", "KMP(ms)");

        for (int size : panelSizes) {
            Map<String, String> subPanel = subset(fullPanel, size);

            long acTime = timeAhoCorasick(sample, subPanel);
            long kmpTime = timeKMP(sample, subPanel);

            System.out.printf("%-12d%-18d%-18d%n", size, acTime, kmpTime);
        }
    }

    private static long timeAhoCorasick(String sample, Map<String, String> panel) {
        AhoCorasick engine = new AhoCorasick();
        engine.addPatterns(panel);
        engine.build();
        long start = System.nanoTime();
        engine.search(sample);
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static long timeKMP(String sample, Map<String, String> panel) {
        long start = System.nanoTime();
        KMPMatcher.search(sample, panel);
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static Map<String, String> subset(Map<String, String> full, int n) {
        Map<String, String> sub = new LinkedHashMap<>();
        int i = 0;
        for (Map.Entry<String, String> e : full.entrySet()) {
            if (i++ >= n) break;
            sub.put(e.getKey(), e.getValue());
        }
        return sub;
    }

    // ---- Synthetic data helpers (stand-ins for your Synthetic Data
    // Generator module — replace with real HBB FASTA + injected variants) ----

    private static final char[] BASES = {'A', 'C', 'G', 'T'};

    private static String generateSyntheticSample(int length) {
        Random rnd = new Random(42);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(BASES[rnd.nextInt(4)]);
        }
        return sb.toString();
    }

    private static Map<String, String> generateSyntheticMarkers(int count, int patternLen) {
        Random rnd = new Random(7);
        Map<String, String> markers = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder(patternLen);
            for (int j = 0; j < patternLen; j++) {
                sb.append(BASES[rnd.nextInt(4)]);
            }
            markers.put("MARKER_" + i, sb.toString());
        }
        return markers;
    }
}
