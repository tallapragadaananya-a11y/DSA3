import java.io.IOException;
import java.util.*;

/**
 * Full screening pipeline on the four synthetic samples:
 *
 *   Stage 1  Aho-Corasick exact scan (cross-checked against KMP)      -> "what markers are present?"
 *   Stage 2  Needleman-Wunsch global alignment of sample vs reference -> "what exactly differs?"
 *            and a cross-check: is each exact hit backed by a real variant?
 *   Stage 3  Needleman-Wunsch semi-global near-match search           -> "which markers are almost present?"
 *
 * Run: javac *.java && java ScreeningDemo [marker_panel.csv]
 * (On Windows consoles, add -Dstdout.encoding=UTF-8 to see Greek letters / arrows correctly.)
 */
public class ScreeningDemo {

    private static final double NEAR_MATCH_MIN_IDENTITY = 0.90;
    private static final int MAX_NEAR_MATCHES_SHOWN = 5;

    public static void main(String[] args) throws IOException {
        List<Marker> panel = args.length > 0 ? CsvPanelLoader.load(args[0]) : MarkerPanel.load();
        Map<String, Marker> byId = MarkerPanel.indexById(panel);
        System.out.println("Panel: " + panel.size() + " markers" + (args.length > 0 ? " (from " + args[0] + ")" : ""));
        System.out.println();

        AhoCorasick ac = new AhoCorasick();
        ac.addPatterns(panel);
        ac.build();

        NeedlemanWunsch nw = new NeedlemanWunsch();                       // +1 match, -1 mismatch, -2 gap
        FuzzyMarkerMatcher fuzzy = new FuzzyMarkerMatcher(nw, NEAR_MATCH_MIN_IDENTITY);

        Map<String, String> samples = FastaSampleGenerator.generateSamples();
        String reference = samples.get("HBB_reference");

        for (Map.Entry<String, String> sampleEntry : samples.entrySet()) {
            String sampleId = sampleEntry.getKey();
            String sequence = sampleEntry.getValue();

            List<Match> acMatches = ac.search(sequence);
            List<Match> kmpMatches = KMPMatcher.search(sequence, panel);
            boolean agree = new HashSet<>(acMatches).equals(new HashSet<>(kmpMatches));
            List<Variant> variants = VariantCaller.call(reference, sequence, nw);

            System.out.println("=== " + sampleId + " ===");
            System.out.println("Engines agree (AC vs KMP): " + agree);

            // ---- Stage 1 + cross-check ----
            System.out.println("Exact marker hits (Aho-Corasick):");
            if (acMatches.isEmpty()) System.out.println("  none");
            Set<String> exactIds = new HashSet<>();
            for (Match m : acMatches) {
                exactIds.add(m.markerId);
                Marker marker = byId.get(m.markerId);
                System.out.printf("  [%s] %s | gene=%s | %s | severity=%s | pos=%d%n",
                        marker.id, marker.disease, marker.gene, marker.mutationType, marker.severity, m.position);

                List<String> supporting = new ArrayList<>();
                for (Variant v : variants) {
                    if (v.inSampleWindow(m.position, m.position + m.matchedSubstring.length())) {
                        supporting.add(v.toString());
                    }
                }
                if (!supporting.isEmpty()) {
                    System.out.println("      NW check: backed by variant vs reference -> " + String.join("; ", supporting));
                } else if ("none".equalsIgnoreCase(marker.severity)) {
                    System.out.println("      NW check: no variant in window (expected for a reference/control marker)");
                } else {
                    System.out.println("      NW check: WARNING - no variant vs reference in this window; the pattern equals the"
                            + " reference sequence here, so this hit is probably not a real variant signature.");
                }
            }

            // ---- Stage 2 ----
            System.out.println("Variants vs HBB_reference (Needleman-Wunsch global alignment):");
            if (variants.isEmpty()) System.out.println("  none (identical to reference)");
            for (Variant v : variants) System.out.println("  " + v);

            // ---- Stage 3 ----
            List<FuzzyMatch> near = fuzzy.findNearMatches(sequence, panel, exactIds);
            System.out.printf("Near matches (no exact hit, identity >= %.0f%%) - NOT detections:%n",
                    NEAR_MATCH_MIN_IDENTITY * 100);
            if (near.isEmpty()) System.out.println("  none");
            int shown = 0;
            for (FuzzyMatch fm : near) {
                if (shown++ >= MAX_NEAR_MATCHES_SHOWN) {
                    System.out.println("  ... and " + (near.size() - MAX_NEAR_MATCHES_SHOWN) + " more");
                    break;
                }
                System.out.printf("  [%s] identity=%.1f%% | %d difference(s) | sample window [%d,%d)%n",
                        fm.markerId, fm.identity() * 100, fm.differences(), fm.alignment.startB, fm.alignment.endB);
                for (String l : fm.alignment.format(60, "    marker", "    sample").split("\\R")) {
                    System.out.println(l);
                }
            }
            System.out.println();
        }

        // Uncomment to persist the generated samples as real .fasta files:
        // FastaSampleGenerator.writeAll("data");
    }
}
