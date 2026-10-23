# Student Grade Management System (Java OOP)

A modular, clean, and beginner-friendly console application built using standard Java (Java 17+) and Object-Oriented Programming (OOP) principles. Developed as an internship project demonstrating real-world object modeling, encapsulation, collections, statistical computing, defensive programming, and robust user input handling.

---

## 1. Project Overview & Architecture

The application is structured into three cohesive Java files, cleanly dividing responsibilities:

```
STUDENT GRADE MANAGEMENT SYSTEM/
├── Student.java          # Domain entity (Encapsulation, Data Validation, GPA scale)
├── GradeManager.java     # Business logic & Collection manager (ArrayList, Statistics)
├── Main.java             # Console user interface (Scanner, Menu loop, Input validation)
├── SystemVerifier.java   # Automated test suite (39 verified unit & edge-case tests)
└── README.md             # Complete project guide and interview defense documentation
```

### Class Responsibilities & Data Flow

```
+-------------------------------------------------------------+
|                          Main.java                          |
|  - Interactive 8-option text menu                           |
|  - Validates user input (re-prompts on invalid data)        |
|  - Passes validated user commands to GradeManager           |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                      GradeManager.java                      |
|  - Holds ArrayList<Student>                                 |
|  - Enforces unique Student IDs                              |
|  - Computes class-wide statistics (Average & Median)        |
|  - Generates formatted Report Cards & Student Tables        |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                        Student.java                         |
|  - Private fields: studentId, name, HashMap<String, Double> |
|  - Validates marks (0.0 to 100.0) and non-blank names       |
|  - Safe read-only grades exposure (Encapsulation)           |
|  - Calculates individual Student Average & Cumulative GPA   |
+-------------------------------------------------------------+
```

---

## 2. Object-Oriented Programming (OOP) Concepts Applied

### 1. Encapsulation
- **Private Fields**: `studentId`, `name`, and the `grades` map in `Student` are strictly `private`. Outside classes cannot modify them directly.
- **Defensive Copying / Unmodifiable View**: `Student.getGrades()` returns `Collections.unmodifiableMap(this.grades)`. If external code attempts `student.getGrades().put("Math", 100.0)` or `student.getGrades().clear()`, Java throws an `UnsupportedOperationException`. This guarantees data integrity.
- **Controlled Modification**: The only way to add or update a mark is through `student.addOrUpdateMark(subject, mark)`, which enforces validation rules first.

### 2. Abstraction
- The complexity of sorting algorithms, median parity checks, GPA conversions, and formatted text alignment is hidden inside concise methods like `manager.calculateClassAverage()`, `manager.calculateMedianScore()`, and `manager.generateReportCard(id)`.
- `Main.java` does not need to know *how* `GradeManager` computes the median; it simply calls the method and displays the result.

### 3. Classes and Objects
- **`Student`** acts as a blueprint for an individual student, maintaining their personal details and academic marks.
- **`GradeManager`** acts as a domain controller managing multiple `Student` object instances in an `ArrayList<Student>`.

### 4. Collections: Why `ArrayList` and `HashMap`?
- **`ArrayList<Student>` in `GradeManager`**:
  - Dynamically resizes as new students register (unlike fixed-size arrays).
  - Preserves registration order.
  - Allows easy iteration for searching, reporting, and sorting.
- **`HashMap<String, Double>` in `Student`**:
  - Perfect key-value pair representation: Subject Name (Key) $\rightarrow$ Mark (Value).
  - Fast $O(1)$ constant-time lookup and updates.
  - Automatically handles updating: calling `grades.put("Math", 95.0)` replaces the previous mark for `"Math"` instead of creating duplicate subjects.

### 5. Discussion on Inheritance and Polymorphism
- **Why was inheritance not forced?**
  In this application, `Student` is the only academic entity. Creating an artificial `Person` superclass with only a `name` field would add boilerplate without providing genuine benefits.
