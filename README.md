# DNA Marker Screening & Pattern-Matching System

## Overview

This project is a Java-based DNA marker screening prototype that detects predefined DNA sequence markers in a DNA sample.

The system uses the **Aho-Corasick multiple-pattern string matching algorithm** as its primary screening algorithm and **Knuth-Morris-Pratt (KMP)** as a baseline for comparison.

The project demonstrates how DNA sequences can be represented as strings and searched efficiently against a panel of predefined markers.

> **Note:** This is an educational prototype using synthetic DNA data. It is not a clinical diagnostic system and must not be used for medical diagnosis.

---

## Objectives

The project demonstrates how to:

- Represent DNA markers as nucleotide sequences.
- Store marker metadata such as gene, disease, mutation type, and severity.
- Search a DNA sequence for multiple markers simultaneously.
- Identify the positions at which markers occur.
- Compare Aho-Corasick and KMP.
- Generate reproducible synthetic DNA samples.
- Read and work with FASTA-formatted DNA sequences.

---

## Technologies

- Java
- Aho-Corasick algorithm
- Knuth-Morris-Pratt (KMP) algorithm
- FASTA sequence format
- Java Collections Framework

No external Java libraries are required.

---

## Project Structure

```text
DNA/
│
├── AhoCorasick.java
├── BenchmarkDemo.java
├── FastaSampleGenerator.java
├── KMPMatcher.java
├── Marker.java
├── MarkerPanel.java
├── Match.java
├── ScreeningDemo.java
│
└── .vscode/
```

---

## Main Components

### `Marker.java`

Represents a DNA marker and its associated metadata.

Typical information includes:

- Marker ID
- DNA pattern
- Gene
- Disease/condition
- Mutation type
- Severity

---

### `MarkerPanel.java`

Contains the predefined marker panel used by the screening system.

The current panel contains HBB-related teaching markers, including:

- `HBB_HbS`
- `HBB_BETA0`
- `HBB_REF`

The marker panel acts as the source of marker sequences and their metadata.

---

### `Match.java`

Represents a detected marker.

A match contains information such as:

```text
marker ID
position
matched sequence
```

The position represents the starting position of the match in the DNA sequence.

---

### `AhoCorasick.java`

Implements the Aho-Corasick multiple-pattern matching algorithm.

The algorithm:

1. Builds a trie containing all marker patterns.
2. Creates failure links between trie nodes.
3. Stores output markers at appropriate nodes.
4. Scans the DNA sequence.
5. Reports all matching markers.

Conceptually:

```text
DNA Markers
     |
     v
   Trie
     |
     v
Failure Links
     |
     v
Aho-Corasick Automaton
     |
     v
DNA Sequence
     |
     v
Detected Markers
```

### Complexity

If `P` is the total length of all marker patterns and `n` is the DNA sequence length:

```text
Automaton construction: O(P)
Search:                 O(n + number of matches)
```

This makes Aho-Corasick suitable for searching one DNA sequence against many markers.

---

### `KMPMatcher.java`

Implements the Knuth-Morris-Pratt algorithm.

KMP efficiently searches for one pattern at a time by constructing an **LPS (Longest Proper Prefix which is also Suffix)** table.

For a marker of length `m` and DNA sequence of length `n`:

```text
Preprocessing: O(m)
Search:        O(n)
```

When many markers are used, KMP has to search the DNA sequence separately for each marker.

---

### `FastaSampleGenerator.java`

Generates synthetic DNA sequences for testing.

The generated samples include:

```text
HBB_reference
sample_sickle_cell
sample_beta_thalassemia
sample_combined
```

The generator uses reproducible random seeds so that the test data can be regenerated consistently.

The generated sequences can also be written in FASTA format.

---

### `ScreeningDemo.java`

Runs the complete screening demonstration.

The program:

