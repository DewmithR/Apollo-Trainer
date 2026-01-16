package db;

import model.Member;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    /**
     * Helper method to generate the next MemberID (e.g., M0001, M0002)
     */
    private String generateNextMemberID(Connection conn) throws SQLException {
        String sql = "SELECT TOP 1 MemberID FROM Member ORDER BY MemberID DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("MemberID");
                // Expects format M#### (e.g., M0001)
                int number = Integer.parseInt(lastId.substring(1));
                return String.format("M%04d", number + 1);
            } else {
                return "M0001"; // First member
            }
        }
    }

    /**
     * C - Create: Adds a new member to the database.
     * @param member The Member object containing data.
     * @return true if successful, false otherwise.
     */
    public boolean addMember(Member member) {
        // SQL will handle generating the next MemberID automatically.
        String sql = "INSERT INTO Member (MemberID, FirstName, LastName, ContactNumber, Email, DateOfBirth, JoiningDate, Address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Generate ID before insertion
            String newId = generateNextMemberID(conn);
            member.setMemberId(newId); // Update the model object with the new ID

            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getFirstName());
            pstmt.setString(3, member.getLastName());
            pstmt.setString(4, member.getContactNumber());
            pstmt.setString(5, member.getEmail());
            pstmt.setDate(6, Date.valueOf(member.getDateOfBirth())); // Convert LocalDate to SQL Date
            pstmt.setDate(7, Date.valueOf(member.getJoiningDate()));
            pstmt.setString(8, member.getAddress());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * R - Read: Retrieves all members from the database.
     * @return A list of Member objects.
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Member";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Member member = new Member(
                        rs.getString("MemberID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("ContactNumber"),
                        rs.getString("Email"),
                        rs.getDate("DateOfBirth").toLocalDate(), // Convert SQL Date to LocalDate
                        rs.getDate("JoiningDate").toLocalDate(),
                        rs.getString("Address")
                );
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving members: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    /**
     * U - Update: Updates an existing member's details.
     * @param member The Member object with updated data.
     * @return true if successful, false otherwise.
     */
    public boolean updateMember(Member member) {
        String sql = "UPDATE Member SET FirstName = ?, LastName = ?, ContactNumber = ?, Email = ?, DateOfBirth = ?, Address = ? " +
                "WHERE MemberID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, member.getContactNumber());
            pstmt.setString(4, member.getEmail());
            pstmt.setDate(5, Date.valueOf(member.getDateOfBirth()));
            pstmt.setString(6, member.getAddress());
            pstmt.setString(7, member.getMemberId()); // Where condition

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * D - Delete: Deletes a member by ID.
     * @param memberId The ID of the member to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteMember(String memberId) {
        String sql = "DELETE FROM Member WHERE MemberID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}