- **When should inheritance be added?**
  If the application expands to include `Teacher`, `TeachingAssistant`, and `Administrator`, creating an abstract `Person` class (or `User` hierarchy) with polymorphic role permissions would be the right design choice.

---

## 3. Academic Grading Scale & Formulas

### 1. Cumulative GPA (4.0 Scale)
Because academic institutions use varying GPA policies, this system implements a standard, configurable 4.0 scale:

| Mark Range (%) | Letter Grade | Grade Points (GP) | Academic Standing |
| :--- | :---: | :---: | :--- |
| **90.00 – 100.00** | **A** | **4.0** | Excellent / High Distinction |
| **80.00 – 89.99**  | **B** | **3.0** | Very Good / Honor Roll |
| **70.00 – 79.99**  | **C** | **2.0** | Satisfactory / Good Standing |
| **60.00 – 69.99**  | **D** | **1.0** | Academic Warning / Needs Improvement |
| **0.00 – 59.99**   | **F** | **0.0** | Academic Probation / Failing |

**Formula**:
$$\text{Student GPA} = \frac{\sum (\text{Subject Grade Points})}{\text{Total Number of Recorded Subjects}}$$

### 2. Class Average Definition
- **Definition**: The **arithmetic mean of active students' overall averages** (students who have at least one recorded mark).
- **Rationale**: Weighting each student equally prevents a student taking 6 subjects from dominating the class benchmark compared to a student taking 3 subjects.
- **Students Without Marks**: Completely excluded from the calculation so they do not falsely drag down the class average.

### 3. Median Student Score Calculation
The active students' averages are collected and sorted in ascending order ($N = \text{number of active students}$):
- **If $N$ is Odd**:
  $$\text{Median} = \text{Element at index } \left\lfloor \frac{N}{2} \right\rfloor$$
  *Example*: $[70.0, 80.0, 90.0] \rightarrow \text{Median} = 80.0\%$
- **If $N$ is Even**:
  $$\text{Median} = \frac{\text{Element at } \left(\frac{N}{2} - 1\right) + \text{Element at } \left(\frac{N}{2}\right)}{2}$$
  *Example*: $[60.0, 70.0, 80.0, 90.0] \rightarrow \text{Median} = \frac{70.0 + 80.0}{2} = 75.0\%$

---

## 4. How to Compile and Run

### Prerequisites
- Java JDK 17 or later installed.
- Check version: `java -version` and `javac -version`.

### Terminal / Command Line
1. Open terminal or PowerShell in the project directory:
   ```bash
   cd "d:\JAVA PROJECTS\STUDENT GRADE MANAGEMENT SYSTEM"
   ```
2. Compile all `.java` source files:
   ```bash
   javac Student.java GradeManager.java Main.java
   ```
3. Run the application:
   ```bash
   java Main
   ```
4. Run the automated verification test suite:
   ```bash
   javac SystemVerifier.java
   java SystemVerifier
   ```

### Running in IDEs
- **IntelliJ IDEA**: Open the folder $\rightarrow$ Right-click `Main.java` $\rightarrow$ Run 'Main.main()'.
- **VS Code**: Open folder $\rightarrow$ Install *Extension Pack for Java* $\rightarrow$ Click **Run** above `public static void main` in `Main.java`.
- **Eclipse**: File $\rightarrow$ Open Projects from File System $\rightarrow$ Select folder $\rightarrow$ Run As $\rightarrow$ Java Application.

---

## 5. Sample Console Execution Walkthrough

