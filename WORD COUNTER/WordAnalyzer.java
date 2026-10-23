import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * WordAnalyzer
 * 
 * A console-based Java application that reads a text corpus file, cleans the text,
 * calculates corpus statistics (total words, unique words, average sentence length),
 * builds word frequencies using a HashMap, and displays the top 10 frequent words
 * in a formatted console table with alphabetical tie-breaking.
 * 
 * Internship Task 2: Word Counter & Text Corpus Analyzer Utility
 */
public class WordAnalyzer {

    // Default sample file path to analyze if the user presses Enter without typing a path
    private static final String DEFAULT_FILE_PATH = "sample.txt";

    /**
     * Program entry point.
     * Supports both command-line arguments (e.g., java WordAnalyzer myfile.txt)
     * and an interactive console loop.
     * 
     * @param args optional command-line arguments; args[0] can be a file path.
     */
    public static void main(String[] args) {
        // If a file path was passed directly as a command-line argument, analyze it and exit
        if (args.length > 0) {
            String filePath = args[0].trim();
            System.out.println("Processing file from command-line argument: " + filePath);
            analyzeFile(new File(filePath));
            return;
        }

        // Otherwise, run interactive console session
        Scanner scanner = new Scanner(System.in);
        System.out.println("===============================================================");
        System.out.println("   WELCOME TO WORD COUNTER & TEXT CORPUS ANALYZER UTILITY     ");
        System.out.println("===============================================================");
        System.out.println("This utility calculates word counts, sentence lengths, and");
        System.out.println("analyzes word frequencies from any plain text file.");
        System.out.println();

        boolean keepRunning = true;
        while (keepRunning) {
            System.out.printf("Enter text file path [Press ENTER for default '%s', or 'q' to quit]: ", DEFAULT_FILE_PATH);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using Word Analyzer. Goodbye!");
                keepRunning = false;
                break;
            }

            String targetPath = input.isEmpty() ? DEFAULT_FILE_PATH : input;
            File targetFile = new File(targetPath);

            // Perform analysis
            analyzeFile(targetFile);

            // Prompt user whether to analyze another file
            System.out.print("Would you like to analyze another file? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (!choice.equals("y") && !choice.equals("yes")) {
                System.out.println("Thank you for using Word Analyzer. Goodbye!");
                keepRunning = false;
            }
            System.out.println();
        }

