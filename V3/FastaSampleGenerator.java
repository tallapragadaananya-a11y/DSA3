import java.io.*;
import java.util.*;

/**
 * Generates synthetic reference + mutant sample sequences that match
 * MarkerPanel exactly — standing in for your team's actual
 * data/HBB_reference.fasta and data/sample_*.fasta files until those are
 * wired in. This is deterministic (fixed seed) so results are reproducible
 * across runs and across teammates' machines.
 *
 * Design, matching the markers.py docstring:
 *   - Hotspot 1 (codon 6 region): wild-type contains HBB_REF and the
 *     unmutated codon; the sickle-cell mutant version instead contains
 *     HBB_HbS. Because only a single base differs between the wild-type and
 *     mutant windows here, HBB_REF still matches in the sickle sample too —
 *     exactly as the docstring says it should.
 *   - Hotspot 2 (separate synthetic locus): wild-type is one base off from
 *     HBB_BETA0; the beta-thalassemia mutant version matches it exactly.
 *   - sample_combined carries both mutations at once.
 */
public class FastaSampleGenerator {

    private static final char[] BASES = {'A', 'C', 'G', 'T'};
    private static final int BACKBONE_LENGTH = 300;
    private static final long SEED = 2026L;

    // Hotspot 1: overlaps HBB_REF (first 20 nt) and, when mutated, HBB_HbS (last 21 nt)
    private static final int HOTSPOT1_OFFSET = 60;
    private static final String HOTSPOT1_WILDTYPE = "ACACCATGGTGCATCTGACTCCTGAGGAGAAGTCT"; // wild-type codon 6 (GAG)
    private static final String HOTSPOT1_MUTANT = "ACACCATGGTGCATCTGACTCCTGTGGAGAAGTCT";   // mutant codon 6 (GTG) -> HBB_HbS

    // Hotspot 2: unrelated synthetic beta-thalassemia teaching locus
    private static final int HOTSPOT2_OFFSET = 160;
    private static final String HOTSPOT2_WILDTYPE = "AGTTGGTGGTAAGGCCATGGG"; // one base off HBB_BETA0
    private static final String HOTSPOT2_MUTANT = "AGTTGGTGGTAAGGCCCTGGG";   // == HBB_BETA0 exactly

    /**
     * Returns sampleId -> sequence for the four samples referenced in
     * markers.py: HBB_reference, sample_sickle_cell, sample_beta_thalassemia,
     * sample_combined.
     */
    public static Map<String, String> generateSamples() {
        String backbone = randomBackbone(BACKBONE_LENGTH, SEED);

        String reference = insert(insert(backbone, HOTSPOT1_OFFSET, HOTSPOT1_WILDTYPE),
                HOTSPOT2_OFFSET, HOTSPOT2_WILDTYPE);
        String sickle = insert(insert(backbone, HOTSPOT1_OFFSET, HOTSPOT1_MUTANT),
                HOTSPOT2_OFFSET, HOTSPOT2_WILDTYPE);
        String beta = insert(insert(backbone, HOTSPOT1_OFFSET, HOTSPOT1_WILDTYPE),
                HOTSPOT2_OFFSET, HOTSPOT2_MUTANT);
        String combined = insert(insert(backbone, HOTSPOT1_OFFSET, HOTSPOT1_MUTANT),
                HOTSPOT2_OFFSET, HOTSPOT2_MUTANT);

        Map<String, String> samples = new LinkedHashMap<>();
        samples.put("HBB_reference", reference);
        samples.put("sample_sickle_cell", sickle);
        samples.put("sample_beta_thalassemia", beta);
        samples.put("sample_combined", combined);
        return samples;
    }

    private static String insert(String backbone, int offset, String window) {
        StringBuilder sb = new StringBuilder(backbone);
        sb.replace(offset, offset + window.length(), window);
        return sb.toString();
    }

    private static String randomBackbone(int length, long seed) {
        Random rnd = new Random(seed);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(BASES[rnd.nextInt(4)]);
        }
        return sb.toString();
    }

    /** Writes a sequence to a FASTA file, wrapped at 70 chars/line (standard convention). */
    public static void writeFasta(String path, String sampleId, String sequence) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write(">" + sampleId);
            w.newLine();
            for (int i = 0; i < sequence.length(); i += 70) {
                w.write(sequence.substring(i, Math.min(i + 70, sequence.length())));
                w.newLine();
            }
        }
    }

    /** Reads a single-sequence FASTA file back, stripping the header line. */
    public static String readFasta(String path) throws IOException {
        StringBuilder seq = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith(">")) continue;
                seq.append(line.trim());
            }
        }
        return seq.toString();
    }

    /** Writes all four generated samples to a directory, e.g. writeAll("data") -> data/*.fasta */
    public static void writeAll(String directory) throws IOException {
        new File(directory).mkdirs();
        for (Map.Entry<String, String> e : generateSamples().entrySet()) {
            writeFasta(directory + File.separator + e.getKey() + ".fasta", e.getKey(), e.getValue());
        }
    }
}
