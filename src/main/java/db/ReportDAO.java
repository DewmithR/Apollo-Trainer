package db;

import model.MemberReportEntry;
import model.FinancialReportEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    /**
     * Retrieves data for the Member Report (Member status and membership details).
     * Corresponds to the combined Member and Membership tables.
     */
    public List<MemberReportEntry> getMemberReport() {
        List<MemberReportEntry> reportList = new ArrayList<>();

        // This query joins Member and Membership tables to get a comprehensive view
        String sql = "SELECT M.MemberID, M.FirstName, M.LastName, " +
                "MS.MembershipType, MS.StartDate, MS.EndDate, MS.PaymentStatus " +
                "FROM Member M " +
                "JOIN Membership MS ON M.MemberID = MS.MemberID " +
                "ORDER BY M.LastName, MS.StartDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String fullName = rs.getString("FirstName") + " " + rs.getString("LastName");

                MemberReportEntry entry = new MemberReportEntry(
                        rs.getString("MemberID"),
                        fullName,
                        rs.getString("MembershipType"),
                        rs.getDate("StartDate").toLocalDate(),
                        rs.getDate("EndDate").toLocalDate(),
                        rs.getString("PaymentStatus")
                );
                reportList.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving Member Report: " + e.getMessage());
        }
        return reportList;
    }

    /**
     * Retrieves data for the Financial Report (detailed payment transactions).
     * Corresponds to the combined Member and Membership tables (using payment fields).
     */
    public List<FinancialReportEntry> getFinancialReport() {
        List<FinancialReportEntry> reportList = new ArrayList<>();

        String sql = "SELECT M.MemberID, M.FirstName, M.LastName, " +
                "MS.MembershipType, MS.PaymentDate, MS.PaymentAmount, MS.PaymentStatus " +
                "FROM Membership MS " +
                "JOIN Member M ON MS.MemberID = M.MemberID " +
                "WHERE MS.PaymentAmount IS NOT NULL AND MS.PaymentAmount > 0 " + // Filter for actual payments
                "ORDER BY MS.PaymentDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String fullName = rs.getString("FirstName") + " " + rs.getString("LastName");

                FinancialReportEntry entry = new FinancialReportEntry(
                        rs.getString("MemberID"),
                        fullName,
                        rs.getString("MembershipType"),
                        rs.getDate("PaymentDate").toLocalDate(),
                        rs.getDouble("PaymentAmount"),
                        rs.getString("PaymentStatus")
                );
                reportList.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving Financial Report: " + e.getMessage());
        }
        return reportList;
    }

    /**
     * Calculates the total revenue from the Membership table.
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(PaymentAmount) AS TotalRevenue FROM Membership WHERE PaymentStatus = 'Paid'";
        double total = 0.0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                total = rs.getDouble("TotalRevenue");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
        }
        return total;
    }
}