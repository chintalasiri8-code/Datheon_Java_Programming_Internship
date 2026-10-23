import java.util.List;
import java.util.Map;

/**
 * Automated test suite to thoroughly verify all normal operations,
 * boundary conditions, and edge cases for the Student Grade Management System.
 */
public class SystemVerifier {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   RUNNING AUTOMATED VERIFICATION SUITE FOR GRADE SYSTEM    ");
        System.out.println("============================================================\n");

        testStudentCreationAndValidation();
        testDuplicateStudentPrevention();
        testMarkRecordingAndOverwriting();
        testAverageAndGPACalculations();
        testMedianCalculationOddAndEven();
        testHandlingStudentsWithNoMarks();
        testMissingStudentHandling();
        testEncapsulationProtection();
        testReportCardGeneration();

        System.out.println("\n============================================================");
        System.out.printf("TEST SUMMARY: %d Passed, %d Failed%n", testsPassed, testsFailed);
        System.out.println("============================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void testStudentCreationAndValidation() {
        System.out.println("--- Test Group 1: Student Creation and Input Validation ---");
        Student s1 = new Student("S101", "Alice Johnson");
        assertTrue(s1.getStudentId().equals("S101"), "Student ID is set and retrievable");
        assertTrue(s1.getName().equals("Alice Johnson"), "Student Name is set and retrievable");
        assertTrue(!s1.hasMarks(), "Newly created student has no marks");
        assertTrue(s1.calculateAverage() == 0.0, "Average is 0.0 when student has no marks");
        assertTrue(s1.calculateGPA() == 0.0, "GPA is 0.0 when student has no marks");

        // Blank ID validation
        boolean caughtBlankId = false;
        try {
            new Student("   ", "Valid Name");
        } catch (IllegalArgumentException e) {
            caughtBlankId = true;
        }
        assertTrue(caughtBlankId, "Reject blank Student ID");

        // Blank Name validation
        boolean caughtBlankName = false;
        try {
            new Student("S102", "   ");
        } catch (IllegalArgumentException e) {
            caughtBlankName = true;
        }
        assertTrue(caughtBlankName, "Reject blank Student Name");

        // Invalid mark (< 0.0 and > 100.0)
        boolean caughtNegativeMark = false;
        try {
            s1.addOrUpdateMark("Math", -5.0);
        } catch (IllegalArgumentException e) {
            caughtNegativeMark = true;
        }
        assertTrue(caughtNegativeMark, "Reject negative mark (-5.0)");

        boolean caughtOver100Mark = false;
        try {
            s1.addOrUpdateMark("Math", 105.0);
        } catch (IllegalArgumentException e) {
            caughtOver100Mark = true;
        }
        assertTrue(caughtOver100Mark, "Reject mark exceeding 100.0 (105.0)");

        // Blank subject validation
        boolean caughtBlankSubject = false;
        try {
            s1.addOrUpdateMark("   ", 85.0);
        } catch (IllegalArgumentException e) {
            caughtBlankSubject = true;
        }
        assertTrue(caughtBlankSubject, "Reject blank subject name");
    }

    private static void testDuplicateStudentPrevention() {
        System.out.println("\n--- Test Group 2: Duplicate ID Prevention in GradeManager ---");
        GradeManager manager = new GradeManager();
        boolean addedFirst = manager.registerStudent("S01", "Bob Smith");
        assertTrue(addedFirst, "First student registration succeeds");
        assertTrue(manager.isDuplicateId("S01"), "isDuplicateId detects existing ID");
        assertTrue(manager.isDuplicateId("s01"), "isDuplicateId is case-insensitive");

        boolean duplicateRejected = false;
        try {
            manager.registerStudent("S01", "Bob Duplicate");
        } catch (IllegalArgumentException e) {
            duplicateRejected = true;
        }
        assertTrue(duplicateRejected, "Registering duplicate ID throws IllegalArgumentException");
        assertTrue(manager.getTotalStudents() == 1, "Duplicate student is not added to list");
    }

