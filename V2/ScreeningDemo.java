import java.io.IOException;
import java.util.*;

/**
 * Runs the actual HBB marker panel (ported from markers.py) against the four
 * synthetic samples, using both engines, and prints a clinician-style match
 * report per sample. This is the "single scan, structured report" demo your
 * Expected Outcome slide describes — separate from BenchmarkDemo.java, which
 * focuses purely on scaling with a larger synthetic panel.
 *
 * Run: javac *.java && java ScreeningDemo
 */
public class ScreeningDemo {

    public static void main(String[] args) throws IOException {
        List<Marker> panel = MarkerPanel.load();
        Map<String, Marker> byId = MarkerPanel.indexById(panel);

        AhoCorasick ac = new AhoCorasick();
        ac.addPatterns(panel);
        ac.build();

        Map<String, String> samples = FastaSampleGenerator.generateSamples();

        for (Map.Entry<String, String> sampleEntry : samples.entrySet()) {
            String sampleId = sampleEntry.getKey();
            String sequence = sampleEntry.getValue();

            List<Match> acMatches = ac.search(sequence);
            List<Match> kmpMatches = KMPMatcher.search(sequence, panel);
            boolean agree = new HashSet<>(acMatches).equals(new HashSet<>(kmpMatches));

            System.out.println("=== " + sampleId + " ===");
            System.out.println("Engines agree: " + agree);

            if (acMatches.isEmpty()) {
                System.out.println("  No markers detected.");
            }
            for (Match m : acMatches) {
                Marker marker = byId.get(m.markerId);
                System.out.printf("  [%s] %s | gene=%s | %s | severity=%s | pos=%d%n",
                        marker.id, marker.disease, marker.gene,
                        marker.mutationType, marker.severity, m.position);
            }
            System.out.println();
        }

        // Uncomment to persist the generated samples as real .fasta files
        // (e.g. so they can sit alongside your team's actual reference data):
        // FastaSampleGenerator.writeAll("data");
    }
}