1. Loads the marker panel.
2. Builds the Aho-Corasick automaton.
3. Generates synthetic samples.
4. Searches the samples using Aho-Corasick.
5. Searches the same samples using KMP.
6. Compares the detected matches.
7. Prints the screening results.

The comparison verifies whether both algorithms produce the same set of detected markers.

---

### `BenchmarkDemo.java`

Benchmarks Aho-Corasick and KMP using increasing marker-panel sizes.

The benchmark tests panel sizes such as:

```text
5
10
20
40
80
```

and measures the execution time of each algorithm.

Example output format:

```text
PanelSize    AhoCorasick(ms)    KMP(ms)
5            ...
10           ...
20           ...
40           ...
80           ...
```

Exact timings depend on the computer, Java version, and system load.

---

# DNA Representation

DNA is represented using the four nucleotide bases:

```text
A = Adenine
C = Cytosine
G = Guanine
T = Thymine
```

Example:

```text
ACACCATGGTGCATCTGACTCCTGTGGAGAAGTCT
```

The DNA sequence is treated as a string for pattern matching.

If a marker pattern occurs inside the DNA sequence, the matching algorithm records its location.

---

# FASTA Format

The project can work with FASTA-formatted DNA sequences.

A FASTA file has the following structure:

```text
>sample_sickle_cell
ACGTACGTACGTACGT...
```

The line beginning with `>` is the sequence identifier.

The remaining lines contain the nucleotide sequence.

---

# Screening Workflow

The complete screening process is:

```text
             Marker Panel
                  |
                  v
          Marker Sequences
                  |
                  v
        Build Aho-Corasick
             Automaton
                  |
                  v
             DNA Sample
                  |
                  v
            Scan Sequence
                  |
                  v
          Detect Matches
                  |
                  v
        Retrieve Marker Data
                  |
                  v
         Generate Report
```

For validation, the same DNA sample can also be processed using KMP.

---

# Why Aho-Corasick?

Aho-Corasick is useful when a single DNA sequence must be checked against many marker patterns.

With KMP, the workflow is approximately:

```text
DNA
 |
 +--> Marker 1
 |
 +--> Marker 2
 |
 +--> Marker 3
 |
 +--> Marker 4
 |
 +--> ...
```

The sequence is repeatedly searched.

Aho-Corasick combines all marker patterns into one automaton:

```text
Marker 1 --Marker 2 ---Marker 3 ----> Aho-Corasick ---> DNA Scan
Marker 4 ---/
Marker 5 --/
```

Therefore, the project uses Aho-Corasick as the primary multi-marker screening approach.

---

# Running the Project

## Requirements

Install Java JDK 8 or later.

Check the installation:

```bash
java -version
```

and:

```bash
javac -version
```

---

## Compile

Open a terminal in the project directory:

```bash
cd DNA
```

Compile all Java files:

```bash
javac *.java
```

---

## Run Screening Demo

```bash
java ScreeningDemo
```

This runs the synthetic DNA marker screening demonstration.

---

## Run Benchmark

```bash
java BenchmarkDemo
```

This compares the performance of Aho-Corasick and KMP with different marker-panel sizes.

---

# Example Marker Detection

Suppose a DNA sequence contains:

```text
...ACACCATGGTGCATCTGACTCCTGTGGAGAAGTCT...
```

and the marker panel contains:

```text
CTGACTCCTGTGGAGAAGTCT
```

The matcher identifies the marker and records:

```text
Marker ID
Matched sequence
Position
```

The system can then retrieve the marker's associated metadata from `MarkerPanel`.

---

# Testing

## Functional Testing

The screening demo compares the results produced by Aho-Corasick and KMP.

The expected validation condition is:

```text
Aho-Corasick results == KMP results
```

If both algorithms identify the same matches, the implementations agree for that test sample.

## Performance Testing

The benchmark increases the number of markers and measures execution time.

This demonstrates how the two approaches behave as the marker panel grows.

---

# Synthetic Data

