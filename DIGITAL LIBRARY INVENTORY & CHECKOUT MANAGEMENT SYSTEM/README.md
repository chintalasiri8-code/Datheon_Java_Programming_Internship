# Task 4: Digital Library Inventory & Checkout Management System

A beginner-friendly, console-based Java application developed to manage library books, member registrations, book loans, and automated return processing with overdue fine calculations.

---

## 1. Project Overview

The **Digital Library Inventory & Checkout Management System** simulates the core operations of a modern municipal or academic library. Built entirely in pure Java using Object-Oriented Programming (OOP) principles and the Java Collections Framework, it demonstrates clean class encapsulation, transaction tracking, and modern date/time arithmetic with `java.time.LocalDate`.

---

## 2. Technologies Used

- **Language:** Java 17+ (Compiled and tested on Java 25 LTS)
- **Paradigm:** Object-Oriented Programming (OOP)
- **Collections:** `java.util.HashMap`, `java.util.ArrayList`, `java.util.List`, `java.util.Map`
- **Date/Time API:** `java.time.LocalDate`, `java.time.format.DateTimeFormatter`, `java.time.temporal.ChronoUnit`
- **Input Handling:** `java.util.Scanner` (full-line reading to prevent newline skipping issues)
- **Build/Runtime:** Standard Java Development Kit (`javac` & `java`), no external dependencies, databases, or third-party frameworks.

---

## 3. Key Features Implemented

1. **Book Inventory Management:**
   - Add new books with Title, Author, and unique ISBN.
   - Prevent duplicate ISBN entries.
   - Enforce non-blank field validation.
   - Display all books in a formatted table with current availability status.
2. **Flexible Book Search:**
   - Case-insensitive substring search by **Title**.
   - Case-insensitive substring search by **Author**.
   - Exact/case-insensitive search by **ISBN**.
   - Formatted table display of matching search results.
3. **Library Member Management:**
   - Register library members with unique Member ID and full name.
   - Prevent duplicate Member IDs.
   - Display all registered members in a formatted table.
4. **Book Checkout Workflow:**
   - Verify book and member existence before loaning.
   - Prevent checking out books already on loan.
   - Automatically assign checkout date (`LocalDate.now()`).
   - Automatically calculate due date based on a fixed 14-day loan period (`checkoutDate.plusDays(14)`).
   - Mark book as unavailable and record active loan transaction.
5. **Book Return & Overdue Fine Calculation:**
   - Verify active loan status before accepting return.
   - Record return date.
   - Calculate overdue days using `ChronoUnit.DAYS.between(dueDate, returnDate)`.
   - Charge ₹5.00 per overdue day (`Math.max(0, overdueDays) * DAILY_FINE_RATE`).
   - On-time and early returns incur exactly ₹0.00 fine.
   - Generate detailed return receipt showing borrower, dates, overdue days, and fine amount.
   - Update book availability back to "Available" and remove active loan record.
6. **Continuous Console Menu & Error Handling:**
   - User-friendly 8-option menu running inside a `while` loop with a `switch` statement.
   - Safe input reading (prevents `InputMismatchException` and skipping bugs).
   - Clean application exit with automatic resource management (`try-with-resources`).

---

## 4. Java Source Files and Responsibilities

| File Name | Class | Responsibility |
|---|---|---|
| `Book.java` | `Book` | Entity model representing a book. Encapsulates `title`, `author`, `isbn`, and boolean `available` status with getters, setter, and formatted string representation. |
| `Member.java` | `Member` | Entity model representing a library member. Encapsulates `memberId` and `name` with getters and formatted display. |
| `Loan.java` | `Loan` | Transaction model representing an active loan. Associates an ISBN with a borrowing Member ID, `checkoutDate`, and `dueDate`. |
| `LibrarySystem.java` | `LibrarySystem` | Main controller class containing in-memory collections, inventory operations, checkout/return workflows, fine calculation logic, interactive console menu, and the `main()` method. |
| `LibrarySystemTest.java` | `LibrarySystemTest` | Automated test suite verifying 59 unit, integration, validation, and edge-case test assertions. |

---

## 5. System Constants & Business Rules

- **Loan Duration:** `14 days` (`LOAN_PERIOD_DAYS = 14`)
- **Daily Fine Rate:** `₹5.00 per day` (`DAILY_FINE_RATE = 5.0`)
- **Date Format:** `dd-MM-yyyy` (e.g., `23-10-2026`)
- **Overdue Formula:**
  $$\text{overdueDays} = \max(0, \text{days between due date and return date})$$
  $$\text{fine} = \text{overdueDays} \times \text{DAILY\_FINE\_RATE}$$

