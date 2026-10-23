import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The GradeManager class manages a collection of students and encapsulates
 * all business logic relating to student registration, mark recording,
 * report generation, and class-level statistical calculations (average and median).
 *
 * Core OOP Principles applied:
 * 1. Abstraction: High-level operations (like calculating class median or generating
 *    report cards) are exposed while the sorting, summation, and formatting details
 *    are kept encapsulated inside methods.
 * 2. Separation of Concerns: Business logic is decoupled from direct console scanner input.
 *    Methods return boolean results, formatted Strings, or computed values.
 */
public class GradeManager {

    // ==========================================
    // 1. Private Fields
    // ==========================================
    // ArrayList is chosen because it allows dynamic resizing as students are added,
    // maintains insertion order, and supports efficient indexed and sequential access.
    private final ArrayList<Student> students;

    // ==========================================
    // 2. Constructor
    // ==========================================
    public GradeManager() {
        this.students = new ArrayList<>();
    }

    // ==========================================
    // 3. Student Registration & Lookup
    // ==========================================
    /**
     * Checks if a student ID already exists in the system (case-insensitive).
     *
     * @param studentId the ID to check
     * @return true if the ID already exists; false otherwise
     */
    public boolean isDuplicateId(String studentId) {
        if (!Student.isValidId(studentId)) {
            return false;
        }
        for (Student s : this.students) {
            if (s.getStudentId().equalsIgnoreCase(studentId.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers a new student in the system after validating input and verifying
     * that the student ID is unique.
     *
     * @param studentId unique ID for the student
     * @param name      full name of the student
     * @return true if registration succeeded
     * @throws IllegalArgumentException if ID/name is invalid, or ID already exists
     */
    public boolean registerStudent(String studentId, String name) {
        if (!Student.isValidId(studentId)) {
            throw new IllegalArgumentException("Cannot register student: Student ID cannot be null or blank.");
        }
        if (!Student.isValidName(name)) {
            throw new IllegalArgumentException("Cannot register student: Student name cannot be null or blank.");
        }
        if (isDuplicateId(studentId)) {
            throw new IllegalArgumentException("Cannot register student: A student with ID '" + studentId.trim() + "' already exists.");
        }

        Student newStudent = new Student(studentId, name);
        this.students.add(newStudent);
        return true;
    }

    /**
     * Finds a student by their unique ID (case-insensitive).
     *
     * @param studentId the ID of the student to locate
     * @return the matching Student instance, or null if not found
     */
    public Student findStudentById(String studentId) {
        if (!Student.isValidId(studentId)) {
            return null;
        }
        for (Student s : this.students) {
            if (s.getStudentId().equalsIgnoreCase(studentId.trim())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Searches students by exact ID or partial name (case-insensitive).
     *
     * @param query search term (ID or name fragment)
     * @return list of matching Student objects (empty if none match)
     */
    public List<Student> searchStudents(String query) {
        List<Student> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        String term = query.trim().toLowerCase();
        for (Student s : this.students) {
            if (s.getStudentId().toLowerCase().contains(term) || s.getName().toLowerCase().contains(term)) {
                results.add(s);
            }
        }
        return results;
    }

    // ==========================================
    // 4. Mark Recording
    // ==========================================
    /**
     * Records or updates a subject mark for an existing student.
     *
     * @param studentId the ID of the student
     * @param subject   the name of the subject
     * @param mark      the numeric mark (0.0 to 100.0)
     * @return true if recorded successfully
     * @throws IllegalArgumentException if student is not found or mark/subject is invalid
     */
    public boolean recordMarks(String studentId, String subject, double mark) {
        Student student = findStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }
        return student.addOrUpdateMark(subject, mark);
    }

    // ==========================================
    // 5. Academic Statistics Calculations
    // ==========================================
    /**
     * Returns a list of all students who currently have at least one recorded mark.
     * Students with no marks are strictly excluded from statistical calculations
     * that require academic scores to prevent skewing class metrics.
     *
     * @return list of students with recorded marks
     */
    public List<Student> getStudentsWithMarks() {
        List<Student> list = new ArrayList<>();
        for (Student s : this.students) {
            if (s.hasMarks()) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * Calculates the Class Average.
     *
     * CRITICAL ACADEMIC DEFINITION:
     * In this system, "Class Average" is defined as the arithmetic mean of all
     * individual student overall averages, calculated across all students who
     * have at least one recorded mark.
     *
     * Why this definition?
     * Weighting each student's average equally treats each student as one benchmark unit,
     * preventing a student taking 8 subjects from having twice the influence on class
     * performance compared to a student taking 4 subjects.
     *
     * If no students have recorded marks, returns 0.0.
     *
     * @return class average score (0.0 to 100.0)
     */
    public double calculateClassAverage() {
        List<Student> activeStudents = getStudentsWithMarks();
        if (activeStudents.isEmpty()) {
            return 0.0;
        }

        double sumOfAverages = 0.0;
        for (Student s : activeStudents) {
            sumOfAverages += s.calculateAverage();
        }

        return sumOfAverages / activeStudents.size();
    }

    /**
     * Alternative Statistic: Calculates the average of EVERY recorded subject mark
     * across the entire class (Grand Mean of all marks).
     *
     * Provided for comprehensive academic analysis and comparison against the student-level average.
     *
     * @return overall marks average, or 0.0 if no marks exist
     */
    public double calculateOverallMarksAverage() {
        int totalMarkEntries = 0;
        double totalSum = 0.0;

        for (Student s : this.students) {
            for (double mark : s.getGrades().values()) {
                totalSum += mark;
                totalMarkEntries++;
            }
        }

        if (totalMarkEntries == 0) {
            return 0.0;
        }
        return totalSum / totalMarkEntries;
    }

    /**
     * Calculates the Median of the students' average scores.
     *
     * ALGORITHM:
     * 1. Collect the overall average of every student who has at least one recorded mark.
     * 2. Sort the list of averages in ascending numerical order.
     * 3. Handle Odd vs Even count:
     *    - If size N is odd:
     *        median = element at index (N / 2)
     *        Example: [70, 80, 90] -> size 3 -> index 3/2 = 1 -> median = 80.0
     *    - If size N is even:
     *        median = average of the two middle elements at (N/2 - 1) and (N/2)
     *        Example: [70, 80, 90, 100] -> size 4 -> indexes 1 and 2 (80 and 90) -> (80 + 90)/2 = 85.0
     * 4. If no students have recorded marks, returns 0.0.
     *
     * @return median student average score
     */
    public double calculateMedianScore() {
        List<Student> activeStudents = getStudentsWithMarks();
        if (activeStudents.isEmpty()) {
            return 0.0;
        }

        // Collect all active student averages
        ArrayList<Double> averages = new ArrayList<>();
        for (Student s : activeStudents) {
            averages.add(s.calculateAverage());
        }

        // Sort in ascending order
        Collections.sort(averages);

        int count = averages.size();
        if (count % 2 != 0) {
            // Odd number of students: pick the middle element
            return averages.get(count / 2);
        } else {
            // Even number of students: arithmetic mean of the two middle elements
            double middle1 = averages.get(count / 2 - 1);
            double middle2 = averages.get(count / 2);
            return (middle1 + middle2) / 2.0;
        }
    }

    // ==========================================
    // 6. Report Card Generation
    // ==========================================
    /**
     * Generates a beautifully formatted text report card for an individual student.
     *
     * @param studentId the ID of the student
     * @return a multi-line formatted report card string
     */
    public String generateReportCard(String studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return "Error: Student with ID '" + studentId + "' not found in the system.\n";
        }

        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(62);
        String subLine = "-".repeat(62);

        sb.append(line).append("\n");
        sb.append(String.format("           ACADEMIC PERFORMANCE REPORT CARD%n"));
        sb.append(line).append("\n");
        sb.append(String.format("Student ID   : %-20s%n", student.getStudentId()));
        sb.append(String.format("Student Name : %-20s%n", student.getName()));
        sb.append(subLine).append("\n");

        if (!student.hasMarks()) {
            sb.append("No subject marks have been recorded for this student yet.\n");
            sb.append(line).append("\n");
            return sb.toString();
        }

        sb.append(String.format("%-25s | %-8s | %-6s | %-12s%n", "Subject", "Mark", "Grade", "Grade Point"));
        sb.append(subLine).append("\n");

        Map<String, Double> grades = student.getGrades();
        for (Map.Entry<String, Double> entry : grades.entrySet()) {
            String subject = entry.getKey();
            double mark = entry.getValue();
            String letterGrade = Student.convertMarkToLetterGrade(mark);
            double gp = Student.convertMarkToGradePoint(mark);

            sb.append(String.format("%-25s | %6.2f   |   %-4s | %4.1f / 4.0%n",
                    subject, mark, letterGrade, gp));
        }

        sb.append(subLine).append("\n");
        sb.append(String.format("Total Subjects Recorded : %d%n", student.getSubjectCount()));
        sb.append(String.format("Overall Percentage      : %.2f%%%n", student.calculateAverage()));
        sb.append(String.format("Cumulative GPA (4.0)    : %.2f%n", student.calculateGPA()));
        sb.append(String.format("Overall Letter Grade    : %s%n", student.getOverallLetterGrade()));

        // Academic Standing Remarks
        String standing;
        double gpa = student.calculateGPA();
        if (gpa >= 3.5) {
            standing = "Excellent / High Distinction";
        } else if (gpa >= 3.0) {
            standing = "Very Good / Honor Roll";
        } else if (gpa >= 2.0) {
            standing = "Satisfactory / Good Standing";
        } else if (gpa >= 1.0) {
            standing = "Academic Warning / Needs Improvement";
        } else {
            standing = "Academic Probation / Failing";
        }
        sb.append(String.format("Academic Standing       : %s%n", standing));
        sb.append(line).append("\n");

        return sb.toString();
    }

    // ==========================================
    // 7. Table Display of All Students
    // ==========================================
    /**
     * Prints a formatted table of all registered students to standard output.
     * Displays ID, Name, Subject count, Average, GPA, and Overall Grade.
     */
    public void displayAllStudents() {
        if (this.students.isEmpty()) {
            System.out.println("\n[!] No students registered in the system yet.\n");
            return;
        }

        String border = "+------------+----------------------+---------------+---------+--------+-------+";
        System.out.println("\n" + border);
        System.out.println(String.format("| %-10s | %-20s | %-13s | %-7s | %-6s | %-5s |",
                "Student ID", "Student Name", "Subjects Count", "Average", "GPA", "Grade"));
        System.out.println(border);

        for (Student s : this.students) {
            String avgStr = s.hasMarks() ? String.format("%.2f%%", s.calculateAverage()) : "N/A";
            String gpaStr = s.hasMarks() ? String.format("%.2f", s.calculateGPA()) : "N/A";
            String gradeStr = s.hasMarks() ? s.getOverallLetterGrade() : "N/A";

            System.out.println(String.format("| %-10s | %-20s | %13d | %7s | %6s | %5s |",
                    s.getStudentId(),
                    truncate(s.getName(), 20),
                    s.getSubjectCount(),
                    avgStr,
                    gpaStr,
                    gradeStr));
        }

        System.out.println(border);
        int total = this.students.size();
        int withMarks = getStudentsWithMarks().size();
        int withoutMarks = total - withMarks;
        System.out.printf("Summary: Total Students: %d | With Marks: %d | Pending Marks: %d%n%n",
                total, withMarks, withoutMarks);
    }

    /**
     * Truncates a string to maxLength with an ellipsis if it exceeds the limit.
     */
    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    // ==========================================
    // 8. Utility & Inspection Methods
    // ==========================================
    public int getTotalStudents() {
        return this.students.size();
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(this.students);
    }
}
