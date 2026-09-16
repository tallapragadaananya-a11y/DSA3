import java.util.*;

/**
 * Direct Java port of your markers.py panel. Same three markers, same
 * patterns, same metadata — this is the single source of truth both engines
 * and the demo classes should load the panel from, instead of hardcoding
 * marker strings in multiple places.
 */
public class MarkerPanel {

    public static List<Marker> load() {
        List<Marker> panel = new ArrayList<>();

        panel.add(new Marker(
                "HBB_HbS",
                "CTGACTCCTGTGGAGAAGTCT",
                "HBB",
                "Sickle Cell Disease",
                "Point mutation (missense), beta-globin codon 6 Glu>Val",
                "high"
        ));

        panel.add(new Marker(
                "HBB_BETA0",
                "AGTTGGTGGTAAGGCCCTGGG",
                "HBB",
                "Beta-Thalassemia (synthetic teaching marker)",
                "Point mutation (synthetic panel marker)",
                "high"
        ));

        panel.add(new Marker(
                "HBB_REF",
                "ACACCATGGTGCATCTGACT",
                "HBB",
                "Wild-type HBB control region",
                "Reference / no variant",
                "none"
        ));

        return panel;
    }

    /** Flattens the panel to markerId -> pattern, for the older Map-based engine methods. */
    public static Map<String, String> asPatternMap(List<Marker> markers) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Marker m : markers) {
            map.put(m.id, m.pattern);
        }
        return map;
    }

    /** Lookup table for turning a Match.markerId back into full clinical metadata. */
    public static Map<String, Marker> indexById(List<Marker> markers) {
        Map<String, Marker> map = new LinkedHashMap<>();
        for (Marker m : markers) {
            map.put(m.id, m);
        }
        return map;
    }
}