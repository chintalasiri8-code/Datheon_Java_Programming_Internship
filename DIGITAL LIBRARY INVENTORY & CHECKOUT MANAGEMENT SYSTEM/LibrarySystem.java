import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * LibrarySystem.java
 *
 * Main entry point and core controller for the Digital Library Inventory & Checkout Management System.
 *
 * This class provides:
 * 1. Book inventory management (Add, Display, Search).
 * 2. Member management (Register, Display).
 * 3. Book checkout workflow with automatic due-date assignment.
 * 4. Book return workflow with automatic overdue days and late-fine calculations.
 * 5. Interactive console menu driven by a while-loop and switch statement.
 * 6. Robust input validation and error handling.
 *
 * Key Concepts Demonstrated:
 * - Object-Oriented Programming (OOP) principles.
 * - Java Collections Framework: HashMap<String, Book>, HashMap<String, Member>, HashMap<String, Loan>.
 * - Java Date and Time API: java.time.LocalDate, plusDays(), ChronoUnit.DAYS.between().
 * - Safe scanner input reading (reading full lines to avoid newline consumption bugs).
 */
public class LibrarySystem {

    // ==========================================
    // System Constants
    // ==========================================

    /** Default loan duration in days */
    public static final int LOAN_PERIOD_DAYS = 14;

    /** Daily fine rate for overdue books in Indian Rupees (₹) */
    public static final double DAILY_FINE_RATE = 5.0;

    /** Standard date format pattern for display */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ==========================================
    // Data Storage Collections (In-Memory)
    // ==========================================

    /** Stores books using unique ISBN as the key */
    private final Map<String, Book> books = new HashMap<>();

    /** Stores members using unique Member ID as the key */
    private final Map<String, Member> members = new HashMap<>();

    /** Stores active loan records using book ISBN as the key */
    private final Map<String, Loan> loans = new HashMap<>();

    // ==========================================
    // Collection Getters (Useful for Inspection/Testing)
    // ==========================================

    public Map<String, Book> getBooks() {
        return books;
    }

    public Map<String, Member> getMembers() {
        return members;
    }

    public Map<String, Loan> getLoans() {
        return loans;
    }

    // ==========================================
    // 1. Book Inventory Management Methods
    // ==========================================

    /**
     * Adds a new book to the library inventory.
     *
     * @param title  The title of the book
     * @param author The author of the book
     * @param isbn   The unique ISBN of the book
     * @return true if the book was successfully added; false otherwise
     */
    public boolean addBook(String title, String author, String isbn) {
        // Validate that fields are not null or blank
        if (title == null || title.trim().isEmpty() ||
            author == null || author.trim().isEmpty() ||
            isbn == null || isbn.trim().isEmpty()) {
            System.out.println("\n[ERROR] All fields (Title, Author, ISBN) are required and cannot be blank.");
            return false;
        }

        String cleanTitle = title.trim();
        String cleanAuthor = author.trim();
        String cleanIsbn = isbn.trim();

        // Check for duplicate ISBN
        if (books.containsKey(cleanIsbn)) {
            System.out.println("\n[ERROR] A book with ISBN '" + cleanIsbn + "' already exists in the inventory.");
            System.out.println("        Duplicate ISBNs are not allowed.");
            return false;
        }

        // Create new book (marked available by default) and store in HashMap
        Book newBook = new Book(cleanTitle, cleanAuthor, cleanIsbn);
        books.put(cleanIsbn, newBook);

        System.out.println("\n[SUCCESS] Book added successfully!");
        System.out.println("          Title : " + cleanTitle);
        System.out.println("          Author: " + cleanAuthor);
        System.out.println("          ISBN  : " + cleanIsbn);
        System.out.println("          Status: Available for checkout");
        return true;
    }

    /**
     * Displays all books currently in the library inventory in a formatted table.
     */
    public void displayAllBooks() {
        System.out.println("\n==========================================================================================");
        System.out.println("                               LIBRARY BOOK INVENTORY                                     ");
        System.out.println("==========================================================================================");

        if (books.isEmpty()) {
            System.out.println("No books are currently registered in the inventory.");
            System.out.println("Use Option 1 from the main menu to add your first book.");
            System.out.println("==========================================================================================");
            return;
        }

        printBookTableHeader();
        for (Book book : books.values()) {
            printBookTableRow(book);
        }
        printBookTableDivider();
        System.out.println("Total Books in Inventory: " + books.size());
    }

