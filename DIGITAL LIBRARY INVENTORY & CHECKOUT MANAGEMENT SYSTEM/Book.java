/**
 * Book.java
 *
 * Represents a book in the Digital Library Inventory.
 * Encapsulates the book's title, author, unique ISBN, and availability status.
 *
 * Key Concepts Demonstrated:
 * - Object-Oriented Programming (OOP): Encapsulation with private fields and public getters/setters.
 * - Constructor initialization: Ensuring each new book starts with a clean, known state (available = true).
 */
public class Book {
    // Private instance variables for encapsulation
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    /**
     * Constructs a new Book instance.
     * When a book is initially added to the library, it is marked as available by default.
     *
     * @param title  The title of the book
     * @param author The author of the book
     * @param isbn   The unique International Standard Book Number (ISBN)
     */
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = true; // Initially available when added to inventory
    }

    // ==========================================
    // Getter Methods
    // ==========================================

    /**
     * Gets the title of the book.
     *
     * @return The title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the author of the book.
     *
     * @return The author's name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the unique ISBN identifier of the book.
     *
     * @return The ISBN string
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Checks whether the book is currently available for checkout.
     *
     * @return true if available, false if currently checked out
     */
    public boolean isAvailable() {
        return available;
    }

    // ==========================================
    // Setter / Update Method
    // ==========================================

    /**
     * Updates the checkout availability status of the book.
     *
     * @param available true to mark as available, false to mark as checked out
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // ==========================================
    // Readable Display Method
    // ==========================================

    /**
     * Returns a clear and formatted string representation of the book's details.
     *
     * @return Formatted string with ISBN, title, author, and status
     */
    @Override
    public String toString() {
        String status = available ? "Available" : "Checked Out";
        return String.format("ISBN: %-15s | Title: %-30s | Author: %-20s | Status: %s",
                isbn, title, author, status);
    }
}
