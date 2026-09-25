/**
 * Small teaching demo: shows the Needleman-Wunsch score matrix and the
 * resulting alignments for (1) a textbook toy pair, (2) the codon-6 sickle
 * mutation window, (3) an insertion/deletion example.
 *
 * Run: javac *.java && java NeedlemanWunschDemo
 */
public class NeedlemanWunschDemo {

    public static void main(String[] args) {
        NeedlemanWunsch nw = new NeedlemanWunsch();   // +1 / -1 / -2

        // 1. Toy example with the full DP matrix
        String a = "GATTACA", b = "GCATGCA";
        System.out.println("1) Toy example: " + a + " vs " + b + "  (match +1, mismatch -1, gap -2)");
        printMatrix(a, b, nw.scoreMatrix(a, b));
        Alignment toy = nw.align(a, b);
        System.out.println(toy.format(60, "A", "B"));
        System.out.println(toy + "\n");

        // 2. Sickle-cell window: wild-type vs mutant
        String wt = "ACACCATGGTGCATCTGACTCCTGAGGAGAAGTCT";
        String hbs = "ACACCATGGTGCATCTGACTCCTGTGGAGAAGTCT";
        System.out.println("2) Codon-6 window, wild-type vs HbS mutant");
        Alignment sickle = nw.align(wt, hbs);
        System.out.println(sickle.format(60, "reference", "sample"));
        for (Variant v : VariantCaller.fromAlignment(sickle)) System.out.println("   -> " + v);
        System.out.println();

        // 3. Indels: 3-base deletion and 2-base insertion
        String ref = "ACACCATGGTGCATCTGACTCCTGAGGAGAAGTCT";
        String del = "ACACCATGGTGCATCTGACTCCTGAGAAGTCT";      // 'GAG' -> 'G' style shortening (3 bases removed)
        String ins = "ACACCATGGTGCATCTGACTCCTTTGAGGAGAAGTCT";  // 2 bases inserted
        System.out.println("3) Deletion example");
        Alignment d = nw.align(ref, del);
        System.out.println(d.format(60, "reference", "sample"));
        for (Variant v : VariantCaller.fromAlignment(d)) System.out.println("   -> " + v);
        System.out.println("\n   Insertion example");
        Alignment in = nw.align(ref, ins);
        System.out.println(in.format(60, "reference", "sample"));
        for (Variant v : VariantCaller.fromAlignment(in)) System.out.println("   -> " + v);
    }

    private static void printMatrix(String a, String b, int[][] h) {
        StringBuilder sb = new StringBuilder(String.format("%5s%5s", "", ""));
        for (int j = 0; j < b.length(); j++) sb.append(String.format("%5s", b.charAt(j)));
        System.out.println(sb);
        for (int i = 0; i < h.length; i++) {
            sb = new StringBuilder(String.format("%5s", i == 0 ? "" : String.valueOf(a.charAt(i - 1))));
            for (int j = 0; j < h[i].length; j++) sb.append(String.format("%5d", h[i][j]));
            System.out.println(sb);
        }
        System.out.println();
    }
}
