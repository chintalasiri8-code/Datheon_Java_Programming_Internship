import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The Student class represents an individual student in the Grade Management System.
 *
 * Core OOP Principles applied:
 * 1. Encapsulation: All fields (studentId, name, grades) are marked private.
 *    Access and modifications are strictly controlled through validated methods.
 * 2. Defensive Copying: The internal grades HashMap is never returned directly;
 *    instead, an unmodifiable view is provided so outside code cannot bypass validation.
 */
public class Student {

    // ==========================================
    // 1. Private Fields (Encapsulation)
    // ==========================================
    private String studentId;
    private String name;
    private final HashMap<String, Double> grades;

    // ==========================================
    // 2. GPA Scale Constants (Configurable Demonstration Scale)
    // ==========================================
    // Standard 4.0 Academic Scale benchmarks:
    // 90.0 - 100.0 : 4.0 Grade Points (A)
    // 80.0 - 89.99 : 3.0 Grade Points (B)
    // 70.0 - 79.99 : 2.0 Grade Points (C)
    // 60.0 - 69.99 : 1.0 Grade Points (D)
    // Below 60.0   : 0.0 Grade Points (F)
    public static final double GRADE_A_MIN = 90.0;
    public static final double GRADE_B_MIN = 80.0;
    public static final double GRADE_C_MIN = 70.0;
    public static final double GRADE_D_MIN = 60.0;

    public static final double GP_A = 4.0;
    public static final double GP_B = 3.0;
    public static final double GP_C = 2.0;
    public static final double GP_D = 1.0;
    public static final double GP_F = 0.0;

    // ==========================================
    // 3. Constructor
    // ==========================================
    /**
     * Constructs a new Student with a unique ID and a student name.
     * Validates that both arguments are non-null and not blank.
     *
     * @param studentId the unique alphanumeric ID of the student
     * @param name      the full name of the student
     * @throws IllegalArgumentException if studentId or name is blank/null
     */
    public Student(String studentId, String name) {
        if (!isValidId(studentId)) {
            throw new IllegalArgumentException("Student ID cannot be null or blank.");
        }
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Student name cannot be null or blank.");
        }