    private static void testMarkRecordingAndOverwriting() {
        System.out.println("\n--- Test Group 3: Mark Recording and Overwriting Existing Subjects ---");
        GradeManager manager = new GradeManager();
        manager.registerStudent("S01", "Charlie Brown");

        manager.recordMarks("S01", "Mathematics", 75.0);
        Student s = manager.findStudentById("S01");
        assertTrue(s.getSubjectCount() == 1, "Subject count is 1 after adding Mathematics");
        assertTrue(s.getGrades().get("Mathematics") == 75.0, "Mark is recorded as 75.0");

        // Overwrite existing mark (must replace, not create duplicate)
        manager.recordMarks("S01", "Mathematics", 88.0);
        assertTrue(s.getSubjectCount() == 1, "Subject count remains 1 after updating Mathematics");
        assertTrue(s.getGrades().get("Mathematics") == 88.0, "Mark is updated to 88.0");

        // Add a second subject
        manager.recordMarks("S01", "Physics", 92.0);
        assertTrue(s.getSubjectCount() == 2, "Subject count becomes 2 after adding Physics");
    }

    private static void testAverageAndGPACalculations() {
        System.out.println("\n--- Test Group 4: Average and GPA Calculations ---");
        Student s = new Student("S99", "Diana Prince");
        // Math: 95.0 -> Grade Point 4.0 (A)
        // Physics: 85.0 -> Grade Point 3.0 (B)
        // History: 75.0 -> Grade Point 2.0 (C)
        // Chemistry: 65.0 -> Grade Point 1.0 (D)
        s.addOrUpdateMark("Math", 95.0);
        s.addOrUpdateMark("Physics", 85.0);
        s.addOrUpdateMark("History", 75.0);
        s.addOrUpdateMark("Chemistry", 65.0);

        // Average = (95 + 85 + 75 + 65) / 4 = 320 / 4 = 80.0
        double expectedAvg = 80.0;
        assertTrue(Math.abs(s.calculateAverage() - expectedAvg) < 0.001, "Average calculation is accurate (80.0%)");

        // GPA = (4.0 + 3.0 + 2.0 + 1.0) / 4 = 10.0 / 4 = 2.50
        double expectedGPA = 2.50;
        assertTrue(Math.abs(s.calculateGPA() - expectedGPA) < 0.001, "GPA calculation is accurate (2.50)");
        assertTrue(s.getOverallLetterGrade().equals("B"), "Overall letter grade for 80% is 'B'");
    }

    private static void testMedianCalculationOddAndEven() {
        System.out.println("\n--- Test Group 5: Median Calculation (Odd vs Even counts) ---");
        GradeManager manager = new GradeManager();

        // 1. Zero students with marks
        manager.registerStudent("S01", "Student 1");
        assertTrue(manager.calculateMedianScore() == 0.0, "Median is 0.0 when no students have marks");

        // 2. Odd count: 1 student (score 80) -> Median = 80.0
        manager.recordMarks("S01", "English", 80.0);
        assertTrue(manager.calculateMedianScore() == 80.0, "Median with 1 student (80) is 80.0");

        // 3. Even count: 2 students (scores 80, 90) -> Median = (80 + 90)/2 = 85.0
        manager.registerStudent("S02", "Student 2");
        manager.recordMarks("S02", "English", 90.0);
        assertTrue(manager.calculateMedianScore() == 85.0, "Median with 2 students (80, 90) is 85.0");

        // 4. Odd count: 3 students (scores 70, 80, 90) -> Median = 80.0
        manager.registerStudent("S03", "Student 3");
        manager.recordMarks("S03", "English", 70.0);
        assertTrue(manager.calculateMedianScore() == 80.0, "Median with 3 students (70, 80, 90) is 80.0");

        // 5. Even count: 4 students (scores 60, 70, 80, 90) -> Median = (70 + 80)/2 = 75.0
        manager.registerStudent("S04", "Student 4");
        manager.recordMarks("S04", "English", 60.0);
        assertTrue(manager.calculateMedianScore() == 75.0, "Median with 4 students (60, 70, 80, 90) is 75.0");
    }

