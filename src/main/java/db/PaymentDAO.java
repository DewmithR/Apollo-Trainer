package db;

import model.Payment;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    private String generateNextPaymentID(Connection conn) throws SQLException {
        String sql = "SELECT TOP 1 PaymentID FROM Payment ORDER BY PaymentID DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("PaymentID");
                int number = Integer.parseInt(lastId.substring(2)); // Assumes format PYYYY (e.g., P0001)
                return String.format("P%04d", number + 1);
            } else {
                return "P0001";
            }
        }
    }

    // Create - Add Payment
    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO Payment (PaymentID, MemberID, MembershipTypeID, PaymentDate, AmountPaid, PaymentMethod) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String newId = generateNextPaymentID(conn);
            payment.setPaymentID(newId);

            pstmt.setString(1, newId);
            pstmt.setString(2, payment.getMemberID());
            pstmt.setString(3, payment.getMembershipTypeID());
            pstmt.setDate(4, Date.valueOf(payment.getPaymentDate()));
            pstmt.setDouble(5, payment.getAmountPaid());
            pstmt.setString(6, payment.getPaymentMethod());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Read All (Joins with Member table to get full name)
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT P.*, M.FirstName, M.LastName FROM Payment P " +
                "JOIN Member M ON P.MemberID = M.MemberID";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String fullName = rs.getString("FirstName") + " " + rs.getString("LastName");

                Payment payment = new Payment(
                        rs.getString("PaymentID"),
                        rs.getString("MemberID"),
                        fullName,
                        rs.getString("MembershipTypeID"),
                        rs.getDate("PaymentDate").toLocalDate(),
                        rs.getDouble("AmountPaid"),
                        rs.getString("PaymentMethod")
                );
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    // Delete - Delete Payment
    public boolean deletePayment(String paymentID) {
        String sql = "DELETE FROM Payment WHERE PaymentID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paymentID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            return false;
        }
    }

}