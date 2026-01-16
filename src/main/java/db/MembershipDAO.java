package db;

import model.Membership;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class MembershipDAO {

    //Create: Adds a new membership/payment record.
    public boolean addMembership(Membership membership) {
        String sql = "INSERT INTO Membership (MemberID, StartDate, EndDate, MembershipType, PaymentAmount, PaymentDate, PaymentStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, membership.getMemberID());
            pstmt.setDate(2, Date.valueOf(membership.getStartDate()));
            pstmt.setDate(3, Date.valueOf(membership.getEndDate()));
            pstmt.setString(4, membership.getMembershipType());
            pstmt.setDouble(5, membership.getPaymentAmount());
            pstmt.setDate(6, Date.valueOf(membership.getPaymentDate()));
            pstmt.setString(7, membership.getPaymentStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated ID (MembershipID)
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error adding membership record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //Read: Retrieves all membership records, joining to get the Member's full name.
    public List<Membership> getAllMemberships() {
        List<Membership> memberships = new ArrayList<>();
        String sql = "SELECT M.*, Mem.FirstName, Mem.LastName FROM Membership M " +
                "JOIN Member Mem ON M.MemberID = Mem.MemberID " +
                "ORDER BY M.MembershipID DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Membership membership = new Membership(
                        rs.getInt("MembershipID"),
                        rs.getString("MemberID"),
                        rs.getDate("StartDate").toLocalDate(),
                        rs.getDate("EndDate").toLocalDate(),
                        rs.getString("MembershipType"),
                        rs.getDouble("PaymentAmount"),
                        rs.getDate("PaymentDate").toLocalDate(),
                        rs.getString("PaymentStatus")
                );
                memberships.add(membership);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving membership records: " + e.getMessage());
            e.printStackTrace();
        }
        return memberships;
    }

    //Delete: Deletes a membership record by MembershipID.
    public boolean deleteMembership(int membershipID) {
        String sql = "DELETE FROM Membership WHERE MembershipID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, membershipID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting membership record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}