```text
============================================================
       WELCOME TO STUDENT GRADE MANAGEMENT SYSTEM           
               Java OOP Internship Project                  
============================================================
A modular, robust, and beginner-friendly academic manager.

------------------------------------------------------------
                    MAIN MENU OPTIONS                       
------------------------------------------------------------
  1. Add Student
  2. Record or Update Marks
  3. Search Student
  4. Display Student Report Card
  5. Display All Students
  6. Calculate Class Average
  7. Calculate Median Student Score
  8. Exit
------------------------------------------------------------
Select an option (1-8): 1

--- [1] Add New Student ---
Enter Student ID (e.g., S101): S101
Enter Student Full Name: Ada Lovelace
[Success] Student 'Ada Lovelace' (ID: S101) registered successfully!

Select an option (1-8): 2

--- [2] Record or Update Subject Marks ---
Enter Student ID: S101
Found student: Ada Lovelace (ID: S101)
Enter Subject Name (e.g., Mathematics): Mathematics
Enter Numeric Mark (0.0 to 100.0): 95.0
[Success] Recorded new marks for 'Mathematics': 95.00 (Student: Ada Lovelace)

Select an option (1-8): 4

--- [4] Display Student Report Card ---
Enter Student ID: S101

==============================================================
           ACADEMIC PERFORMANCE REPORT CARD
==============================================================
Student ID   : S101                
Student Name : Ada Lovelace        
--------------------------------------------------------------
Subject                   | Mark     | Grade  | Grade Point 
--------------------------------------------------------------
Mathematics               |  95.00   |   A    |  4.0 / 4.0
Algorithms                |  90.00   |   A    |  4.0 / 4.0
--------------------------------------------------------------
Total Subjects Recorded : 2
Overall Percentage      : 92.50%
Cumulative GPA (4.0)    : 4.00
Overall Letter Grade    : A
Academic Standing       : Excellent / High Distinction
==============================================================
```

---

## 6. Assignment Requirements Verification Checklist

| Requirement | Class | Method / Mechanism | Verified Status |
| :--- | :--- | :--- | :---: |
| **Java 17+ Console Application** | All | Standard Java `Scanner`, no external dependencies | Verified |
| **Unique Student IDs** | `GradeManager` | `isDuplicateId()`, `registerStudent()` | Verified |
| **Input Validation (Non-blank names/IDs)** | `Student`, `Main` | `isValidId()`, `isValidName()`, `readNonEmptyString()` | Verified |
| **Numeric Marks [0.0 - 100.0]** | `Student`, `Main` | `isValidMark()`, `readDoubleInRange()` | Verified |
| **Encapsulation & Private Fields** | `Student` | `private studentId, name, grades; Collections.unmodifiableMap()` | Verified |
| **Add / Update Subject Marks** | `Student` | `addOrUpdateMark()` (replaces existing, preserves unique subjects) | Verified |
| **Search Student by ID or Name** | `GradeManager` | `findStudentById()`, `searchStudents()` | Verified |
| **Individual Student Average** | `Student` | `calculateAverage()` | Verified |
| **GPA on Configurable 4.0 Scale** | `Student` | `convertMarkToGradePoint()`, `calculateGPA()` | Verified |
| **Class Average Calculation** | `GradeManager` | `calculateClassAverage()` (Student mean, excludes empty students) | Verified |
| **Median Student Score (Odd & Even)** | `GradeManager` | `calculateMedianScore()` (Odd: middle; Even: mean of 2 middles) | Verified |
| **Handling Missing / Empty Students** | Both | `hasMarks()`, descriptive messages, returns `0.0` or `"N/A"` | Verified |
| **Formatted Report Card** | `GradeManager` | `generateReportCard()` with subjects, grades, standing | Verified |
| **Formatted All-Students Table** | `GradeManager` | `displayAllStudents()` with aligned table and summary footer | Verified |
| **Interactive Menu (Options 1–8)** | `Main` | `printMenu()`, `switch(choice)` | Verified |
| **Crash Prevention on Invalid Input** | `Main` | `readMenuChoice()`, `readDoubleInRange()`, `try/catch` | Verified |
| **Automated Verification Suite** | `SystemVerifier` | 39 automated unit & integration test cases | 39 / 39 PASSED |
