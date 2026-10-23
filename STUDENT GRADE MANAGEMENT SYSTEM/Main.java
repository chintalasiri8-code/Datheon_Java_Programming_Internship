import java.util.List;
import java.util.Scanner;

/**
 * Main application class and console user interface entry point
 * for the Student Grade Management System.
 *
 * It manages user interaction, displays the interactive text menu,
 * reads and validates user inputs, and delegates all business operations
 * to the GradeManager instance.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GradeManager manager = new GradeManager();
        boolean running = true;

        printWelcomeBanner();

        while (running) {
            printMenu();
            int choice = readMenuChoice(scanner, 1, 8);

            switch (choice) {
                case 1:
                    handleAddStudent(scanner, manager);
                    break;
                case 2:
                    handleRecordOrUpdateMarks(scanner, manager);
                    break;
                case 3:
                    handleSearchStudent(scanner, manager);
                    break;
                case 4:
                    handleDisplayReportCard(scanner, manager);
                    break;
                case 5:
                    handleDisplayAllStudents(manager);
                    break;
                case 6:
                    handleCalculateClassAverage(manager);
                    break;
                case 7:
                    handleCalculateMedianScore(manager);
                    break;
                case 8:
                    running = false;
                    printExitMessage();
                    break;
                default:
                    // Guard in case readMenuChoice bounds change
                    System.out.println("[!] Invalid option. Please select a valid menu choice (1-8).\n");
                    break;
            }
        }

        scanner.close();
    }

    // ==========================================
    // Visual Banners & Menu
    // ==========================================
    private static void printWelcomeBanner() {
        System.out.println("============================================================");
        System.out.println("       WELCOME TO STUDENT GRADE MANAGEMENT SYSTEM           ");
        System.out.println("               Java OOP Internship Project                  ");
        System.out.println("============================================================");
        System.out.println("A modular, robust, and beginner-friendly academic manager.\n");
    }

    private static void printMenu() {
        System.out.println("------------------------------------------------------------");
        System.out.println("                    MAIN MENU OPTIONS                       ");
        System.out.println("------------------------------------------------------------");
        System.out.println("  1. Add Student");
        System.out.println("  2. Record or Update Marks");
        System.out.println("  3. Search Student");
        System.out.println("  4. Display Student Report Card");
        System.out.println("  5. Display All Students");
        System.out.println("  6. Calculate Class Average");
        System.out.println("  7. Calculate Median Student Score");
        System.out.println("  8. Exit");
        System.out.println("------------------------------------------------------------");
    }

    private static void printExitMessage() {
        System.out.println("\n============================================================");
        System.out.println("  Thank you for using Student Grade Management System!      ");
        System.out.println("  Have a great day and happy learning!                      ");
        System.out.println("============================================================\n");
    }

    // ==========================================
    // Menu Action Handlers
    // ==========================================

    /**
     * Option 1: Registers a new student after reading ID and Name.
     */
    private static void handleAddStudent(Scanner scanner, GradeManager manager) {
        System.out.println("\n--- [1] Add New Student ---");
        String id = readNonEmptyString(scanner, "Enter Student ID (e.g., S101): ");

        if (manager.isDuplicateId(id)) {
            System.out.println("[Error] A student with ID '" + id + "' already exists! Student IDs must be unique.\n");
            return;
        }

        String name = readNonEmptyString(scanner, "Enter Student Full Name: ");

        try {
            manager.registerStudent(id, name);
            System.out.println("[Success] Student '" + name + "' (ID: " + id + ") registered successfully!\n");
        } catch (IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage() + "\n");
        }
    }

    /**
     * Option 2: Records or updates marks for a subject for a given student.
     */
    private static void handleRecordOrUpdateMarks(Scanner scanner, GradeManager manager) {
        System.out.println("\n--- [2] Record or Update Subject Marks ---");
        if (manager.getTotalStudents() == 0) {
            System.out.println("[!] No students registered yet. Please add a student first.\n");
            return;
        }

        String id = readNonEmptyString(scanner, "Enter Student ID: ");
        Student student = manager.findStudentById(id);

        if (student == null) {
            System.out.println("[Error] Student with ID '" + id + "' not found.\n");
            return;
        }

        System.out.println("Found student: " + student.getName() + " (ID: " + student.getStudentId() + ")");
        String subject = readNonEmptyString(scanner, "Enter Subject Name (e.g., Mathematics): ");
        double mark = readDoubleInRange(scanner, "Enter Numeric Mark (0.0 to 100.0): ", 0.0, 100.0);

        boolean wasUpdate = student.getGrades().containsKey(subject.trim());
        try {
            manager.recordMarks(id, subject, mark);
            if (wasUpdate) {
                System.out.printf("[Success] Updated marks for '%s': %.2f (Student: %s)%n%n",
                        subject.trim(), mark, student.getName());
            } else {
                System.out.printf("[Success] Recorded new marks for '%s': %.2f (Student: %s)%n%n",
                        subject.trim(), mark, student.getName());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage() + "\n");
        }
    }

    /**
     * Option 3: Searches for students by exact ID or partial name.
     */
    private static void handleSearchStudent(Scanner scanner, GradeManager manager) {
        System.out.println("\n--- [3] Search Student ---");
        if (manager.getTotalStudents() == 0) {
            System.out.println("[!] No students registered yet.\n");
            return;
        }

        String query = readNonEmptyString(scanner, "Enter Student ID or Name to search: ");
        List<Student> matches = manager.searchStudents(query);

        if (matches.isEmpty()) {
            System.out.println("[!] No matching student found for query: '" + query + "'.\n");
            return;
        }

        System.out.printf("%nFound %d matching student(s):%n", matches.size());
        for (Student s : matches) {
            String avgStr = s.hasMarks() ? String.format("%.2f%%", s.calculateAverage()) : "No marks yet";
            String gpaStr = s.hasMarks() ? String.format("%.2f GPA", s.calculateGPA()) : "N/A";
            System.out.printf("  • ID: %-10s | Name: %-20s | Subjects: %-2d | %s | %s%n",
                    s.getStudentId(), s.getName(), s.getSubjectCount(), avgStr, gpaStr);
        }
        System.out.println();
    }

    /**
     * Option 4: Generates and prints an individual student report card.
     */
    private static void handleDisplayReportCard(Scanner scanner, GradeManager manager) {
        System.out.println("\n--- [4] Display Student Report Card ---");
        if (manager.getTotalStudents() == 0) {
            System.out.println("[!] No students registered yet.\n");
            return;
        }

        String id = readNonEmptyString(scanner, "Enter Student ID: ");
        String report = manager.generateReportCard(id);
        System.out.println("\n" + report);
    }

    /**
     * Option 5: Displays all registered students in a neat formatted table.
     */
    private static void handleDisplayAllStudents(GradeManager manager) {
        System.out.println("\n--- [5] All Registered Students ---");
        manager.displayAllStudents();
    }

    /**
     * Option 6: Calculates and displays the class average.
     */
    private static void handleCalculateClassAverage(GradeManager manager) {
        System.out.println("\n--- [6] Calculate Class Average ---");
        int totalStudents = manager.getTotalStudents();
        List<Student> activeStudents = manager.getStudentsWithMarks();

        if (totalStudents == 0) {
            System.out.println("[!] No students registered in the system.\n");
            return;
        }

        if (activeStudents.isEmpty()) {
            System.out.println("[!] No marks recorded yet for any of the " + totalStudents + " registered student(s).");
            System.out.println("    Class average cannot be computed without recorded scores.\n");
            return;
        }

        double classAvg = manager.calculateClassAverage();
        double overallMarksAvg = manager.calculateOverallMarksAverage();

        System.out.println("------------------------------------------------------------");
        System.out.printf("Total Registered Students : %d%n", totalStudents);
        System.out.printf("Students with Marks       : %d%n", activeStudents.size());
        System.out.printf("Students Pending Marks    : %d (excluded from calculation)%n",
                totalStudents - activeStudents.size());
        System.out.println("------------------------------------------------------------");
        System.out.printf("Class Average (Student Mean) : %.2f%%%n", classAvg);
        System.out.printf("All-Marks Grand Mean         : %.2f%%%n", overallMarksAvg);
        System.out.println("------------------------------------------------------------");
        System.out.println("Note: 'Class Average' is the average of individual student");
        System.out.println("averages, treating each active student with equal weight.\n");
    }

    /**
     * Option 7: Calculates and displays the median student score (handling odd & even counts).
     */
    private static void handleCalculateMedianScore(GradeManager manager) {
        System.out.println("\n--- [7] Calculate Median Student Score ---");
        int totalStudents = manager.getTotalStudents();
        List<Student> activeStudents = manager.getStudentsWithMarks();

        if (totalStudents == 0) {
            System.out.println("[!] No students registered in the system.\n");
            return;
        }

        if (activeStudents.isEmpty()) {
            System.out.println("[!] No marks recorded yet for any of the " + totalStudents + " registered student(s).");
            System.out.println("    Median score cannot be computed without recorded scores.\n");
            return;
        }

        double median = manager.calculateMedianScore();
        int activeCount = activeStudents.size();
        String parity = (activeCount % 2 == 0) ? "Even (average of 2 middle scores)" : "Odd (exact middle score)";

        System.out.println("------------------------------------------------------------");
        System.out.printf("Active Students Count  : %d (%s)%n", activeCount, parity);
        System.out.printf("Median Student Score   : %.2f%%%n", median);
        System.out.println("------------------------------------------------------------");
        System.out.println("Note: Students without recorded marks are excluded.\n");
    }

    // ==========================================
    // Robust Scanner Input Helpers
    // ==========================================
    /**
     * Reads a menu choice from the console, validating that the input
     * is an integer within [min, max].
     *
     * Prevents crashing when user enters text or out-of-range numbers.
     */
    private static int readMenuChoice(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Select an option (" + min + "-" + max + "): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("[!] Input cannot be empty. Please enter a number between " + min + " and " + max + ".");
                continue;
            }

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[!] Choice out of range. Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. '" + input + "' is not a valid number.");
            }
        }
    }

    /**
     * Prompts the user repeatedly until a non-blank string is entered.
     */
    private static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("[!] Input cannot be blank or whitespace. Please try again.");
        }
    }

    /**
     * Reads a floating point number within [min, max], validating numeric format.
     */
    private static double readDoubleInRange(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("[!] Input cannot be empty. Please enter a number between " + min + " and " + max + ".");
                continue;
            }

            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[!] Value must be between %.1f and %.1f. Please re-enter.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. '" + input + "' is not a valid number. Please re-enter.");
            }
        }
    }
}
