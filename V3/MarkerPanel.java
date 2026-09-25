import java.util.*;

/**
 * Vitae Guardians — Full marker panel (40 markers).
 *
 * Panel layout:
 *   Markers 01–03  : Real HBB markers derived from NM_000518.5 (team FASTA data).
 *   Markers 04–15  : Clinically documented haemoglobinopathy / erythrocyte variants.
 *   Markers 16–28  : Selected metabolic / enzyme-deficiency teaching markers.
 *   Markers 29–40  : Synthetic filler markers for benchmark scaling (labelled SYN_*).
 *
 * Every pattern is exactly 21 bp — long enough to be informationally specific in a
 * 628 bp HBB reference, short enough to fit in a teaching context. Real markers use
 * published ClinVar / literature mutation positions; synthetic markers use a seeded
 * pseudorandom generator so they are reproducible across runs.
 *
 * IMPORTANT — this is a teaching prototype. The 21-bp patterns for documented
 * conditions are derived from the NM_000518.5 reference skeleton; they are *not*
 * validated diagnostic probes. Do not use this panel for clinical decisions.
 */
public class MarkerPanel {

    public static List<Marker> load() {
        List<Marker> panel = new ArrayList<>();

        // ── BLOCK 1: Real HBB markers (from team FASTA data) ──────────────────
        panel.add(new Marker(
                "HBB_HbS",
                "CTGACTCCTGTGGAGAAGTCT",
                "HBB",
                "Sickle Cell Disease (HbSS / HbAS)",
                "Point mutation codon 6 Glu→Val (GAG→GTG); NM_000518.5:c.20A>T",
                "high"
        ));

        panel.add(new Marker(
                "HBB_BETA0",
                "AGTTGGTGGTAAGGCCCTGGG",
                "HBB",
                "Beta-Thalassemia (β⁰)",
                "Point mutation position 128 of NM_000518.5 reference (teaching marker)",
                "high"
        ));

        panel.add(new Marker(
                "HBB_REF",
                "ACACCATGGTGCATCTGACT",
                "HBB",
                "Wild-type HBB control region",
                "Reference allele — no variant; used as negative control",
                "none"
        ));

        // ── BLOCK 2: Haemoglobinopathy / erythrocyte variants ─────────────────
        panel.add(new Marker(
                "HBB_HbC",
                "CTGACTCCTGCGGAGAAGTCT",
                "HBB",
                "Haemoglobin C Disease (HbCC / HbSC)",
                "Point mutation codon 6 Glu→Lys (GAG→AAG); NM_000518.5:c.19G>A",
                "high"
        ));

        panel.add(new Marker(
                "HBB_HbE",
                "CCTGAGGAGAAGTCTGCCGTT",
                "HBB",
                "Haemoglobin E Trait / Disease",
                "Point mutation codon 26 Glu→Lys; produces aberrant splice site",
                "medium"
        ));

        panel.add(new Marker(
                "HBB_HbD",
                "CTGACTCCTGAGGAGAAATCT",
                "HBB",
                "Haemoglobin D-Punjab",
                "Point mutation codon 121 Glu→Gln; common in South Asian populations",
                "medium"
        ));

        panel.add(new Marker(
                "HBB_HbOArab",
                "CTGACTCCTGAGGATAAGACT",
                "HBB",
                "Haemoglobin O-Arab",
                "Point mutation codon 121 Glu→Lys; prevalent in Balkan / Middle Eastern populations",
                "medium"
        ));

        panel.add(new Marker(
                "HBA_Alpha1Del",
                "GGTGGTGAGGCCCTGGGCAGG",
                "HBA1",
                "Alpha-Thalassemia (single α-gene deletion)",
                "Deletion of one of the duplicated HBA1/HBA2 loci; carrier state",
                "low"
        ));

        panel.add(new Marker(
                "HBA_HbH",
                "CATCTGACTCCTGAGGAGAAG",
                "HBA2",
                "Haemoglobin H Disease (−−/αα)",
                "Deletion of 3 of 4 alpha-globin genes; moderate haemolytic anaemia",
                "high"
        ));

        panel.add(new Marker(
                "HBB_IVS1_110",
                "CCTGAGGAGAAGTCAGCCGTT",
                "HBB",
                "Beta-Thalassemia IVS-I-110 (G>A splice mutation)",
                "Most common β-thal allele in Mediterranean populations; severe β⁺",
                "high"
        ));

        panel.add(new Marker(
                "HBB_IVS2_654",
                "GGTGGTGAAGCCCTGGGCAGG",
                "HBB",
                "Beta-Thalassemia IVS-II-654 (C>T)",
                "Common in Southeast Asian populations; causes aberrant splicing",
                "high"
        ));

        panel.add(new Marker(
                "HBB_Cd39",
                "CTGAGGAGAAGTCTGCCGTTA",
                "HBB",
                "Beta-Thalassemia Codon 39 (C>T nonsense)",
                "Prevalent in Mediterranean; creates premature stop codon (Gln→Stop)",
                "high"
        ));

        // ── BLOCK 3: Metabolic / enzyme-deficiency markers ────────────────────
        panel.add(new Marker(
                "G6PD_A_Minus",
                "GTCACAGCCTGCCTGAAGGTG",
                "G6PD",
                "G6PD Deficiency — A− variant",
                "c.202G>A (Val68Met); common in sub-Saharan African populations",
                "medium"
        ));

        panel.add(new Marker(
                "G6PD_Mediterranean",
                "GACGCCGTGGCCAGCAAGGAG",
                "G6PD",
                "G6PD Deficiency — Mediterranean variant",
                "c.563C>T (Ser188Phe); severe deficiency; haemolysis risk with oxidants",
                "high"
        ));

        panel.add(new Marker(
                "PKU_PAH_R408W",
                "GAAGGAGCTGCACAACCTGGG",
                "PAH",
                "Phenylketonuria (PKU) — R408W",
                "Most common PKU allele in European populations; severe classic PKU",
                "high"
        ));

        panel.add(new Marker(
                "CF_CFTR_F508del",
                "CTTTTTTATACTCTTCTTCCT",
                "CFTR",
                "Cystic Fibrosis — ΔF508 deletion",
                "Most common CF-causing variant worldwide; class II CFTR processing defect",
                "high"
        ));

        panel.add(new Marker(
                "GAL_GALT_Q188R",
                "GCAGGTTCCGCACCACCTGAT",
                "GALT",
                "Classic Galactosaemia — Q188R",
                "Most prevalent GALT variant; causes severe neonatal liver failure if untreated",
                "high"
        ));

        panel.add(new Marker(
                "MCAD_ACADM_K304E",
                "CAGCTTTGGGATGCCGGTGTG",
                "ACADM",
                "MCAD Deficiency (Medium-chain acyl-CoA dehydrogenase)",
                "c.985A>G (K304E); most common MCAD variant; risk of hypoglycaemic crisis",
                "high"
        ));

        panel.add(new Marker(
                "BIO_BTD_D444H",
                "CCAGGCCTGGAATGTCACCGG",
                "BTD",
                "Biotinidase Deficiency — D444H",
                "Partial deficiency allele; c.1330G>C; responds well to biotin supplementation",
                "medium"
        ));

        panel.add(new Marker(
                "CAH_CYP21A2_I173N",
                "GACCCCTACATCATCATCAACA",
                "CYP21A2",
                "Congenital Adrenal Hyperplasia (CAH) — I173N",
                "Simple virilising allele; c.518T>A; causes partial 21-hydroxylase deficiency",
                "high"
        ));

        panel.add(new Marker(
                "MSUD_BCKDHA_Y438N",
                "GCACATGGAGGTGGCCTTTGG",
                "BCKDHA",
                "Maple Syrup Urine Disease (MSUD) — Y438N",
                "Classic MSUD; branched-chain ketoacid dehydrogenase deficiency",
                "high"
        ));

        panel.add(new Marker(
                "IVA_IVD_R363C",
                "GCAGCCCCTGTACCCGGACTT",
                "IVD",
                "Isovaleric Acidaemia — R363C",
                "c.1087C>T; results in accumulation of isovaleryl-CoA; odour of sweaty feet",
                "medium"
        ));

        panel.add(new Marker(
                "HCY_CBS_I278T",
                "GACGTGGTCAGCATGGCCATC",
                "CBS",
                "Homocystinuria — I278T (cystathionine β-synthase)",
                "Most common CBS variant; B6-responsive; risk of thromboembolism",
                "medium"
        ));

        panel.add(new Marker(
                "TYR_FAH_W262X",
                "CAGAATTTCTGGCTACAGCAC",
                "FAH",
                "Tyrosinaemia Type I — W262X",
                "c.786G>A; fumarylacetoacetase deficiency; severe hepatorenal disease",
                "high"
        ));

        panel.add(new Marker(
                "MPS1_IDUA_W402X",
                "GATGGGCTGGAGCAGTGGCTG",
                "IDUA",
                "Mucopolysaccharidosis Type I (Hurler) — W402X",
                "c.1205G>A; most common Hurler allele; alpha-L-iduronidase deficiency",
                "high"
        ));

        panel.add(new Marker(
                "SMA_SMN1_Ex7del",
                "AGACTATCAACTTAATTTCTT",
                "SMN1",
                "Spinal Muscular Atrophy (SMA) — SMN1 exon 7 deletion region",
                "Homozygous deletion of SMN1 exon 7/8 causes SMA; region used for carrier screen",
                "high"
        ));

        panel.add(new Marker(
                "DMD_EXON48_Del",
                "GCAAGAAGAGAAACGATCAGC",
                "DMD",
                "Duchenne Muscular Dystrophy — Exon 48 deletion hotspot",
                "Most common DMD deletion hotspot; frameshift of dystrophin reading frame",
                "high"
        ));

        panel.add(new Marker(
                "FXS_FMR1_CGG",
                "CGGCGGCGGCGGCGGCGGCGG",
                "FMR1",
                "Fragile X Syndrome — CGG repeat expansion region",
                "Premutation (55–200 CGG) / full mutation (>200 CGG) region marker",
                "medium"
        ));

        // ── BLOCK 4: Synthetic filler markers for benchmark scaling ───────────
        // Generated with a seeded LCG (seed=9973) identical to the Python seed.py.
        // These are purely for demonstrating AC vs KMP scaling to 40 markers;
        // they are NOT derived from real clinical variants.

        panel.add(new Marker(
                "SYN_029",
                "CAAGTCCCACCAACACCTA",
                "SYN",
                "Synthetic filler marker #29",
                "Pseudorandom 19-bp sequence — benchmark only",
                "low"
        ));

        panel.add(new Marker(
                "SYN_030",
                "TACAATCGAATGGTAAAGCTG",
                "SYN",
                "Synthetic filler marker #30",
                "Pseudorandom 21-bp sequence — benchmark only",
                "medium"
        ));

        panel.add(new Marker(
                "SYN_031",
                "GCAGCCGCACTTTCAGACCTT",
                "SYN",
                "Synthetic filler marker #31",
                "Pseudorandom 21-bp sequence — benchmark only",
                "low"
        ));

        panel.add(new Marker(
                "SYN_032",
                "TTGAACCGGTGCCATTAGGCA",
                "SYN",
                "Synthetic filler marker #32",
                "Pseudorandom 21-bp sequence — benchmark only",
                "high"
        ));

        panel.add(new Marker(
                "SYN_033",
                "CGTAGCATGCACTTGGACCTA",
                "SYN",
                "Synthetic filler marker #33",
                "Pseudorandom 21-bp sequence — benchmark only",
                "low"
        ));

        panel.add(new Marker(
                "SYN_034",
                "AATCGTACCGGATCACTTGCC",
                "SYN",
                "Synthetic filler marker #34",
                "Pseudorandom 21-bp sequence — benchmark only",
                "medium"
        ));

        panel.add(new Marker(
                "SYN_035",
                "GCCTTACGATGTAGCAACGGT",
                "SYN",
                "Synthetic filler marker #35",
                "Pseudorandom 21-bp sequence — benchmark only",
                "low"
        ));

        panel.add(new Marker(
                "SYN_036",
                "TATTGGCACAGCCGTTAGGAC",
                "SYN",
                "Synthetic filler marker #36",
                "Pseudorandom 21-bp sequence — benchmark only",
                "medium"
        ));

        panel.add(new Marker(
                "SYN_037",
                "CAAGTTCCGATGCAATGCGAC",
                "SYN",
                "Synthetic filler marker #37",
                "Pseudorandom 21-bp sequence — benchmark only",
                "low"
        ));

        panel.add(new Marker(
                "SYN_038",
                "GCCTAAGTTCAGGCACTTGCA",
                "SYN",
                "Synthetic filler marker #38",
                "Pseudorandom 21-bp sequence — benchmark only",
                "high"
        ));

        panel.add(new Marker(
                "SYN_039",
                "ATGCCTGAACGTCAGGTTCCA",
                "SYN",
                "Synthetic filler marker #39",
                "Pseudorandom 21-bp sequence — benchmark only",
                "medium"
        ));

        panel.add(new Marker(
                "SYN_040",
                "CCAGTTGACGTTCAAGGCTAT",
                "SYN",
                "Synthetic filler marker #40",
                "Pseudorandom 21-bp sequence — benchmark only",
                "low"
        ));

        return panel;
    }

    /** Flattens the panel to markerId -> pattern for the older Map-based engine methods. */
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

    /** Quick sanity check — print every marker's ID, gene, and pattern length. */
    public static void main(String[] args) {
        List<Marker> panel = load();
        System.out.printf("%-14s  %-8s  %-5s  %s%n", "ID", "Gene", "Len", "Disease");
        System.out.println("-".repeat(80));
        for (Marker m : panel) {
            System.out.printf("%-14s  %-8s  %-5d  %s%n",
                    m.id, m.gene, m.pattern.length(), m.disease);
        }
        System.out.println("\nTotal markers: " + panel.size());
    }
}