        this.studentId = studentId.trim();
        this.name = name.trim();
        this.grades = new HashMap<>();
    }

    // ==========================================
    // 4. Validation Helper Methods
    // ==========================================
    /**
     * Checks if a student ID is valid (non-null and not empty/blank).
     */
    public static boolean isValidId(String id) {
        return id != null && !id.trim().isEmpty();
    }

    /**
     * Checks if a student name is valid (non-null and not empty/blank).
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Checks if a subject name is valid (non-null and not empty/blank).
     */
    public static boolean isValidSubject(String subject) {
        return subject != null && !subject.trim().isEmpty();
    }

    /**
     * Validates that a numeric mark is between 0.0 and 100.0 inclusive.
     */
    public static boolean isValidMark(double mark) {
        return !Double.isNaN(mark) && mark >= 0.0 && mark <= 100.0;
    }

    // ==========================================
    // 5. Getters and Setters
    // ==========================================
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (!isValidId(studentId)) {
            throw new IllegalArgumentException("Student ID cannot be null or blank.");
        }
        this.studentId = studentId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Student name cannot be null or blank.");
        }
        this.name = name.trim();
    }

    /**
     * Returns a read-only view of the student's grades map.
     *
     * WHY THIS MATTERS FOR OOP:
     * If we returned 'this.grades' directly, any outside class could call
     * student.getGrades().clear() or put an invalid mark like -999.0 directly.
     * By returning Collections.unmodifiableMap, we preserve strict encapsulation.
     *
     * @return an unmodifiable Map containing subject names and marks
     */
    public Map<String, Double> getGrades() {
        return Collections.unmodifiableMap(this.grades);
    }

    // ==========================================
    // 6. Subject Mark Management
    // ==========================================
    /**
     * Adds a new subject mark or updates an existing subject's mark.
     * If the subject already exists, its previous mark is safely overwritten.
     *
     * @param subject the name of the academic subject
     * @param mark    the score obtained (0.0 to 100.0)
     * @return true if the mark was successfully recorded or updated
     * @throws IllegalArgumentException if the subject is blank or mark is out of range
     */
    public boolean addOrUpdateMark(String subject, double mark) {
        if (!isValidSubject(subject)) {
            throw new IllegalArgumentException("Subject name cannot be null or blank.");
        }
        if (!isValidMark(mark)) {
            throw new IllegalArgumentException("Mark must be a numeric value between 0.0 and 100.0. Received: " + mark);
        }

        // Standardize subject key to avoid duplicate keys like "Math" vs "math" or " Math "
        String formattedSubject = subject.trim();
        this.grades.put(formattedSubject, mark);
        return true;
    }

    /**
     * Checks if this student has at least one recorded mark.
     *
     * @return true if at least one subject has been recorded; false otherwise
     */
    public boolean hasMarks() {
        return !this.grades.isEmpty();
    }

    /**
     * Returns the total number of subjects recorded for this student.
     *
     * @return count of recorded subjects
     */
    public int getSubjectCount() {
        return this.grades.size();
    }

    // ==========================================
    // 7. Academic Calculations (Average & GPA)
    // ==========================================
    /**
     * Calculates the arithmetic mean (average) of all recorded marks for this student.
     *
     * Formula: sum of all marks / total number of subjects
     * If no marks are recorded, returns 0.0 to prevent division by zero.
     *
     * @return average score between 0.0 and 100.0, or 0.0 if no marks exist
     */
    public double calculateAverage() {
        if (this.grades.isEmpty()) {
            return 0.0;
        }

        double totalMarks = 0.0;
        for (double mark : this.grades.values()) {
            totalMarks += mark;
        }

        return totalMarks / this.grades.size();
    }

    /**
     * Converts an individual percentage mark into Grade Points on a 4.0 scale.
     *
     * Demonstration Scale:
     * - [90.0, 100.0] -> 4.0 (A)
     * - [80.0, 89.99] -> 3.0 (B)
     * - [70.0, 79.99] -> 2.0 (C)
     * - [60.0, 69.99] -> 1.0 (D)
     * - [0.0,  59.99] -> 0.0 (F)
     *
     * @param mark score between 0.0 and 100.0
     * @return grade points (0.0 to 4.0)
     */
    public static double convertMarkToGradePoint(double mark) {
        if (mark >= GRADE_A_MIN) {
            return GP_A;
        } else if (mark >= GRADE_B_MIN) {
            return GP_B;
        } else if (mark >= GRADE_C_MIN) {
            return GP_C;
        } else if (mark >= GRADE_D_MIN) {
            return GP_D;
        } else {
            return GP_F;
        }
    }

    /**
     * Converts a percentage mark into a standard letter grade.
     *
     * @param mark score between 0.0 and 100.0
     * @return Letter Grade ("A", "B", "C", "D", or "F")
     */
    public static String convertMarkToLetterGrade(double mark) {
        if (mark >= GRADE_A_MIN) {
            return "A";
        } else if (mark >= GRADE_B_MIN) {
            return "B";
        } else if (mark >= GRADE_C_MIN) {
            return "C";
        } else if (mark >= GRADE_D_MIN) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * Calculates the cumulative Grade Point Average (GPA) for this student.
     *
     * Method:
     * 1. Each individual subject mark is converted to its respective grade point (0.0 to 4.0).
     * 2. All subject grade points are summed up.
     * 3. The sum is divided by the total number of subjects.
     *
     * If the student has no recorded marks, returns 0.0.
     *
     * @return cumulative GPA on a 4.0 scale (0.00 to 4.00)
     */
    public double calculateGPA() {
        if (this.grades.isEmpty()) {
            return 0.0;
        }

        double totalGradePoints = 0.0;
        for (double mark : this.grades.values()) {
            totalGradePoints += convertMarkToGradePoint(mark);
        }

        return totalGradePoints / this.grades.size();
    }

    /**
     * Returns an overall letter grade corresponding to the student's overall average.
     *
     * @return "N/A" if no marks are recorded, otherwise "A", "B", "C", "D", or "F"
     */
    public String getOverallLetterGrade() {
        if (!hasMarks()) {
            return "N/A";
        }
        return convertMarkToLetterGrade(calculateAverage());
    }

    // ==========================================
    // 8. Standard Object Overrides
    // ==========================================
    @Override
    public String toString() {
        return "Student[ID='" + studentId + "', Name='" + name + "', Subjects=" + grades.size() + "]";
    }
}
