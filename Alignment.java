/**
 * Result of one pairwise alignment (global or semi-global).
 *
 * alignedA / alignedB are equal-length strings where '-' marks a gap.
 * By convention in this project:
 *   A = reference sequence (or the marker pattern in semi-global mode)
 *   B = sample sequence    (or the long text in semi-global mode)
 *
 * startB / endB give the 0-based half-open range [startB, endB) of sequence B
 * that took part in the alignment. For a global alignment that is the whole
 * of B; for a semi-global alignment it is the window of the sample where the
 * marker was best placed.
 */
public class Alignment {
    public final String alignedA;
    public final String alignedB;
    public final int score;
    public final int startB;
    public final int endB;

    public final int matches;
    public final int mismatches;
    public final int gaps;      // total '-' characters across both rows
    public final int columns;   // alignment length

    public Alignment(String alignedA, String alignedB, int score, int startB, int endB) {
        this.alignedA = alignedA;
        this.alignedB = alignedB;
        this.score = score;
        this.startB = startB;
        this.endB = endB;

        int mt = 0, mm = 0, gp = 0;
        for (int i = 0; i < alignedA.length(); i++) {
            char x = alignedA.charAt(i), y = alignedB.charAt(i);
            if (x == '-' || y == '-') gp++;
            else if (x == y) mt++;
            else mm++;
        }
        this.matches = mt;
        this.mismatches = mm;
        this.gaps = gp;
        this.columns = alignedA.length();
    }

    /** matches / alignment columns, in the range 0..1. */
    public double identity() {
        return columns == 0 ? 0.0 : (double) matches / columns;
    }

    /** '|' = match, '.' = mismatch, ' ' = gap. Same length as the aligned rows. */
    public String midline() {
        StringBuilder sb = new StringBuilder(columns);
        for (int i = 0; i < columns; i++) {
            char x = alignedA.charAt(i), y = alignedB.charAt(i);
            sb.append(x == '-' || y == '-' ? ' ' : (x == y ? '|' : '.'));
        }
        return sb.toString();
    }

    /** Three-line pretty print (A / midline / B), wrapped every `width` columns. */
    public String format(int width, String labelA, String labelB) {
        String mid = midline();
        int pad = Math.max(labelA.length(), labelB.length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns; i += width) {
            int end = Math.min(i + width, columns);
            sb.append(String.format("%-" + pad + "s  %s%n", labelA, alignedA.substring(i, end)));
            sb.append(String.format("%-" + pad + "s  %s%n", "", mid.substring(i, end)));
            sb.append(String.format("%-" + pad + "s  %s%n", labelB, alignedB.substring(i, end)));
            if (end < columns) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Alignment{score=" + score + ", identity=" + String.format("%.1f%%", identity() * 100)
                + ", matches=" + matches + ", mismatches=" + mismatches + ", gaps=" + gaps
                + ", B=[" + startB + "," + endB + ")}";
    }
}
