package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button memberManagerButton;
    @FXML private Button membershipButton;
    @FXML private Button workoutManagerButton;
    @FXML private Button userManagerButton;
    @FXML private Button calculatorToolsButton;


     // Initializes the dashboard after authenticating user.

    public void initData(User user) {
        if (user != null) {
            welcomeLabel.setText("User: " + user.getFirstName() + " (" + user.getRole() + ")");
            System.out.println("User loaded into Dashboard: " + user.getUsername());
        }
    }


     // Logout button, returning the user to the login screen.

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Load the Login FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Login");

            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);

            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading login screen during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Member management button
    @FXML
    private void handleMemberManagement(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/member.fxml"));
            Parent memberRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Member Management");

            Scene scene = new Scene(memberRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Member Management screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Membership & Payments button
    @FXML
    private void handleMembershipManagement(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/membership.fxml"));
            Parent membershipRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Membership & Payments");

            Scene scene = new Scene(membershipRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Membership Management screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Workout management button
    @FXML
    private void handleWorkoutManagement(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/workout.fxml"));
            Parent workoutRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Workout Management");

            Scene scene = new Scene(workoutRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Workout Management screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // System user management button
    @FXML
    private void handleUserManagement(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/user-management.fxml"));
            Parent userRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - User Management");

            Scene scene = new Scene(userRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading User Management screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Health calculators button
    @FXML
    private void handleCalculatorTools(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/calculator.fxml"));
            Parent calculatorRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Health Calculators");

            Scene scene = new Scene(calculatorRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Health Calculators screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Reports module button
    @FXML
    private void handleReportsModule(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));
            Parent reportsRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Reports Module");

            Scene scene = new Scene(reportsRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Reports Module screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Measurement tracking button
    @FXML
    private void handleMeasurementTracking(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/measurements.fxml"));
            Parent measurementRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Measurement Tracking");

            Scene scene = new Scene(measurementRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Measurement Tracking screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}