/**
 * One difference between a sample and the reference, as found by
 * VariantCaller from a Needleman-Wunsch global alignment.
 *
 * Positions are 0-based, in the coordinates of this project's reference
 * sequence (NOT genomic / NM_000518.5 coordinates).
 */
public class Variant {
    public enum Type { SNP, INSERTION, DELETION }

    public final Type type;
    public final int refPos;      // index in the reference where the change sits
    public final int samplePos;   // index in the sample where the change sits
    public final String ref;      // reference allele ("" for an insertion)
    public final String alt;      // sample allele    ("" for a deletion)

    public Variant(Type type, int refPos, int samplePos, String ref, String alt) {
        this.type = type;
        this.refPos = refPos;
        this.samplePos = samplePos;
        this.ref = ref;
        this.alt = alt;
    }

    /** True if the variant starts inside the sample window [from, to). */
    public boolean inSampleWindow(int from, int to) {
        return samplePos >= from && samplePos < to;
    }

    @Override
    public String toString() {
        switch (type) {
            case SNP:       return "SNP       ref[" + refPos + "] " + ref + ">" + alt;
            case INSERTION: return "INSERTION before ref[" + refPos + "] +" + alt;
            default:        return "DELETION  ref[" + refPos + "] -" + ref;
        }
    }
}
