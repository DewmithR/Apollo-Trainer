package db;

import model.MemberWorkout;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberWorkoutDAO {

    // C - Create (Assign)
    public boolean assignPlan(MemberWorkout assignment) {
        String sql = "INSERT INTO MemberWorkout (MemberID, PlanID, AssignedDate) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, assignment.getMemberID());
            pstmt.setInt(2, assignment.getPlanID());
            pstmt.setDate(3, Date.valueOf(assignment.getAssignedDate()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        assignment.setAssignmentID(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error assigning workout plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // R - Read All Assignments (Joins for display names)
    public List<MemberWorkout> getAllAssignments() {
        List<MemberWorkout> assignments = new ArrayList<>();
        // Select from MemberWorkout, join Member and WorkoutPlan for descriptive names
        String sql = "SELECT MW.*, M.FirstName, M.LastName, WP.PlanName FROM MemberWorkout MW " +
                "JOIN Member M ON MW.MemberID = M.MemberID " +
                "JOIN WorkoutPlan WP ON MW.PlanID = WP.PlanID " +
                "ORDER BY MW.AssignedDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String memberName = rs.getString("FirstName") + " " + rs.getString("LastName");

                MemberWorkout assignment = new MemberWorkout(
                        rs.getInt("AssignmentID"),
                        rs.getString("MemberID"),
                        rs.getInt("PlanID"),
                        memberName,
                        rs.getString("PlanName"),
                        rs.getDate("AssignedDate").toLocalDate()
                );
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving workout assignments: " + e.getMessage());
            e.printStackTrace();
        }
        return assignments;
    }

    // D - Delete Assignment
    public boolean deleteAssignment(int assignmentID) {
        String sql = "DELETE FROM MemberWorkout WHERE AssignmentID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, assignmentID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting assignment: " + e.getMessage());
            return false;
        }
    }
}