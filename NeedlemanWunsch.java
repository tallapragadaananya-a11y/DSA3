/**
 * Needleman-Wunsch dynamic-programming aligner.
 *
 * Where this fits in the project
 * ------------------------------
 * Aho-Corasick and KMP answer "does this EXACT 21-mer occur in the sample?".
 * Needleman-Wunsch answers a different question: "how similar are these two
 * sequences, and exactly where do they differ?". That gives us two things the
 * exact engines cannot do:
 *
 *   1. GLOBAL alignment  (align)           - sample vs reference, base by base.
 *        Used by VariantCaller to list SNPs / insertions / deletions.
 *   2. SEMI-GLOBAL alignment (alignSemiGlobal) - a short marker vs a long sample,
 *        where the marker must be aligned end-to-end but the sample may
 *        overhang for free on both sides. Used by FuzzyMarkerMatcher to find
 *        "near matches" (markers that are 1-2 bases away from a hit).
 *
 * Scoring (linear gap penalty), configurable:
 *   match = +1   mismatch = -1   gap = -2
 * The ambiguity code 'N' scores 0 against anything (neither reward nor penalty).
 *
 * Cost: O(m * n) time and memory. That is fine for verifying candidates
 * against a gene-sized sequence (hundreds to tens of thousands of bases), but
 * it is NOT a replacement for Aho-Corasick when scanning very large samples.
 * A size guard throws an exception instead of exhausting memory.
 */
public class NeedlemanWunsch {

    private static final long MAX_CELLS = 60_000_000L;

    private final int match;
    private final int mismatch;
    private final int gap;

    public NeedlemanWunsch() {
        this(1, -1, -2);
    }

    public NeedlemanWunsch(int match, int mismatch, int gap) {
        if (gap > 0) throw new IllegalArgumentException("gap penalty must be <= 0");
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
    }

    private int sub(char x, char y) {
        if (x == 'N' || y == 'N') return 0;
        return x == y ? match : mismatch;
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /** Global alignment: both sequences are aligned end to end. */
    public Alignment align(String a, String b) {
        a = a.toUpperCase();
        b = b.toUpperCase();
        int[][] h = fill(a, b, false);
        return traceback(a, b, h, a.length(), b.length(), false);
    }

    /**
     * Semi-global ("fit") alignment: the whole pattern is aligned, but it may
     * sit anywhere inside text - leading and trailing text is free.
     * Returns the best-scoring placement (leftmost on ties).
     */
    public Alignment alignSemiGlobal(String pattern, String text) {
        pattern = pattern.toUpperCase();
        text = text.toUpperCase();
        int[][] h = fill(pattern, text, true);
        int m = pattern.length();
        int bestJ = 0;
        for (int j = 1; j <= text.length(); j++) {
            if (h[m][j] > h[m][bestJ]) bestJ = j;
        }
        return traceback(pattern, text, h, m, bestJ, true);
    }

    /** The full global score matrix (rows = a, columns = b) - handy for teaching/demo output. */
    public int[][] scoreMatrix(String a, String b) {
        return fill(a.toUpperCase(), b.toUpperCase(), false);
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private int[][] fill(String a, String b, boolean semiGlobal) {
        int m = a.length(), n = b.length();
        if ((long) (m + 1) * (n + 1) > MAX_CELLS) {
            throw new IllegalArgumentException("Alignment matrix too large (" + (m + 1) + " x " + (n + 1)
                    + "). Use Aho-Corasick/KMP to find candidate regions first, then align only those windows.");
        }
        int[][] h = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) h[i][0] = i * gap;                 // pattern/ref gaps are always penalised
        if (!semiGlobal) {
            for (int j = 1; j <= n; j++) h[0][j] = j * gap;
        }                                                               // semi-global: row 0 stays 0 (free leading text)

        for (int i = 1; i <= m; i++) {
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                int diag = h[i - 1][j - 1] + sub(ca, b.charAt(j - 1));
                int up = h[i - 1][j] + gap;     // char of a against a gap in b
                int left = h[i][j - 1] + gap;   // char of b against a gap in a
                h[i][j] = Math.max(diag, Math.max(up, left));
            }
        }
        return h;
    }

    private Alignment traceback(String a, String b, int[][] h, int i, int j, boolean semiGlobal) {
        int score = h[i][j];
        int endB = j;
        StringBuilder ra = new StringBuilder();
        StringBuilder rb = new StringBuilder();

        // Global: run to (0,0). Semi-global: stop as soon as the whole pattern is consumed (i == 0).
        while (i > 0 || (j > 0 && !semiGlobal)) {
            if (i > 0 && j > 0 && h[i][j] == h[i - 1][j - 1] + sub(a.charAt(i - 1), b.charAt(j - 1))) {
                ra.append(a.charAt(i - 1));
                rb.append(b.charAt(j - 1));
                i--;
                j--;
            } else if (i > 0 && h[i][j] == h[i - 1][j] + gap) {
                ra.append(a.charAt(i - 1));
                rb.append('-');
                i--;
            } else {
                ra.append('-');
                rb.append(b.charAt(j - 1));
                j--;
            }
        }
        return new Alignment(ra.reverse().toString(), rb.reverse().toString(), score, j, endB);
    }
}
