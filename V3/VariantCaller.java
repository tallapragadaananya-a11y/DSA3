import java.util.*;

/**
 * Turns a Needleman-Wunsch global alignment (reference vs sample) into a list
 * of SNPs, insertions and deletions.
 *
 * Runs of consecutive gap columns are merged into one insertion/deletion.
 * Columns involving the ambiguity code 'N' are treated as "no call" and skipped.
 */
public class VariantCaller {

    public static List<Variant> call(String reference, String sample, NeedlemanWunsch nw) {
        return fromAlignment(nw.align(reference, sample));
    }

    public static List<Variant> fromAlignment(Alignment aln) {
        List<Variant> out = new ArrayList<>();
        String A = aln.alignedA, B = aln.alignedB;
        int cols = A.length();
        int r = 0, s = 0, i = 0;

        while (i < cols) {
            char x = A.charAt(i), y = B.charAt(i);
            if (x != '-' && y != '-') {                         // aligned pair
                if (x != y && x != 'N' && y != 'N') {
                    out.add(new Variant(Variant.Type.SNP, r, s, String.valueOf(x), String.valueOf(y)));
                }
                r++; s++; i++;
            } else if (x == '-') {                              // extra bases in the sample
                int sStart = s;
                StringBuilder ins = new StringBuilder();
                while (i < cols && A.charAt(i) == '-') { ins.append(B.charAt(i)); i++; s++; }
                out.add(new Variant(Variant.Type.INSERTION, r, sStart, "", ins.toString()));
            } else {                                            // bases missing from the sample
                int rStart = r;
                StringBuilder del = new StringBuilder();
                while (i < cols && B.charAt(i) == '-') { del.append(A.charAt(i)); i++; r++; }
                out.add(new Variant(Variant.Type.DELETION, rStart, s, del.toString(), ""));
            }
        }
        return out;
    }
}
