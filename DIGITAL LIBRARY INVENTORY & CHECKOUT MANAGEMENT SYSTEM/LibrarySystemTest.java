import java.time.LocalDate;
import java.util.List;

/**
 * LibrarySystemTest.java
 *
 * Automated test suite to comprehensively verify all business logic,
 * error handling, edge cases, date arithmetic, and fine calculations for Task 4.
 */
public class LibrarySystemTest {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("      RUNNING DIGITAL LIBRARY SYSTEM AUTOMATED TEST SUITE   ");
        System.out.println("============================================================\n");

        testBookAddition();
        testBookAdditionValidation();
        testMemberRegistration();
        testMemberRegistrationValidation();
        testBookSearch();
        testCheckoutWorkflow();
        testCheckoutValidation();
        testReturnWorkflowOnTime();
        testReturnWorkflowOverdue();
        testReturnValidation();
        testFineAndOverdueCalculationLogic();

        System.out.println("\n============================================================");
        System.out.printf(" TEST SUMMARY: %d / %d PASSED (%.1f%%)\n",
                passedTests, totalTests, (totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0));
        System.out.println("============================================================");

        if (passedTests == totalTests) {
            System.out.println(">>> ALL AUTOMATED TESTS COMPLETED SUCCESSFULLY! <<<");
        } else {
            System.err.println(">>> SOME TESTS FAILED. PLEASE REVIEW LOGS. <<<");
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf(" [PASS] Test #%02d: %s\n", totalTests, testName);
        } else {
            System.err.printf(" [FAIL] Test #%02d: %s\n", totalTests, testName);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        totalTests++;
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            passedTests++;
            System.out.printf(" [PASS] Test #%02d: %s (Expected: %s, Actual: %s)\n", totalTests, testName, expected, actual);
        } else {
            System.err.printf(" [FAIL] Test #%02d: %s (Expected: %s, Actual: %s)\n", totalTests, testName, expected, actual);
        }
    }

    private static void testBookAddition() {
        System.out.println("\n--- 1. Testing Book Addition ---");
        LibrarySystem lib = new LibrarySystem();

        boolean added = lib.addBook("Clean Code", "Robert C. Martin", "978-0132350884");
        assertTrue("Add valid book to empty inventory", added);
        assertEquals("Inventory size should be 1", 1, lib.getBooks().size());

        Book book = lib.getBooks().get("978-0132350884");
        assertTrue("Book retrieved should not be null", book != null);
        assertEquals("Book title matches", "Clean Code", book.getTitle());
        assertEquals("Book author matches", "Robert C. Martin", book.getAuthor());
        assertTrue("New book is available by default", book.isAvailable());

        boolean duplicate = lib.addBook("Another Book", "Another Author", "978-0132350884");
        assertTrue("Adding duplicate ISBN must be rejected", !duplicate);
        assertEquals("Inventory size must still be 1 after duplicate attempt", 1, lib.getBooks().size());
    }

    private static void testBookAdditionValidation() {
        System.out.println("\n--- 2. Testing Book Validation (Empty/Blank Fields) ---");
        LibrarySystem lib = new LibrarySystem();

        assertTrue("Blank title rejected", !lib.addBook("", "Author", "ISBN1"));
        assertTrue("Blank author rejected", !lib.addBook("Title", "   ", "ISBN2"));
        assertTrue("Blank ISBN rejected", !lib.addBook("Title", "Author", ""));
        assertTrue("Null title rejected", !lib.addBook(null, "Author", "ISBN3"));
        assertEquals("Inventory remains 0 after all invalid add attempts", 0, lib.getBooks().size());
    }

    private static void testMemberRegistration() {
        System.out.println("\n--- 3. Testing Member Registration ---");
        LibrarySystem lib = new LibrarySystem();

        boolean reg1 = lib.registerMember("MEM001", "Alice Johnson");
        assertTrue("Register first member", reg1);
        assertEquals("Members size is 1", 1, lib.getMembers().size());

        Member m = lib.getMembers().get("MEM001");
        assertTrue("Member retrieved not null", m != null);
        assertEquals("Member ID matches", "MEM001", m.getMemberId());
        assertEquals("Member name matches", "Alice Johnson", m.getName());

        boolean dupMember = lib.registerMember("MEM001", "Bob Smith");
        assertTrue("Duplicate member ID must be rejected", !dupMember);
        assertEquals("Members count remains 1", 1, lib.getMembers().size());
    }

    private static void testMemberRegistrationValidation() {
        System.out.println("\n--- 4. Testing Member Validation (Empty/Blank Fields) ---");
        LibrarySystem lib = new LibrarySystem();

        assertTrue("Blank memberId rejected", !lib.registerMember("", "Alice"));
        assertTrue("Blank member name rejected", !lib.registerMember("MEM002", "   "));
        assertTrue("Null memberId rejected", !lib.registerMember(null, "Bob"));
        assertEquals("Members count remains 0 after invalid registrations", 0, lib.getMembers().size());
    }

    private static void testBookSearch() {
        System.out.println("\n--- 5. Testing Book Search ---");
        LibrarySystem lib = new LibrarySystem();
        lib.addBook("Effective Java", "Joshua Bloch", "978-0134685991");
        lib.addBook("Java Concurrency in Practice", "Brian Goetz", "978-0321349606");
        lib.addBook("Head First Java", "Kathy Sierra", "978-0596009205");

        // Search by Title (case-insensitive substring)
        List<Book> javaBooks = lib.searchBooksByTitle("java");
        assertEquals("Search title 'java' matches 3 books", 3, javaBooks.size());

        List<Book> concurrencyBooks = lib.searchBooksByTitle("concurrency");
        assertEquals("Search title 'concurrency' matches 1 book", 1, concurrencyBooks.size());

        List<Book> noBooks = lib.searchBooksByTitle("Python");
        assertEquals("Search title 'Python' matches 0 books", 0, noBooks.size());

        // Search by Author (case-insensitive substring)
        List<Book> blochBooks = lib.searchBooksByAuthor("bloch");
        assertEquals("Search author 'bloch' matches 1 book", 1, blochBooks.size());

        // Search by ISBN
        Book foundByIsbn = lib.searchBooksByIsbn("978-0596009205");
        assertTrue("Found book by exact ISBN", foundByIsbn != null && "Head First Java".equals(foundByIsbn.getTitle()));

        Book notFoundIsbn = lib.searchBooksByIsbn("999-9999999999");
        assertTrue("Nonexistent ISBN returns null", notFoundIsbn == null);
    }

    private static void testCheckoutWorkflow() {
        System.out.println("\n--- 6. Testing Checkout Workflow ---");
        LibrarySystem lib = new LibrarySystem();
        lib.addBook("Clean Code", "Robert C. Martin", "978-0132350884");
        lib.registerMember("MEM101", "David Miller");

        LocalDate today = LocalDate.of(2026, 10, 23);
        boolean checkoutSuccess = lib.checkoutBook("978-0132350884", "MEM101", today);
        assertTrue("Checkout succeeds for valid book and member", checkoutSuccess);

        Book book = lib.getBooks().get("978-0132350884");
        assertTrue("Book is marked as unavailable after checkout", !book.isAvailable());

        Loan loan = lib.getLoans().get("978-0132350884");
        assertTrue("Active loan record created", loan != null);
        assertEquals("Loan borrower ID matches", "MEM101", loan.getMemberId());
        assertEquals("Loan checkout date matches", today, loan.getCheckoutDate());
        assertEquals("Loan due date is checkout date + 14 days", today.plusDays(14), loan.getDueDate());
    }

    private static void testCheckoutValidation() {
        System.out.println("\n--- 7. Testing Checkout Validation & Failure Cases ---");
        LibrarySystem lib = new LibrarySystem();
        lib.addBook("Book One", "Author A", "ISBN-111");
        lib.registerMember("MEM201", "Member One");
        lib.registerMember("MEM202", "Member Two");

        // Checkout once
        lib.checkoutBook("ISBN-111", "MEM201");

        // Attempt checkout for already checked out book
        boolean secondCheckout = lib.checkoutBook("ISBN-111", "MEM202");
        assertTrue("Checking out already checked-out book must fail", !secondCheckout);

        // Attempt checkout with nonexistent book
        boolean nonexistentBook = lib.checkoutBook("ISBN-999", "MEM201");
        assertTrue("Checkout with nonexistent book must fail", !nonexistentBook);

        // Attempt checkout with nonexistent member
        lib.addBook("Book Two", "Author B", "ISBN-222");
        boolean nonexistentMember = lib.checkoutBook("ISBN-222", "MEM999");
        assertTrue("Checkout with nonexistent member must fail", !nonexistentMember);
        assertTrue("Book Two must remain available after failed checkout", lib.getBooks().get("ISBN-222").isAvailable());
    }

    private static void testReturnWorkflowOnTime() {
        System.out.println("\n--- 8. Testing Return Workflow (On-Time / Early Return) ---");
        LibrarySystem lib = new LibrarySystem();
        lib.addBook("Refactoring", "Martin Fowler", "978-0201485677");
        lib.registerMember("MEM301", "Grace Hopper");

        LocalDate checkoutDate = LocalDate.of(2026, 10, 1);
        lib.checkoutBook("978-0201485677", "MEM301", checkoutDate);

        // Due date is 2026-10-15 (14 days later). Returning on 2026-10-10 (5 days early)
        LocalDate earlyReturnDate = LocalDate.of(2026, 10, 10);
        boolean earlyReturnSuccess = lib.returnBook("978-0201485677", earlyReturnDate);

        assertTrue("Early return succeeds", earlyReturnSuccess);
        assertTrue("Book is available again", lib.getBooks().get("978-0201485677").isAvailable());
        assertTrue("Active loan record removed", !lib.getLoans().containsKey("978-0201485677"));

        // Test exact due date return
        lib.checkoutBook("978-0201485677", "MEM301", checkoutDate);
        LocalDate onTimeReturnDate = LocalDate.of(2026, 10, 15);
        boolean onTimeReturnSuccess = lib.returnBook("978-0201485677", onTimeReturnDate);
        assertTrue("On-time return (exact due date) succeeds with 0 fine", onTimeReturnSuccess);
        assertTrue("Book is available again", lib.getBooks().get("978-0201485677").isAvailable());
    }

    private static void testReturnWorkflowOverdue() {
        System.out.println("\n--- 9. Testing Return Workflow (Late / Overdue Return & Fine) ---");
        LibrarySystem lib = new LibrarySystem();
        lib.addBook("Design Patterns", "Gang of Four", "978-0201633610");
        lib.registerMember("MEM401", "Alan Turing");

        LocalDate checkoutDate = LocalDate.of(2026, 10, 1);
        lib.checkoutBook("978-0201633610", "MEM401", checkoutDate);
        // Due date: 2026-10-15. Returning on 2026-10-20 (5 days late).
        LocalDate lateReturnDate = LocalDate.of(2026, 10, 20);

        boolean returnSuccess = lib.returnBook("978-0201633610", lateReturnDate);
        assertTrue("Late return succeeds and settles transaction", returnSuccess);
        assertTrue("Book status restored to available", lib.getBooks().get("978-0201633610").isAvailable());
        assertTrue("Loan record cleaned up after return", !lib.getLoans().containsKey("978-0201633610"));
    }

    private static void testReturnValidation() {
        System.out.println("\n--- 10. Testing Return Validation & Failure Cases ---");
        LibrarySystem lib = new LibrarySystem();
        lib.addBook("The Pragmatic Programmer", "Andy Hunt", "978-0201616224");

        // Try to return a book that is in inventory but never checked out
        boolean returnAvailableBook = lib.returnBook("978-0201616224");
        assertTrue("Returning a book that is already available must fail", !returnAvailableBook);

        // Try to return a nonexistent book
        boolean returnNonexistent = lib.returnBook("ISBN-NOT-FOUND");
        assertTrue("Returning nonexistent book must fail", !returnNonexistent);

        // Try blank ISBN
        boolean returnBlank = lib.returnBook("   ");
        assertTrue("Returning with blank ISBN must fail", !returnBlank);
    }

    private static void testFineAndOverdueCalculationLogic() {
        System.out.println("\n--- 11. Testing Date & Fine Calculation Logic Directly ---");

        LocalDate dueDate = LocalDate.of(2026, 11, 10);

        // Case A: 5 days before due date
        LocalDate earlyDate = LocalDate.of(2026, 11, 5);
        long overdueA = LibrarySystem.calculateOverdueDays(dueDate, earlyDate);
        double fineA = LibrarySystem.calculateFine(overdueA);
        assertEquals("Early return overdue days is 0", 0L, overdueA);
        assertEquals("Early return fine is 0.0", 0.0, fineA);

        // Case B: Exactly on due date
        LocalDate exactDueDate = LocalDate.of(2026, 11, 10);
        long overdueB = LibrarySystem.calculateOverdueDays(dueDate, exactDueDate);
        double fineB = LibrarySystem.calculateFine(overdueB);
        assertEquals("Exact due date overdue days is 0", 0L, overdueB);
        assertEquals("Exact due date fine is 0.0", 0.0, fineB);

        // Case C: 3 days overdue -> 3 * 5 = ₹15.00
        LocalDate lateDate3 = LocalDate.of(2026, 11, 13);
        long overdueC = LibrarySystem.calculateOverdueDays(dueDate, lateDate3);
        double fineC = LibrarySystem.calculateFine(overdueC);
        assertEquals("3 days late overdue days is 3", 3L, overdueC);
        assertEquals("3 days late fine is 15.0", 15.0, fineC);

        // Case D: 10 days overdue -> 10 * 5 = ₹50.00
        LocalDate lateDate10 = LocalDate.of(2026, 11, 20);
        long overdueD = LibrarySystem.calculateOverdueDays(dueDate, lateDate10);
        double fineD = LibrarySystem.calculateFine(overdueD);
        assertEquals("10 days late overdue days is 10", 10L, overdueD);
        assertEquals("10 days late fine is 50.0", 50.0, fineD);
    }
}
