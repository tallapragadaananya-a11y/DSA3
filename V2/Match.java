/**
 * Represents a single marker match found in a DNA sample.
 *
 * markerId          - identifier of the disease marker (e.g. "HBB_E6V")
 * position           - 0-based start index of the match within the sample
 * matchedSubstring    - the exact substring that matched (== the pattern text)
 */
public class Match {
    public final String markerId;
    public final int position;
    public final String matchedSubstring;

    public Match(String markerId, int position, String matchedSubstring) {
        this.markerId = markerId;
        this.position = position;
        this.matchedSubstring = matchedSubstring;
    }

    @Override
    public String toString() {
        return "Match{marker=" + markerId +
                ", pos=" + position +
                ", pattern='" + matchedSubstring + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match m = (Match) o;
        return position == m.position
                && markerId.equals(m.markerId)
                && matchedSubstring.equals(m.matchedSubstring);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(markerId, position, matchedSubstring);
    }
}