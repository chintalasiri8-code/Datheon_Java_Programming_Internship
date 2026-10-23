import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Loan.java
 *
 * Represents an active book loan transaction.
 * Associates a borrowed book (by ISBN) with a borrowing member (by Member ID),
 * recording both the checkout date and the calculated due date.
 *
 * Key Concepts Demonstrated:
 * - Domain modeling: Separating the loan record from the book and member entities.
 * - Modern Java Date and Time API: Using java.time.LocalDate for immutable calendar dates.
 */
public class Loan {
    // Standard date format used for readable display across the library application
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Private fields
    private String isbn;
    private String memberId;
    private LocalDate checkoutDate;
    private LocalDate dueDate;

    /**
     * Constructs a new Loan transaction record.
     *
     * @param isbn         The unique ISBN of the borrowed book
     * @param memberId     The unique ID of the member who borrowed the book
     * @param checkoutDate The date on which the book was checked out
     * @param dueDate      The scheduled due date for returning the book
     */
    public Loan(String isbn, String memberId, LocalDate checkoutDate, LocalDate dueDate) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
    }

    // ==========================================
    // Getter Methods
    // ==========================================

    /**
     * Gets the ISBN of the checked-out book.
     *
     * @return The ISBN string
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Gets the Member ID of the borrower.
     *
     * @return The member ID string
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Gets the checkout date.
     *
     * @return The LocalDate of checkout
     */
    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    /**
     * Gets the due date for return.
     *
     * @return The LocalDate of due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Helper method to get the formatted checkout date string (dd-MM-yyyy).
     *
     * @return Formatted checkout date string
     */
    public String getFormattedCheckoutDate() {
        return checkoutDate.format(DATE_FORMATTER);
    }

    /**
     * Helper method to get the formatted due date string (dd-MM-yyyy).
     *
     * @return Formatted due date string
     */
    public String getFormattedDueDate() {
        return dueDate.format(DATE_FORMATTER);
    }

    // ==========================================
    // Readable Display Method
    // ==========================================

    @Override
    public String toString() {
        return String.format("ISBN: %-15s | Member ID: %-10s | Checkout: %s | Due: %s",
                isbn, memberId, getFormattedCheckoutDate(), getFormattedDueDate());
    }
}
