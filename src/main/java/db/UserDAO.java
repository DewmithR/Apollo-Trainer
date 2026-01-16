package db;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAO {

    /**
     * Authenticates a user based on username and a simple password check.
     * In a real application, you'd use a hashing library like BCrypt.
     * For this beginner project, we'll do a simple (INSECURE) check.
     * * @param username The username provided.
     * @param password The password provided.
     * @return An Optional containing the User object if successful, or empty otherwise.
     */
    public Optional<User> authenticateUser(String username, String password) {
        String sql = "SELECT UserID, FirstName, LastName, Role, IsActive FROM SystemUser WHERE Username = ? AND PasswordHash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // **Insecure: Only for beginner assignment purposes**

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // User successfully authenticated
                    User user = new User(
                            rs.getInt("UserID"),
                            username,
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("Role"),
                            rs.getBoolean("IsActive")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }
}