The project currently uses synthetic DNA sequences rather than patient sequencing data.

This is useful for:

- Algorithm testing
- Demonstrations
- Reproducible experiments
- DSA coursework
- Performance benchmarking

The synthetic data should not be interpreted as real patient genetic information.

---

# Limitations

The current implementation has several limitations.

### Exact Matching

The system currently searches for exact nucleotide sequences.

It does not perform:

- Approximate matching
- Sequence alignment
- Indel detection
- SNP-tolerant matching
- Sequencing-error correction

### Synthetic Data

The current demonstration uses generated DNA sequences.

It is therefore not equivalent to processing real clinical sequencing data.

### Limited Marker Panel

Only a small set of HBB-related teaching markers is included.

### No Clinical Interpretation

Finding a matching sequence does not automatically establish a medical diagnosis, carrier status, disease severity, or prognosis.

### No Sequencing Quality

Real sequencing data can contain errors, ambiguous bases, and varying coverage. The current implementation assumes a clean DNA string.

---

# Future Improvements

Possible extensions include:

## 1. Real FASTA Upload

Allow users to provide their own FASTA files:

```text
FASTA File
    |
    v
Sequence Parser
    |
    v
DNA Sequence
    |
    v
Aho-Corasick
    |
    v
Detected Markers
```

## 2. Larger Marker Database

Move marker metadata from hardcoded Java objects into:

- CSV
- JSON
- SQL database

## 3. Approximate Matching

Add algorithms for detecting sequences that are similar rather than exactly identical.

Potential approaches include:

- Hamming distance
- Edit distance
- Sequence alignment
- k-mer methods

## 4. FASTQ Support

Add support for FASTQ files so sequencing quality scores can also be considered.

## 5. Web Interface

A future frontend could provide:

```text
Upload DNA
     |
     v
Select Marker Panel
     |
     v
Run Screening
     |
     v
View Matches
     |
     v
Generate Report
```

## 6. Database Integration

Marker information could be stored and retrieved dynamically from a database.

## 7. Security

A real system handling patient data would require appropriate:

- Authentication
- Authorization
- Encryption
- Audit logging
- Secure storage
- Privacy controls
- Data retention policies

---

# Algorithm Comparison

| Feature | Aho-Corasick | KMP |
|---|---|---|
| Main purpose | Multiple-pattern matching | Single-pattern matching |
| Patterns searched simultaneously | Yes | No |
| Preprocessing | Trie + failure links | LPS table |
| Search per DNA sample | Shared scan | One scan per marker |
| Overlapping matches | Supported | Supported |
| Project role | Primary algorithm | Baseline |
| Search complexity | O(n + matches) | O(n + m) per pattern |

Where:

- `n` = DNA sequence length
- `m` = marker length
- `matches` = number of reported matches

---

# Educational Significance

This project demonstrates the application of **Data Structures and Algorithms to Bioinformatics**.

The central concept is:

```text
DNA Sequence
     +
DNA Marker Panel
     +
String Matching
     |
     v
Computational DNA Screening
```

A biological sequence-screening problem is represented as a computational multiple-pattern matching problem.

The project therefore connects:

- Trie data structures
- Failure functions
- Pattern matching
- String algorithms
- FASTA parsing
- Computational biology

---

# Disclaimer

This project is intended for **educational and prototype purposes only**.

The DNA markers, synthetic sequences, classifications, and screening results in this project are not intended to provide medical advice or clinical diagnosis.

A real clinical genetic-testing system would require validated biological data, laboratory validation, appropriate variant interpretation, quality controls, regulatory compliance, and qualified professionals.

---

## Author

**Project:** DNA Marker Screening & Pattern-Matching System  
**Language:** Java  
**Primary Algorithm:** Aho-Corasick  
**Baseline Algorithm:** Knuth-Morris-Pratt (KMP)  
**Domain:** Bioinformatics / Computational Biology / Data Structures & Algorithms