        scanner.close();
    }

    /**
     * Orchestrates the complete file reading, analysis, and display pipeline.
     * Handles missing files, unreadable files, and I/O errors gracefully.
     * 
     * @param file the File object to analyze
     */
    public static void analyzeFile(File file) {
        // Step 1: Validate file existence and permissions
        if (!file.exists()) {
            System.out.println("\n[ERROR] File not found: " + file.getPath());
            System.out.println("Please check that the file path is spelled correctly and exists.\n");
            return;
        }

        if (file.isDirectory()) {
            System.out.println("\n[ERROR] The specified path is a directory, not a text file: " + file.getPath());
            System.out.println("Please provide a path to a valid text file.\n");
            return;
        }

        if (!file.canRead()) {
            System.out.println("\n[ERROR] Cannot read file (permission denied): " + file.getPath() + "\n");
            return;
        }

        // Step 2: Read file content using Java IO
        String rawText;
        try {
            rawText = readFile(file);
        } catch (FileNotFoundException e) {
            System.out.println("\n[ERROR] File was not found during reading: " + e.getMessage() + "\n");
            return;
        } catch (IOException e) {
            System.out.println("\n[ERROR] An I/O error occurred while reading the file: " + e.getMessage() + "\n");
            return;
        }

        // Step 3: Handle empty or whitespace-only files gracefully
        long fileSizeBytes = file.length();
        if (rawText.trim().isEmpty()) {
            System.out.println("\n[WARNING] The file '" + file.getName() + "' is empty (contains no text).");
            displayResults(file.getPath(), fileSizeBytes, 0, 0, 0, 0.0, Collections.emptyList());
            return;
        }

        // Step 4: Clean and tokenize text into words
        List<String> words = tokenizeWords(rawText);

        // Step 5: Count total words and unique words via HashMap
        int totalWords = words.size();
        Map<String, Integer> frequencyMap = countWordFrequencies(words);
        int uniqueWords = frequencyMap.size();

        // Step 6: Identify sentences and calculate average sentence length
        int sentenceCount = countSentences(rawText);
        double avgSentenceLength = calculateAverageSentenceLength(totalWords, sentenceCount);

        // Step 7: Sort word frequencies (descending by count, tie-breaker alphabetical)
        List<Map.Entry<String, Integer>> sortedEntries = getSortedFrequencies(frequencyMap);

        // Step 8: Display the formatted results
        displayResults(file.getPath(), fileSizeBytes, totalWords, uniqueWords, sentenceCount, avgSentenceLength, sortedEntries);
    }

    /**
     * Reads the entire contents of a text file using BufferedReader and FileReader.
     * 
     * @param file the File to be read
     * @return the file content as a single String
     * @throws IOException if an I/O error occurs
     */
    public static String readFile(File file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        // Use try-with-resources to ensure BufferedReader is automatically closed
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                // Append each line followed by a space to prevent joining words across line breaks
                contentBuilder.append(currentLine).append(" ");
            }
        }

        return contentBuilder.toString();
    }

    /**
     * Cleans text and splits it into lowercase word tokens using regular expressions.
     * Uses \\W+ to identify non-word delimiter boundaries (spaces, punctuation, symbols).
     * Filters out any empty tokens.
     * 
     * @param text the raw text to tokenize
     * @return list of cleaned, lowercase word tokens
     */
    public static List<String> tokenizeWords(String text) {
        List<String> wordList = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return wordList;
        }

        // Split text by one or more non-alphanumeric characters.
        // [^a-zA-Z0-9]+ matches any sequence of punctuation, spaces, or symbols.
        // This removes punctuation and splits words cleanly without merging adjacent words.
        String[] tokens = text.toLowerCase().split("[^a-zA-Z0-9]+");

        for (String token : tokens) {
            // Trim and ignore empty tokens that occur at text boundaries
            String cleanedToken = token.trim();
            if (!cleanedToken.isEmpty()) {
                wordList.add(cleanedToken);
            }
        }

        return wordList;
    }

    /**
     * Stores word occurrences and calculates their frequencies using a HashMap<String, Integer>.
     * 
     * @param words list of word tokens
     * @return a Map where keys are unique words and values are their occurrence counts
     */
    public static Map<String, Integer> countWordFrequencies(List<String> words) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String word : words) {
            // If the word already exists in the map, increment its count
            if (frequencyMap.containsKey(word)) {
                int currentCount = frequencyMap.get(word);
                frequencyMap.put(word, currentCount + 1);
            } else {
                // If it is the first time seeing this word, insert with count 1
                frequencyMap.put(word, 1);
            }
        }

        return frequencyMap;
    }

    /**
     * Counts the number of sentences detected in the text based on common sentence-ending
     * punctuation marks: period (.), exclamation mark (!), and question mark (?).
     * 
     * Limitations:
     * - Abbreviations (such as "e.g.", "Dr.", "U.S.A.") and decimal numbers (such as "3.14")
     *   contain periods that may be detected as sentence boundaries.
     * - Unpunctuated text or headings without trailing punctuation will not be detected.
     * 
     * @param text raw text input
     * @return the number of detected sentences (0 if none found)
     */
    public static int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        // Check if there are any sentence-ending punctuation marks in the text
        boolean hasSentencePunctuation = text.matches(".*[.!?].*");
        if (!hasSentencePunctuation) {
            return 0;
        }

        // Split text by sequences of one or more sentence-ending marks (. ! ?)
        // Grouping [.!?]+ ensures "..." or "?!" or "!!" is counted as a single sentence break.
        String[] sentenceChunks = text.split("[.!?]+");
        int count = 0;

        for (String chunk : sentenceChunks) {
            // Check if the chunk contains at least one word character (letter or digit)
            if (chunk.matches(".*[a-zA-Z0-9].*")) {
                count++;
            }
        }

        return count;
    }

    /**
     * Calculates the average sentence length in words per sentence.
     * Safely handles cases where sentenceCount is zero to avoid division by zero.
     * 
     * @param totalWords total number of valid word tokens
     * @param sentenceCount total number of detected sentences
     * @return average sentence length, or 0.0 if sentenceCount is 0
     */
    public static double calculateAverageSentenceLength(int totalWords, int sentenceCount) {
        if (sentenceCount <= 0) {
            return 0.0;
        }
        return (double) totalWords / sentenceCount;
    }

    /**
     * Sorts the entries of the word frequency map:
     * 1. Primary order: Frequency in descending order (highest count first).
     * 2. Tie-breaker: Alphabetical order (A to Z) when frequencies are equal.
     * 
     * @param frequencyMap the map of words and their counts
     * @return sorted list of Map.Entry objects
     */
    public static List<Map.Entry<String, Integer>> getSortedFrequencies(Map<String, Integer> frequencyMap) {
        // Convert the map's entrySet into an ArrayList for sorting
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());

        // Sort using Collections.sort and a custom Comparator
        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                // Step 1: Compare frequencies descending (entry2 vs entry1)
                int freqComparison = entry2.getValue().compareTo(entry1.getValue());
                if (freqComparison != 0) {
                    return freqComparison;
                }

                // Step 2: Tie-breaker - compare words alphabetically ascending (entry1 vs entry2)
                return entry1.getKey().compareTo(entry2.getKey());
            }
        });

        return entryList;
    }

    /**
     * Formats and displays the analysis summary and top 10 frequent words in a clean console table.
     * 
     * @param filePath path of the analyzed file
     * @param fileSizeBytes size of the file in bytes
     * @param totalWords total word count
     * @param uniqueWords distinct word count
     * @param sentenceCount detected sentence count
     * @param avgSentenceLength average words per sentence
     * @param sortedEntries sorted word frequency entries
     */
    public static void displayResults(String filePath, long fileSizeBytes, int totalWords,
                                      int uniqueWords, int sentenceCount, double avgSentenceLength,
                                      List<Map.Entry<String, Integer>> sortedEntries) {
        System.out.println();
        System.out.println("===============================================================");
        System.out.println("              WORD COUNTER & CORPUS ANALYZER                   ");
        System.out.println("===============================================================");
        System.out.printf("File Path              : %s%n", filePath);
        System.out.printf("File Size              : %,d bytes%n", fileSizeBytes);
        System.out.println("---------------------------------------------------------------");
        System.out.printf("Total Words            : %,d%n", totalWords);
        System.out.printf("Unique Words           : %,d%n", uniqueWords);
        System.out.printf("Detected Sentences     : %,d%n", sentenceCount);

        if (sentenceCount > 0) {
            System.out.printf("Average Sentence Length: %.2f words/sentence%n", avgSentenceLength);
        } else {
            System.out.println("Average Sentence Length: 0.00 words/sentence (No sentence endings detected)");
        }

        System.out.println("---------------------------------------------------------------");
        System.out.println("                     TOP FREQUENT WORDS                        ");
        System.out.println("---------------------------------------------------------------");

        if (sortedEntries.isEmpty()) {
            System.out.println("No words found in the provided text file.");
        } else {
            System.out.printf("+------+--------------------------------+------------+%n");
            System.out.printf("| %-4s | %-30s | %10s |%n", "Rank", "Word", "Frequency");
            System.out.printf("+------+--------------------------------+------------+%n");

            // Display up to 10 entries (or fewer if less than 10 unique words exist)
            int displayLimit = Math.min(10, sortedEntries.size());
            for (int i = 0; i < displayLimit; i++) {
                Map.Entry<String, Integer> entry = sortedEntries.get(i);
                System.out.printf("| %4d | %-30s | %10d |%n", (i + 1), entry.getKey(), entry.getValue());
            }

            System.out.printf("+------+--------------------------------+------------+%n");

            if (sortedEntries.size() < 10) {
                System.out.printf("Note: File contains %d unique word(s) (displaying all available).%n", sortedEntries.size());
            }
        }

        System.out.println("===============================================================");
        System.out.println();
    }
}