    private static void testHandlingStudentsWithNoMarks() {
        System.out.println("\n--- Test Group 6: Exclusion of Students Without Marks From Statistics ---");
        GradeManager manager = new GradeManager();
        manager.registerStudent("S01", "Active Student 1");
        manager.recordMarks("S01", "Art", 80.0); // Avg = 80.0

        manager.registerStudent("S02", "Active Student 2");
        manager.recordMarks("S02", "Art", 100.0); // Avg = 100.0

        manager.registerStudent("S03", "Empty Student 3"); // No marks recorded!

        // Class average should only consider S01 and S02: (80 + 100) / 2 = 90.0
        // If S03 were counted as 0, average would incorrectly be (80 + 100 + 0) / 3 = 60.0!
        double classAvg = manager.calculateClassAverage();
        assertTrue(Math.abs(classAvg - 90.0) < 0.001,
                "Class average correctly excludes student with no marks (Expected: 90.0, Received: " + classAvg + ")");

        // Median should only consider S01 and S02: (80 + 100) / 2 = 90.0
        double median = manager.calculateMedianScore();
        assertTrue(Math.abs(median - 90.0) < 0.001,
                "Class median correctly excludes student with no marks (Expected: 90.0, Received: " + median + ")");
    }

    private static void testMissingStudentHandling() {
        System.out.println("\n--- Test Group 7: Missing Student Handling ---");
        GradeManager manager = new GradeManager();

        Student notFound = manager.findStudentById("NON_EXISTENT");
        assertTrue(notFound == null, "findStudentById returns null for missing student");

        boolean errorCaught = false;
        try {
            manager.recordMarks("NON_EXISTENT", "Math", 90.0);
        } catch (IllegalArgumentException e) {
            errorCaught = true;
        }
        assertTrue(errorCaught, "recordMarks throws IllegalArgumentException for missing student");

        String report = manager.generateReportCard("NON_EXISTENT");
        assertTrue(report.contains("not found"), "generateReportCard handles missing student gracefully");
    }

    private static void testEncapsulationProtection() {
        System.out.println("\n--- Test Group 8: Encapsulation and Immutability of Grades Map ---");
        Student s = new Student("S10", "Eve Hacker");
        s.addOrUpdateMark("CS", 90.0);

        Map<String, Double> externalGradesView = s.getGrades();
        boolean modificationBlocked = false;
        try {
            // Attempting to illegally inject or clear grades directly via returned reference
            externalGradesView.put("HackedSubject", 100.0);
        } catch (UnsupportedOperationException e) {
            modificationBlocked = true;
        }
        assertTrue(modificationBlocked, "Direct modification of grades map via getter throws UnsupportedOperationException");
        assertTrue(s.getSubjectCount() == 1, "Internal grades map remains safely encapsulated and uncorrupted");
    }

    private static void testReportCardGeneration() {
        System.out.println("\n--- Test Group 9: Report Card Generation ---");
        GradeManager manager = new GradeManager();
        manager.registerStudent("S01", "Grace Hopper");
        manager.recordMarks("S01", "Computer Science", 98.0);
        manager.recordMarks("S01", "Calculus", 91.0);

        String reportCard = manager.generateReportCard("S01");
        assertTrue(reportCard.contains("Grace Hopper"), "Report card contains student name");
        assertTrue(reportCard.contains("Computer Science"), "Report card contains subject");
        assertTrue(reportCard.contains("4.00"), "Report card contains cumulative GPA 4.00");
        assertTrue(reportCard.contains("Excellent"), "Report card contains academic standing");
    }
}