    /**
     * Searches books by title, author, or ISBN.
     *
     * @param scanner Scanner instance to read user search input
     */
    public void searchBooks(Scanner scanner) {
        boolean searching = true;
        while (searching) {
            System.out.println("\n--------------------------------------------------");
            System.out.println("                  SEARCH BOOKS                    ");
            System.out.println("--------------------------------------------------");
            System.out.println("1. Search by Title");
            System.out.println("2. Search by Author");
            System.out.println("3. Search by ISBN");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter search option (1-4): ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    System.out.print("Enter book title or keyword to search: ");
                    String titleQuery = scanner.nextLine().trim();
                    if (titleQuery.isEmpty()) {
                        System.out.println("[ERROR] Search term cannot be empty.");
                        break;
                    }
                    searchBooksByTitle(titleQuery);
                    break;

                case "2":
                    System.out.print("Enter author name or keyword to search: ");
                    String authorQuery = scanner.nextLine().trim();
                    if (authorQuery.isEmpty()) {
                        System.out.println("[ERROR] Search term cannot be empty.");
                        break;
                    }
                    searchBooksByAuthor(authorQuery);
                    break;

                case "3":
                    System.out.print("Enter exact ISBN to search: ");
                    String isbnQuery = scanner.nextLine().trim();
                    if (isbnQuery.isEmpty()) {
                        System.out.println("[ERROR] Search term cannot be empty.");
                        break;
                    }
                    searchBooksByIsbn(isbnQuery);
                    break;

                case "4":
                    searching = false;
                    System.out.println("Returning to Main Menu...");
                    break;

                default:
                    System.out.println("[ERROR] Invalid search option. Please enter 1, 2, 3, or 4.");
                    break;
            }
        }
    }

    /**
     * Searches books by title using case-insensitive substring matching.
     *
     * @param titleQuery Search keyword
     */
    public List<Book> searchBooksByTitle(String titleQuery) {
        List<Book> results = new ArrayList<>();
        String queryLower = titleQuery.toLowerCase();

        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(queryLower)) {
                results.add(book);
            }
        }

        displaySearchResults("Title", titleQuery, results);
        return results;
    }

    /**
     * Searches books by author using case-insensitive substring matching.
     *
     * @param authorQuery Search keyword
     */
    public List<Book> searchBooksByAuthor(String authorQuery) {
        List<Book> results = new ArrayList<>();
        String queryLower = authorQuery.toLowerCase();

        for (Book book : books.values()) {
            if (book.getAuthor().toLowerCase().contains(queryLower)) {
                results.add(book);
            }
        }

        displaySearchResults("Author", authorQuery, results);
        return results;
    }

    /**
     * Searches for a book by its exact ISBN.
     *
     * @param isbnQuery The ISBN to find
     */
    public Book searchBooksByIsbn(String isbnQuery) {
        Book book = books.get(isbnQuery.trim());
        List<Book> results = new ArrayList<>();
        if (book != null) {
            results.add(book);
        }

        displaySearchResults("ISBN", isbnQuery, results);
        return book;
    }

    /**
     * Displays a list of search result books in a formatted table.
     */
    private void displaySearchResults(String criteria, String query, List<Book> results) {
        System.out.println("\n==========================================================================================");
        System.out.println(" Search Results for " + criteria + ": \"" + query + "\" (Found: " + results.size() + " matches)");
        System.out.println("==========================================================================================");

        if (results.isEmpty()) {
            System.out.println("No books found matching the search criteria.");
            System.out.println("==========================================================================================");
            return;
        }

        printBookTableHeader();
        for (Book book : results) {
            printBookTableRow(book);
        }
        printBookTableDivider();
    }

    // ==========================================
    // 2. Member Management Methods
    // ==========================================

    /**
     * Registers a new library member.
     *
     * @param memberId Unique member identifier
     * @param name     Full name of member
     * @return true if successfully registered; false otherwise
     */
    public boolean registerMember(String memberId, String name) {
        // Validate non-blank input
        if (memberId == null || memberId.trim().isEmpty() ||
            name == null || name.trim().isEmpty()) {
            System.out.println("\n[ERROR] Both Member ID and Member Name are required and cannot be blank.");
            return false;
        }

        String cleanMemberId = memberId.trim();
        String cleanName = name.trim();

        // Check for duplicate Member ID
        if (members.containsKey(cleanMemberId)) {
            System.out.println("\n[ERROR] A member with ID '" + cleanMemberId + "' is already registered.");
            System.out.println("        Duplicate Member IDs are not allowed.");
            return false;
        }

        // Register member into HashMap
        Member member = new Member(cleanMemberId, cleanName);
        members.put(cleanMemberId, member);

        System.out.println("\n[SUCCESS] Member registered successfully!");
        System.out.println("          Member ID: " + cleanMemberId);
        System.out.println("          Name     : " + cleanName);
        return true;
    }

    /**
     * Displays all registered members in a formatted table.
     */
    public void displayAllMembers() {
        System.out.println("\n====================================================================");
        System.out.println("                     REGISTERED LIBRARY MEMBERS                     ");
        System.out.println("====================================================================");

        if (members.isEmpty()) {
            System.out.println("No members are currently registered in the system.");
            System.out.println("Use Option 4 from the main menu to register a member.");
            System.out.println("====================================================================");
            return;
        }

        System.out.println("+---------------+--------------------------------------------------+");
        System.out.printf("| %-13s | %-48s |\n", "Member ID", "Member Name");
        System.out.println("+---------------+--------------------------------------------------+");

        for (Member member : members.values()) {
            System.out.printf("| %-13s | %-48s |\n", member.getMemberId(), member.getName());
        }

        System.out.println("+---------------+--------------------------------------------------+");
        System.out.println("Total Registered Members: " + members.size());
    }

    // ==========================================
    // 3. Book Checkout Workflow
    // ==========================================

    /**
     * Checks out a book to a registered library member using current date.
     *
     * @param isbn     ISBN of the book
     * @param memberId ID of the borrower
     * @return true if checkout succeeded; false otherwise
     */
    public boolean checkoutBook(String isbn, String memberId) {
        return checkoutBook(isbn, memberId, LocalDate.now());
    }

    /**
     * Overloaded checkoutBook method allowing a custom checkout date (useful for testing).
     *
     * @param isbn         ISBN of the book
     * @param memberId     ID of the borrower
     * @param checkoutDate The date on which checkout occurs
     * @return true if checkout succeeded; false otherwise
     */
    public boolean checkoutBook(String isbn, String memberId, LocalDate checkoutDate) {
        // Validation: blank inputs
        if (isbn == null || isbn.trim().isEmpty() ||
            memberId == null || memberId.trim().isEmpty()) {
            System.out.println("\n[ERROR] Both ISBN and Member ID are required for checkout.");
            return false;
        }

        String cleanIsbn = isbn.trim();
        String cleanMemberId = memberId.trim();

        // 1. Check whether book exists
        Book book = books.get(cleanIsbn);
        if (book == null) {
            System.out.println("\n[ERROR] Checkout Failed: Book with ISBN '" + cleanIsbn + "' was not found in the inventory.");
            return false;
        }

        // 2. Check whether member exists
        Member member = members.get(cleanMemberId);
        if (member == null) {
            System.out.println("\n[ERROR] Checkout Failed: Member with ID '" + cleanMemberId + "' is not registered.");
            System.out.println("        Please register the member first (Option 4).");
            return false;
        }

        // 3. Check whether book is available
        if (!book.isAvailable()) {
            Loan activeLoan = loans.get(cleanIsbn);
            String borrowerInfo = (activeLoan != null) ? (" (Loaned to Member ID: " + activeLoan.getMemberId() + ")") : "";
            System.out.println("\n[ERROR] Checkout Failed: Book '" + book.getTitle() + "' is currently checked out." + borrowerInfo);
            return false;
        }

        // 4. Calculate due date
        LocalDate dueDate = checkoutDate.plusDays(LOAN_PERIOD_DAYS);

        // 5. Update book availability
        book.setAvailable(false);

        // 6. Record loan transaction
        Loan loan = new Loan(cleanIsbn, cleanMemberId, checkoutDate, dueDate);
        loans.put(cleanIsbn, loan);

        // 7. Display checkout confirmation
        System.out.println("\n====================================================================");
        System.out.println("                     BOOK CHECKOUT CONFIRMED                        ");
        System.out.println("====================================================================");
        System.out.printf(" Book Title    : %s\n", book.getTitle());
        System.out.printf(" ISBN          : %s\n", book.getIsbn());
        System.out.printf(" Borrower Name : %s\n", member.getName());
        System.out.printf(" Member ID     : %s\n", member.getMemberId());
        System.out.printf(" Checkout Date : %s\n", checkoutDate.format(DATE_FORMATTER));
        System.out.printf(" Due Date      : %s (%d days loan period)\n", dueDate.format(DATE_FORMATTER), LOAN_PERIOD_DAYS);
        System.out.println("====================================================================");
        System.out.println("[SUCCESS] The book has been checked out successfully.");
        return true;
    }

    // ==========================================
    // 4. Book Return & Fine Calculation Workflow
    // ==========================================

    /**
     * Returns a checked-out book using the current date.
     *
     * @param isbn ISBN of the book to return
     * @return true if return succeeded; false otherwise
     */
    public boolean returnBook(String isbn) {
        return returnBook(isbn, LocalDate.now());
    }

    /**
     * Overloaded returnBook method allowing a custom return date (ideal for testing late returns).
     *
     * @param isbn       ISBN of the book to return
     * @param returnDate The date on which the return occurs
     * @return true if return succeeded; false otherwise
     */
    public boolean returnBook(String isbn, LocalDate returnDate) {
        // Validation: blank input
        if (isbn == null || isbn.trim().isEmpty()) {
            System.out.println("\n[ERROR] ISBN cannot be blank.");
            return false;
        }

        String cleanIsbn = isbn.trim();

        // 1. Verify book exists
        Book book = books.get(cleanIsbn);
        if (book == null) {
            System.out.println("\n[ERROR] Return Failed: Book with ISBN '" + cleanIsbn + "' does not exist in the inventory.");
            return false;
        }

        // 2. Verify book has an active loan
        Loan loan = loans.get(cleanIsbn);
        if (loan == null || book.isAvailable()) {
            System.out.println("\n[ERROR] Return Failed: Book '" + book.getTitle() + "' is not currently checked out.");
            System.out.println("        It is already available in the library inventory.");
            return false;
        }

        // 3. Calculate overdue days and fine
        long overdueDays = calculateOverdueDays(loan.getDueDate(), returnDate);
        double fine = calculateFine(overdueDays);

        // 4. Mark book as available again
        book.setAvailable(true);

        // 5. Remove active loan record
        loans.remove(cleanIsbn);

        // 6. Display return confirmation receipt
        Member member = members.get(loan.getMemberId());
        String memberName = (member != null) ? member.getName() : "Unknown Member";

        System.out.println("\n====================================================================");
        System.out.println("                     BOOK RETURN RECEIPT                            ");
        System.out.println("====================================================================");
        System.out.printf(" Book Title    : %s\n", book.getTitle());
        System.out.printf(" ISBN          : %s\n", book.getIsbn());
        System.out.printf(" Member ID     : %s (%s)\n", loan.getMemberId(), memberName);
        System.out.printf(" Checkout Date : %s\n", loan.getFormattedCheckoutDate());
        System.out.printf(" Due Date      : %s\n", loan.getFormattedDueDate());
        System.out.printf(" Return Date   : %s\n", returnDate.format(DATE_FORMATTER));
        System.out.printf(" Overdue Days  : %d day(s)\n", overdueDays);
        System.out.printf(" Late Fine Rate: ₹%.2f per day\n", DAILY_FINE_RATE);
        System.out.printf(" Total Fine Due: ₹%.2f\n", fine);
        System.out.println("--------------------------------------------------------------------");
        if (overdueDays == 0) {
            System.out.println(" Status        : Returned ON TIME. No fine charged.");
        } else {
            System.out.println(" Status        : Returned OVERDUE. Please collect the late fee.");
        }
        System.out.println("====================================================================");
        System.out.println("[SUCCESS] The book has been successfully returned to inventory.");
        return true;
    }

    // ==========================================
    // 5. Overdue and Fine Calculation Helpers
    // ==========================================

    /**
     * Calculates the number of overdue days between due date and return date.
     * Uses ChronoUnit.DAYS.between(dueDate, returnDate).
     *
     * Rule:
     * - If returnDate is on or before dueDate, overdue days is 0.
     * - If returnDate is after dueDate, returns the positive difference in days.
     *
     * @param dueDate    The scheduled return date
     * @param returnDate The actual date the book was returned
     * @return Number of overdue days (0 if returned on or before due date)
     */
    public static long calculateOverdueDays(LocalDate dueDate, LocalDate returnDate) {
        long daysBetween = ChronoUnit.DAYS.between(dueDate, returnDate);
        return Math.max(0, daysBetween);
    }

    /**
     * Calculates the late fine amount based on overdue days and daily fine rate.
     *
     * @param overdueDays Total number of days overdue
     * @return Fine amount in Indian Rupees (₹)
     */
    public static double calculateFine(long overdueDays) {
        return overdueDays * DAILY_FINE_RATE;
    }

    // ==========================================
    // 6. Table Formatting Helpers
    // ==========================================

    private static void printBookTableHeader() {
        printBookTableDivider();
        System.out.printf("| %-15s | %-32s | %-22s | %-12s |\n", "ISBN", "Title", "Author", "Status");
        printBookTableDivider();
    }

    private static void printBookTableRow(Book book) {
        String status = book.isAvailable() ? "Available" : "Checked Out";
        // Truncate title or author if excessively long to preserve clean table layout
        String title = truncate(book.getTitle(), 32);
        String author = truncate(book.getAuthor(), 22);
        System.out.printf("| %-15s | %-32s | %-22s | %-12s |\n", book.getIsbn(), title, author, status);
    }

    private static void printBookTableDivider() {
        System.out.println("+-----------------+----------------------------------+------------------------+--------------+");
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    // ==========================================
    // 7. Interactive Console Menu Loop
    // ==========================================

    /**
     * Runs the continuous console menu until the user selects Exit.
     *
     * @param scanner Scanner for user console input
     */
    public void runMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n============================================================");
            System.out.println("   DIGITAL LIBRARY INVENTORY & CHECKOUT MANAGEMENT SYSTEM   ");
            System.out.println("============================================================");
            System.out.println(" 1. Add Book");
            System.out.println(" 2. Display All Books");
            System.out.println(" 3. Search Books");
            System.out.println(" 4. Register Member");
            System.out.println(" 5. Display All Members");
            System.out.println(" 6. Check Out Book");
            System.out.println(" 7. Return Book");
            System.out.println(" 8. Exit");
            System.out.println("============================================================");
            System.out.print("Enter your choice (1-8): ");

            // Read complete line safely to prevent Scanner newline issues
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleMenuAddBook(scanner);
                    break;

                case "2":
                    displayAllBooks();
                    break;

                case "3":
                    searchBooks(scanner);
                    break;

                case "4":
                    handleMenuRegisterMember(scanner);
                    break;

                case "5":
                    displayAllMembers();
                    break;

                case "6":
                    handleMenuCheckoutBook(scanner);
                    break;

                case "7":
                    handleMenuReturnBook(scanner);
                    break;

                case "8":
                    running = false;
                    System.out.println("\nThank you for using the Digital Library Management System. Goodbye!");
                    break;

                default:
                    System.out.println("\n[ERROR] Invalid choice '" + choice + "'! Please enter a number between 1 and 8.");
                    break;
            }
        }
    }

    private void handleMenuAddBook(Scanner scanner) {
        System.out.println("\n--- Add New Book to Inventory ---");
        System.out.print("Enter Book Title : ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Book Author: ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter Book ISBN  : ");
        String isbn = scanner.nextLine().trim();

        addBook(title, author, isbn);
    }

    private void handleMenuRegisterMember(Scanner scanner) {
        System.out.println("\n--- Register New Library Member ---");
        System.out.print("Enter Member ID  : ");
        String memberId = scanner.nextLine().trim();

        System.out.print("Enter Member Name: ");
        String name = scanner.nextLine().trim();

        registerMember(memberId, name);
    }

    private void handleMenuCheckoutBook(Scanner scanner) {
        System.out.println("\n--- Check Out a Book ---");
        System.out.print("Enter Book ISBN : ");
        String isbn = scanner.nextLine().trim();

        System.out.print("Enter Member ID : ");
        String memberId = scanner.nextLine().trim();

        checkoutBook(isbn, memberId);
    }

    private void handleMenuReturnBook(Scanner scanner) {
        System.out.println("\n--- Return a Book ---");
        System.out.print("Enter Book ISBN : ");
        String isbn = scanner.nextLine().trim();

        returnBook(isbn);
    }

    // ==========================================
    // 8. Main Entry Method
    // ==========================================

    /**
     * Application entry point.
     * Initializes the system and starts the console menu with resource cleanup.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        LibrarySystem library = new LibrarySystem();

        // Use try-with-resources to ensure Scanner is closed properly upon exit
        try (Scanner scanner = new Scanner(System.in)) {
            library.runMenu(scanner);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
