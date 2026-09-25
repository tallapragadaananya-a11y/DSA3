import java.io.IOException;
import java.util.*;

/**
 * Correctness + scaling benchmark for the three engines:
 *   Aho-Corasick (one pass), KMP (one pass per marker),
 *   Needleman-Wunsch semi-global (one O(m*n) alignment per marker).
 *
 * Expected shape: AC ~flat, KMP ~linear in panel size, NW ~linear AND with a much
 * larger constant. NW is a verification tool, not a scanner - that contrast is the point.
 *
 * Run: javac *.java && java BenchmarkDemo [marker_panel.csv]
 * With no argument a synthetic 80-marker, 12-bp panel is used.
 */
public class BenchmarkDemo {

    private static final int SAMPLE_LENGTH = 50_000;
    private static final int RUNS = 5;                 // each timing is the median of RUNS runs
    private static volatile int sink;                  // stops the JIT discarding results

    public static void main(String[] args) throws IOException {
        Map<String, String> fullPanel;
        if (args.length > 0) {
            fullPanel = MarkerPanel.asPatternMap(CsvPanelLoader.load(args[0]));
            System.out.println("Panel: " + fullPanel.size() + " markers from " + args[0]);
        } else {
            fullPanel = generateSyntheticMarkers(80, 12);
            System.out.println("Panel: 80 synthetic markers (12 bp)");
        }

        // Plant every marker in the random sample so the correctness check has real matches.
        String sample = plantMarkers(generateSyntheticSample(SAMPLE_LENGTH), fullPanel);
        NeedlemanWunsch nw = new NeedlemanWunsch();

        // ---- 1. Correctness check on the full panel ----
        AhoCorasick ac = new AhoCorasick();
        ac.addPatterns(fullPanel);
        ac.build();
        List<Match> acMatches = ac.search(sample);
        List<Match> kmpMatches = KMPMatcher.search(sample, fullPanel);

        boolean agree = new HashSet<>(acMatches).equals(new HashSet<>(kmpMatches));
        Set<String> found = new HashSet<>();
        for (Match m : acMatches) found.add(m.markerId);

        boolean nwPerfect = true;
        for (Map.Entry<String, String> e : fullPanel.entrySet()) {
            Alignment a = nw.alignSemiGlobal(e.getValue(), sample);
            if (a.mismatches + a.gaps != 0) nwPerfect = false;
        }
        System.out.println("Aho-Corasick matches: " + acMatches.size());
        System.out.println("KMP matches:          " + kmpMatches.size());
        System.out.println("Engines agree:        " + agree);
        System.out.println("All planted markers found by AC: " + (found.size() == fullPanel.size()));
        System.out.println("NW semi-global finds a perfect (0-difference) alignment for every marker: " + nwPerfect);
        System.out.println();

        // ---- 2. Scaling benchmark: median time vs panel size ----
        List<Integer> sizes = new ArrayList<>();
        for (int s : new int[]{5, 10, 20, 40, 80, 160}) if (s < fullPanel.size()) sizes.add(s);
        sizes.add(fullPanel.size());

        // warm-up so the JIT has compiled everything before timing starts
        for (int w = 0; w < 30; w++) {
            timeAhoCorasick(sample, subset(fullPanel, Math.min(10, fullPanel.size())));
            timeKMP(sample, subset(fullPanel, Math.min(10, fullPanel.size())));
            timeNW(nw, sample, subset(fullPanel, Math.min(5, fullPanel.size())));
        }

        System.out.printf("Sample length: %,d bp, median of %d runs, times in ms%n", sample.length(), RUNS);
        System.out.printf("%-11s%-16s%-16s%-14s%-16s%n", "PanelSize", "AC build", "AC scan", "KMP", "NW semi-global");
        for (int size : sizes) {
            Map<String, String> subPanel = subset(fullPanel, size);
            double acBuild = median(() -> buildAC(subPanel));
            AhoCorasick engine = buildAC(subPanel);
            double acScan = median(() -> sink += engine.search(sample).size());
            double kmp = median(() -> timeKMPOnce(sample, subPanel));
            double nwMs = median(() -> timeNWOnce(nw, sample, subPanel));
            System.out.printf("%-11d%-16.3f%-16.3f%-14.3f%-16.3f%n", size, acBuild, acScan, kmp, nwMs);
        }
    }

    // ---- timing helpers ----

    private static double median(Runnable r) {
        long[] t = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            r.run();
            t[i] = System.nanoTime() - start;
        }
        Arrays.sort(t);
        return t[RUNS / 2] / 1_000_000.0;
    }

    private static AhoCorasick buildAC(Map<String, String> panel) {
        AhoCorasick engine = new AhoCorasick();
        engine.addPatterns(panel);
        engine.build();
        return engine;
    }

    private static void timeAhoCorasick(String sample, Map<String, String> panel) {
        sink += buildAC(panel).search(sample).size();
    }

    private static void timeKMP(String sample, Map<String, String> panel) {
        timeKMPOnce(sample, panel);
    }

    private static void timeNW(NeedlemanWunsch nw, String sample, Map<String, String> panel) {
        timeNWOnce(nw, sample, panel);
    }

    private static void timeKMPOnce(String sample, Map<String, String> panel) {
        sink += KMPMatcher.search(sample, panel).size();
    }

    private static void timeNWOnce(NeedlemanWunsch nw, String sample, Map<String, String> panel) {
        int total = 0;
        for (String p : panel.values()) total += nw.alignSemiGlobal(p, sample).score;
        sink += total;
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

    // ---- Synthetic data helpers (stand-ins for real HBB FASTA + injected variants) ----

    private static final char[] BASES = {'A', 'C', 'G', 'T'};

    private static String generateSyntheticSample(int length) {
        Random rnd = new Random(42);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(BASES[rnd.nextInt(4)]);
        return sb.toString();
    }

    /** Overwrites the sample with each marker at evenly spaced positions. */
    private static String plantMarkers(String sample, Map<String, String> panel) {
        StringBuilder sb = new StringBuilder(sample);
        int step = sample.length() / (panel.size() + 1);
        int pos = step;
        for (String p : panel.values()) {
            if (pos + p.length() > sb.length()) break;
            sb.replace(pos, pos + p.length(), p);
            pos += step;
        }
        return sb.toString();
    }

    private static Map<String, String> generateSyntheticMarkers(int count, int patternLen) {
        Random rnd = new Random(7);
        Map<String, String> markers = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder(patternLen);
            for (int j = 0; j < patternLen; j++) sb.append(BASES[rnd.nextInt(4)]);
            markers.put("MARKER_" + i, sb.toString());
        }
        return markers;
    }
}