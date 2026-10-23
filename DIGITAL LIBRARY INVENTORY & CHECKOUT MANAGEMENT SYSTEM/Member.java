/**
 * Member.java
 *
 * Represents a registered library member in the Digital Library System.
 * Encapsulates the member's unique ID and full name.
 *
 * Key Concepts Demonstrated:
 * - Encapsulation: Keeping member data private and providing controlled access via getters.
 * - Clean entity modeling for library patrons.
 */
public class Member {
    // Private instance variables for encapsulation
    private String memberId;
    private String name;

    /**
     * Constructs a new Member instance.
     *
     * @param memberId The unique ID of the member (e.g., "MEM001")
     * @param name     The full name of the member (e.g., "Alice Johnson")
     */
    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }

    // ==========================================
    // Getter Methods
    // ==========================================

    /**
     * Gets the unique member identifier.
     *
     * @return The member ID string
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Gets the member's full name.
     *
     * @return The name string
     */
    public String getName() {
        return name;
    }

    // ==========================================
    // Readable Display Method
    // ==========================================

    /**
     * Returns a clear and formatted string representation of the member's details.
     *
     * @return Formatted string with Member ID and Name
     */
    @Override
    public String toString() {
        return String.format("Member ID: %-12s | Name: %s", memberId, name);
    }
}
