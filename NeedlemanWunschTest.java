import java.util.*;

/**
 * Dependency-free self-checks for the alignment code.
 * Run: javac *.java && java NeedlemanWunschTest
 */
public class NeedlemanWunschTest {
    private static int failures = 0;

    public static void main(String[] args) {
        NeedlemanWunsch nw = new NeedlemanWunsch();

        // identical sequences: score = length, no differences
        Alignment same = nw.align("ACGTACGT", "ACGTACGT");
        check("identical score", same.score == 8);
        check("identical identity", same.identity() == 1.0);

        // one substitution: 7 matches - 1 mismatch
        Alignment sub = nw.align("ACGTACGT", "ACGAACGT");
        check("one substitution score", sub.score == 6);
        List<Variant> v1 = VariantCaller.fromAlignment(sub);
        check("one SNP called", v1.size() == 1 && v1.get(0).type == Variant.Type.SNP
                && v1.get(0).refPos == 3 && v1.get(0).ref.equals("T") && v1.get(0).alt.equals("A"));

        // deletion / insertion
        List<Variant> vd = VariantCaller.call("ACGTTTACGT", "ACGTTACGT", nw);
        check("one deletion called", vd.size() == 1 && vd.get(0).type == Variant.Type.DELETION);
        List<Variant> vi = VariantCaller.call("ACGTACGT", "ACGTAACGT", nw);
        check("one insertion called", vi.size() == 1 && vi.get(0).type == Variant.Type.INSERTION);

        // aligned rows strip back to the inputs
        Alignment x = nw.align("GATTACA", "GCATGCA");
        check("rows strip to A", x.alignedA.replace("-", "").equals("GATTACA"));
        check("rows strip to B", x.alignedB.replace("-", "").equals("GCATGCA"));
        check("equal row lengths", x.alignedA.length() == x.alignedB.length());

        // semi-global: exact placement inside a longer text
        Alignment sg = nw.alignSemiGlobal("GATTACA", "CCCCGATTACATTTT");
        check("semi-global exact score", sg.score == 7);
        check("semi-global window", sg.startB == 4 && sg.endB == 11);

        // semi-global with a mismatch is still located
        Alignment sg2 = nw.alignSemiGlobal("GATTACA", "CCCCGATTGCATTTT");
        check("semi-global mismatch located", sg2.startB == 4 && sg2.mismatches == 1);

        // 'N' is a no-call, not a variant
        check("N is not a SNP", VariantCaller.call("ACGTACGT", "ACGNACGT", nw).isEmpty());

        // lower case is accepted
        check("case-insensitive", nw.align("acgt", "ACGT").score == 4);

        System.out.println(failures == 0 ? "\nAll checks passed." : "\n" + failures + " check(s) FAILED.");
        if (failures > 0) System.exit(1);
    }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
        if (!ok) failures++;
    }
}
