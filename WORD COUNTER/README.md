# Task 2: Word Counter & Text Corpus Analyzer Utility

A clean, modular, beginner-friendly console-based Java application that analyzes text files, calculates key corpus statistics (total words, unique words, and average sentence length), counts word frequencies using a `HashMap<String, Integer>`, and displays the top 10 most frequent words in a formatted console table with alphabetical tie-breaking.

---

## 1. Project Overview

- **Class Name**: `WordAnalyzer.java`
- **Language Level**: Java 17 or later (Standard Java SE runtime and compiler)
- **Architecture**: Modular console application using Java I/O (`BufferedReader`, `FileReader`, `File`), Java Collections Framework (`HashMap`, `ArrayList`, `Collections.sort`, `Comparator`), and Regular Expressions (`java.util.regex`).
- **Dependencies**: None (pure core Java without external libraries or frameworks).

---

## 2. File Structure

```text
d:/JAVA PROJECTS/WORD COUNTER/
├── WordAnalyzer.java      # Main Java source code containing all analysis logic
├── sample.txt             # Primary sample text corpus for evaluation
├── empty.txt              # Test file: empty file (0 bytes)
├── few_words.txt          # Test file: small dataset (< 10 words) with frequency ties
├── mixed_test.txt         # Test file: mixed case, brackets, quotes, and punctuation
├── no_punctuation.txt     # Test file: text without sentence-ending punctuation
└── README.md              # Project documentation and guide
```

---

## 3. Features & Specifications

1. **File Input & Validation**:
   - Supports both interactive file path entry (with default `sample.txt`) and direct command-line arguments (`java WordAnalyzer <file>`).
   - Gracefully handles missing files, directory paths, unreadable files, and empty files without crashing.

2. **Text Cleaning & Tokenization**:
   - Converts all words to lowercase for case-insensitive counting.
   - Uses regex `[^a-zA-Z0-9]+` to remove punctuation, symbols, and whitespace without incorrectly merging adjacent words.
   - Filters out any empty tokens.

3. **Word Frequency & Statistics**:
   - Counts **total words** (all valid tokens).
   - Counts **unique words** (distinct word tokens).
   - Uses `HashMap<String, Integer>` where keys are words and values are occurrence counts.

4. **Sentence Detection & Average Sentence Length**:
   - Identifies sentences using common sentence-ending punctuation marks (`.`, `!`, and `?`).
   - Groups consecutive marks (such as `...` or `?!`) so they are not counted as multiple sentence boundaries.
   - Calculates average sentence length as:
     $$\text{Average Sentence Length} = \frac{\text{Total Valid Words}}{\text{Detected Sentences}}$$
   - Prevents division by zero when text lacks sentence-ending punctuation.

5. **Top 10 Words with Tie-Breaking**:
   - Converts the `HashMap` entry set into an `ArrayList<Map.Entry<String, Integer>>`.
   - Sorts in descending order of frequency.
   - If frequencies are equal, breaks ties in ascending alphabetical order (A to Z).
   - Displays up to 10 entries (or all available if fewer than 10 unique words exist).

6. **Formatted Console Table**:
   - Formats statistics and ranked word frequency tables cleanly using `System.out.printf()`.

---

## 4. How to Compile and Run

### Prerequisites
- Java Development Kit (JDK) 17 or later installed.
- Terminal or command prompt opened in the project folder `d:\JAVA PROJECTS\WORD COUNTER`.

### Step 1: Compile the Java Program
```bash
javac WordAnalyzer.java
```

### Step 2: Run the Program

#### Option A: Interactive Mode
```bash
java WordAnalyzer
```
- Press `[ENTER]` to analyze the default `sample.txt`.
- Type any custom file path (e.g., `few_words.txt`).
- Type `q` or `quit` to exit.

#### Option B: Direct Command-Line Argument Mode
```bash
java WordAnalyzer sample.txt
java WordAnalyzer few_words.txt
java WordAnalyzer empty.txt
```

---

## 5. Sample Execution Output

Running `java WordAnalyzer sample.txt`:

```text
Processing file from command-line argument: sample.txt

===============================================================
              WORD COUNTER & CORPUS ANALYZER                   
===============================================================
File Path              : sample.txt
File Size              : 586 bytes
---------------------------------------------------------------
Total Words            : 89
Unique Words           : 66
Detected Sentences     : 10
Average Sentence Length: 8.90 words/sentence
---------------------------------------------------------------
                     TOP FREQUENT WORDS                        
---------------------------------------------------------------
+------+--------------------------------+------------+
| Rank | Word                           |  Frequency |
+------+--------------------------------+------------+
|    1 | java                           |         10 |
|    2 | and                            |          4 |
|    3 | is                             |          4 |
|    4 | code                           |          3 |
|    5 | to                             |          3 |
|    6 | as                             |          2 |
|    7 | love                           |          2 |
|    8 | programmers                    |          2 |
|    9 | support                        |          2 |
|   10 | a                              |          1 |
+------+--------------------------------+------------+
===============================================================
```

*(Note that words with identical counts like "and" and "is" (both 4), or "as", "love", "programmers", and "support" (all 2) are ordered alphabetically as a tie-breaker).*

---

## 6. Verification and Test Results

The following test scenarios were executed and verified:

| Test Case | Input File | Expected Behavior | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Normal Input** | `sample.txt` | Cleanly counts words, sentences, average length, top 10 table | Total: 89 words, 66 unique, 10 sentences, avg: 8.90 | **Passed** |
| **Empty File** | `empty.txt` (0 bytes) | Warning displayed, 0 counts, no crashes | Total: 0 words, 0 sentences, avg: 0.00, graceful handling | **Passed** |
| **Missing File** | `non_existent_file.txt` | Friendly error message without crash | `[ERROR] File not found: non_existent_file.txt` | **Passed** |
| **Few Words (< 10) & Ties** | `few_words.txt` | All 4 entries displayed, alphabetical tie-breaking | 1. apple (2), 2. banana (2), 3. cherry (1), 4. date (1) | **Passed** |
| **No Punctuation** | `no_punctuation.txt` | 0 sentences detected, zero-division avoided | Total: 10 words, 0 sentences, avg: 0.00 | **Passed** |
| **Mixed Case & Punctuation** | `mixed_test.txt` | Case normalized to lowercase, punctuation removed | Case merged (e.g. JAVA, java, JaVa -> java: 5) | **Passed** |
| **Interactive Session** | User input | Interactive loop with default option and quit command | Successfully looped and exited cleanly | **Passed** |
