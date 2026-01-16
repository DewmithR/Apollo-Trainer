package db;

import model.SystemUser;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemUserDAO {

    /**
     * Helper method to simulate password hashing for safe storage.
     * IN A REAL APPLICATION, USE A SECURE HASHING LIBRARY (e.g., BCrypt).
     */
    private String hashPassword(String password) {
        // Simple, insecure return for demonstration purposes:
        return password;
        // Real implementation: return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * C - Create: Adds a new system user.
     */
    public boolean addUser(SystemUser user, String rawPassword) {
        String sql = "INSERT INTO SystemUser (Username, PasswordHash, FirstName, LastName, Role, IsActive) VALUES (?, ?, ?, ?, ?, ?)";
        String hashedPassword = hashPassword(rawPassword);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getRole());
            pstmt.setBoolean(6, user.getIsActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserID(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error adding system user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * R - Read All: Retrieves all system users.
     */
    public List<SystemUser> getAllUsers() {
        List<SystemUser> users = new ArrayList<>();
        // Note: Do NOT retrieve the actual PasswordHash for security unless absolutely necessary (e.g., for verification logic)
        String sql = "SELECT UserID, Username, FirstName, LastName, Role, IsActive FROM SystemUser";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SystemUser user = new SystemUser(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        "", // We explicitly pass an empty string for the passwordHash to prevent exposure
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Role"),
                        rs.getBoolean("IsActive")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving system users: " + e.getMessage());
        }
        return users;
    }

    /**
     * U - Update: Updates user details, including optional password change.
     */
    public boolean updateUser(SystemUser user, String newPassword) {
        String sql = "";

        if (newPassword != null && !newPassword.isEmpty()) {
            // Update with new password hash
            sql = "UPDATE SystemUser SET Username = ?, PasswordHash = ?, FirstName = ?, LastName = ?, Role = ?, IsActive = ? WHERE UserID = ?";
        } else {
            // Update without changing password
            sql = "UPDATE SystemUser SET Username = ?, FirstName = ?, LastName = ?, Role = ?, IsActive = ? WHERE UserID = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());

            int paramIndex = 2;
            if (newPassword != null && !newPassword.isEmpty()) {
                String hashedPassword = hashPassword(newPassword);
                pstmt.setString(2, hashedPassword);
                paramIndex = 3;
            }

            pstmt.setString(paramIndex++, user.getFirstName());
            pstmt.setString(paramIndex++, user.getLastName());
            pstmt.setString(paramIndex++, user.getRole());
            pstmt.setBoolean(paramIndex++, user.getIsActive());
            pstmt.setInt(paramIndex, user.getUserID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating system user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * D - Delete: Deletes a system user.
     */
    public boolean deleteUser(int userID) {
        String sql = "DELETE FROM SystemUser WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting system user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}