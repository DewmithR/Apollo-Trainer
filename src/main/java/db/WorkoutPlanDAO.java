package db;

import model.WorkoutPlan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlanDAO {

    // Create - Add a new workout plan
    public boolean addPlan(WorkoutPlan plan) {
        String sql = "INSERT INTO WorkoutPlan (PlanName, Description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, plan.getPlanName());
            pstmt.setString(2, plan.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        plan.setPlanID(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error adding workout plan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Read All - Get all workout plans
    public List<WorkoutPlan> getAllPlans() {
        List<WorkoutPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM WorkoutPlan";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                WorkoutPlan plan = new WorkoutPlan(
                        rs.getInt("PlanID"),
                        rs.getString("PlanName"),
                        rs.getString("Description")
                );
                plans.add(plan);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving workout plans: " + e.getMessage());
        }
        return plans;
    }

    // Update - Update an existing workout plan
    public boolean updatePlan(WorkoutPlan plan) {
        String sql = "UPDATE WorkoutPlan SET PlanName = ?, Description = ? WHERE PlanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plan.getPlanName());
            pstmt.setString(2, plan.getDescription());
            pstmt.setInt(3, plan.getPlanID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating workout plan: " + e.getMessage());
            return false;
        }
    }

    // Delete - Delete a workout plan
    public boolean deletePlan(int planID) {
        String sql = "DELETE FROM WorkoutPlan WHERE PlanID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, planID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting workout plan: " + e.getMessage());
            return false;
        }
    }
}