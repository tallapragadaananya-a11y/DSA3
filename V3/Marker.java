/**
 * One entry in the HBB marker panel — the Java equivalent of a dict in
 * markers.py. Holds the pattern itself plus the clinical metadata your
 * Proposed Functionalities slide calls for (gene, mutation type, severity),
 * so the report generator has something to work with beyond a bare match.
 */
public class Marker {
    public final String id;
    public final String pattern;
    public final String gene;
    public final String disease;
    public final String mutationType;
    public final String severity;

    public Marker(String id, String pattern, String gene, String disease,
                  String mutationType, String severity) {
        this.id = id;
        this.pattern = pattern;
        this.gene = gene;
        this.disease = disease;
        this.mutationType = mutationType;
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "Marker{" + id + ", pattern='" + pattern + "', disease='" + disease + "'}";
    }
}
