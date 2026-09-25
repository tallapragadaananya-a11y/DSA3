import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Loads a marker panel from a CSV file (e.g. marker_panel.csv) so the panel
 * can be swapped without recompiling.
 *
 * Required columns (header names): marker_id, pattern, gene, disease,
 * description, risk_level. Extra columns are ignored.
 * description -> Marker.mutationType, risk_level -> Marker.severity.
 */
public class CsvPanelLoader {

    public static List<Marker> load(String path) throws IOException {
        List<Marker> panel = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String header = r.readLine();
            if (header == null) throw new IOException("Empty CSV: " + path);
            if (header.startsWith("\uFEFF")) header = header.substring(1);
            List<String> cols = split(header);
            int id = need(cols, "marker_id"), pat = need(cols, "pattern"), gene = need(cols, "gene"),
                dis = need(cols, "disease"), desc = need(cols, "description"), risk = need(cols, "risk_level");

            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                List<String> f = split(line);
                panel.add(new Marker(f.get(id).trim(), f.get(pat).trim().toUpperCase(), f.get(gene).trim(),
                        f.get(dis).trim(), f.get(desc).trim(), f.get(risk).trim()));
            }
        }
        return panel;
    }

    private static int need(List<String> cols, String name) throws IOException {
        int i = cols.indexOf(name);
        if (i < 0) throw new IOException("CSV is missing required column: " + name);
        return i;
    }

    /** Minimal CSV splitter: handles "quoted, fields" and "" escapes (no multi-line fields). */
    private static List<String> split(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQ = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQ) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQ = false;
                } else cur.append(c);
            } else if (c == '"') inQ = true;
            else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        out.add(cur.toString());
        return out;
    }
}
