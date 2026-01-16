package db;

import model.BodyMeasurement;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BodyMeasurementDAO {

    /**
     * C - Create: Adds a new body measurement record.
     */
    public boolean addMeasurement(BodyMeasurement record) {
        // Updated SQL to match the five columns in your schema
        String sql = "INSERT INTO BodyMeasurement (MemberID, Weight, Height, BMI, BodyFatPercentage) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, record.getMemberID());
            pstmt.setDouble(2, record.getWeight());
            pstmt.setDouble(3, record.getHeight());
            pstmt.setDouble(4, record.getBmi());
            pstmt.setDouble(5, record.getBodyFatPercentage());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setMeasurementID(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error adding body measurement: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * R - Read All: Retrieves all body measurement records with member names.
     */
    public List<BodyMeasurement> getAllMeasurements() {
        List<BodyMeasurement> records = new ArrayList<>();
        // Updated SQL query (removed RecordDate, ordered by MeasurementID DESC as a default)
        String sql = "SELECT BM.*, M.FirstName, M.LastName FROM BodyMeasurement BM " +
                "JOIN Member M ON BM.MemberID = M.MemberID " +
                "ORDER BY BM.MeasurementID DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String memberName = rs.getString("FirstName") + " " + rs.getString("LastName");

                BodyMeasurement record = new BodyMeasurement(
                        rs.getInt("MeasurementID"),
                        rs.getString("MemberID"),
                        memberName,
                        rs.getDouble("Weight"),
                        rs.getDouble("Height"),
                        rs.getDouble("BMI"),
                        rs.getDouble("BodyFatPercentage")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving body measurements: " + e.getMessage());
        }
        return records;
    }

    /**
     * U - Update: Updates an existing body measurement record.
     */
    public boolean updateMeasurement(BodyMeasurement record) {
        // Updated SQL to match the five columns in your schema
        String sql = "UPDATE BodyMeasurement SET MemberID = ?, Weight = ?, Height = ?, BMI = ?, BodyFatPercentage = ? WHERE MeasurementID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getMemberID());
            pstmt.setDouble(2, record.getWeight());
            pstmt.setDouble(3, record.getHeight());
            pstmt.setDouble(4, record.getBmi());
            pstmt.setDouble(5, record.getBodyFatPercentage());
            pstmt.setInt(6, record.getMeasurementID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating body measurement: " + e.getMessage());
            return false;
        }
    }

    /**
     * D - Delete: Deletes a body measurement record.
     */
    public boolean deleteMeasurement(int measurementID) {
        String sql = "DELETE FROM BodyMeasurement WHERE MeasurementID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, measurementID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting body measurement: " + e.getMessage());
            return false;
        }
    }
}