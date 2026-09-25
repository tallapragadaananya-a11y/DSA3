import java.util.*;

/**
 * Approximate marker search built on semi-global Needleman-Wunsch.
 *
 * Intended second stage AFTER the exact engines:
 *   1. Aho-Corasick finds exact marker hits in one pass.
 *   2. For markers with no exact hit, this class finds the best approximate
 *      placement in the sample and reports it if identity >= minIdentity.
 *
 * A "near match" is NOT a detection. It means "this sample is N bases away
 * from the marker" - useful for spotting the reference allele next to a
 * disease allele, sequencing errors, or neighbouring variants.
 */
public class FuzzyMarkerMatcher {

    private final NeedlemanWunsch nw;
    private final double minIdentity;

    public FuzzyMarkerMatcher(NeedlemanWunsch nw, double minIdentity) {
        this.nw = nw;
        this.minIdentity = minIdentity;
    }

    public FuzzyMatch bestHit(String sample, Marker marker) {
        return new FuzzyMatch(marker.id, nw.alignSemiGlobal(marker.pattern, sample));
    }

    /**
     * @param exactHitIds marker IDs that already matched exactly (skipped here)
     * @return near matches, best identity first
     */
    public List<FuzzyMatch> findNearMatches(String sample, List<Marker> panel, Set<String> exactHitIds) {
        List<FuzzyMatch> out = new ArrayList<>();
        for (Marker m : panel) {
            if (exactHitIds.contains(m.id)) continue;
            FuzzyMatch fm = bestHit(sample, m);
            if (fm.identity() >= minIdentity) out.add(fm);
        }
        out.sort(Comparator.comparingDouble(FuzzyMatch::identity).reversed()
                .thenComparing(fm -> fm.markerId));
        return out;
    }
}