---

## 6. Compilation and Execution Instructions

### Prerequisites
- JDK 17 or later installed.
- Terminal / Command Prompt / PowerShell open in the project root directory:
  `d:\JAVA PROJECTS\DIGITAL LIBRARY INVENTORY & CHECKOUT MANAGEMENT SYSTEM`

### Step 1: Compile the Java Source Files
Run the following command to compile all project source files:
```bash
javac Book.java Member.java Loan.java LibrarySystem.java
```

*(Optional) To compile the automated test suite alongside the application:*
```bash
javac Book.java Member.java Loan.java LibrarySystem.java LibrarySystemTest.java
```

### Step 2: Run the Library Management Application
Run the compiled `LibrarySystem` class:
```bash
java LibrarySystem
```

### Step 3: Run the Automated Test Suite (Optional)
To run the automated verification suite:
```bash
java LibrarySystemTest
```

---

## 7. Sample Execution Workflow

### A. Adding a Book & Registering a Member
```text
Enter your choice (1-8): 1
--- Add New Book to Inventory ---
Enter Book Title : Clean Code
Enter Book Author: Robert C. Martin
Enter Book ISBN  : 978-0132350884

[SUCCESS] Book added successfully!
          Title : Clean Code
          Author: Robert C. Martin
          ISBN  : 978-0132350884
          Status: Available for checkout

Enter your choice (1-8): 4
--- Register New Library Member ---
Enter Member ID  : MEM001
Enter Member Name: Alice Johnson

[SUCCESS] Member registered successfully!
          Member ID: MEM001
          Name     : Alice Johnson
```

### B. Checking Out a Book
```text
Enter your choice (1-8): 6
--- Check Out a Book ---
Enter Book ISBN : 978-0132350884
Enter Member ID : MEM001

====================================================================
                     BOOK CHECKOUT CONFIRMED                        
====================================================================
 Book Title    : Clean Code
 ISBN          : 978-0132350884
 Borrower Name : Alice Johnson
 Member ID     : MEM001
 Checkout Date : 23-10-2026
 Due Date      : 06-11-2026 (14 days loan period)
====================================================================
[SUCCESS] The book has been checked out successfully.
```

### C. Returning a Book (On-Time Return: ₹0.00 Fine)
```text
Enter your choice (1-8): 7
--- Return a Book ---
Enter Book ISBN : 978-0132350884

====================================================================
                     BOOK RETURN RECEIPT                            
====================================================================
 Book Title    : Clean Code
 ISBN          : 978-0132350884
 Member ID     : MEM001 (Alice Johnson)
 Checkout Date : 23-10-2026
 Due Date      : 06-11-2026
 Return Date   : 23-10-2026
 Overdue Days  : 0 day(s)
 Late Fine Rate: ₹5.00 per day
 Total Fine Due: ₹0.00
--------------------------------------------------------------------
 Status        : Returned ON TIME. No fine charged.
====================================================================
[SUCCESS] The book has been successfully returned to inventory.
```

### D. Late Return Scenario (Simulated: 5 Days Overdue)
When a book due on `15-10-2026` is returned on `20-10-2026`:
```text
====================================================================
                     BOOK RETURN RECEIPT                            
====================================================================
 Book Title    : Design Patterns
 ISBN          : 978-0201633610
 Member ID     : MEM401 (Alan Turing)
 Checkout Date : 01-10-2026
 Due Date      : 15-10-2026
 Return Date   : 20-10-2026
 Overdue Days  : 5 day(s)
 Late Fine Rate: ₹5.00 per day
 Total Fine Due: ₹25.00
--------------------------------------------------------------------
 Status        : Returned OVERDUE. Please collect the late fee.
====================================================================
```

---

## 8. Known Scope and Limitations

1. **In-Memory Storage:** In accordance with the internship project scope, data is stored strictly in memory using `HashMap`. When the console application exits, any added books, members, or loan histories are cleared.
2. **Single Copy per ISBN:** Each ISBN represents a unique physical copy in the inventory.
3. **No Financial Payment Collection:** Late return fines are calculated and displayed on the return receipt, but monetary payment transactions are intentionally omitted to keep the project focused on core OOP and inventory requirements.
