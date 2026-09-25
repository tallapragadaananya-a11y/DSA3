/** A marker's best approximate placement in a sample (from semi-global Needleman-Wunsch). */
public class FuzzyMatch {
    public final String markerId;
    public final Alignment alignment;

    public FuzzyMatch(String markerId, Alignment alignment) {
        this.markerId = markerId;
        this.alignment = alignment;
    }

    /** 0-based start of the aligned window in the sample. */
    public int position() { return alignment.startB; }

    public double identity() { return alignment.identity(); }

    /** Substitutions + gap columns: a simple "edit distance" style count. */
    public int differences() { return alignment.mismatches + alignment.gaps; }

    @Override
    public String toString() {
        return "FuzzyMatch{" + markerId + ", pos=" + position()
                + ", identity=" + String.format("%.1f%%", identity() * 100) + "}";
    }
}
