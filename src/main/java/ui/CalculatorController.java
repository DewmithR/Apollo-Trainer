package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.DecimalFormat;

public class CalculatorController {

    private final DecimalFormat df = new DecimalFormat("0.0");
    private double lastCalculatedBMR = 0; // Store BMR for TDEE calculation

    // --- BMI Components ---
    @FXML private TextField bmiWeightField; // kg
    @FXML private TextField bmiHeightField; // meters
    @FXML private Label bmiResultLabel;
    @FXML private Label bmiCategoryLabel;

    // --- BMR Components ---
    @FXML private TextField bmrWeightField;  // kg
    @FXML private TextField bmrHeightField;  // cm
    @FXML private TextField bmrAgeField;
    @FXML private ComboBox<String> bmrGenderComboBox;
    @FXML private Label bmrResultLabel;

    // --- TDEE Components ---
    @FXML private TextField tdeeBmrField;
    @FXML private ComboBox<String> activityLevelComboBox;
    @FXML private Label tdeeResultLabel;

    // --- Activity Level Data ---
    private final ObservableList<String> genders = FXCollections.observableArrayList("Male", "Female");

    // Activity factors (Mifflin-St Jeor equation)
    private final ObservableList<String> activityLevels = FXCollections.observableArrayList(
            "1.2 - Sedentary (little or no exercise)",
            "1.375 - Lightly Active (light exercise/sports 1-3 days/week)",
            "1.55 - Moderately Active (moderate exercise/sports 3-5 days/week)",
            "1.725 - Very Active (hard exercise/sports 6-7 days/week)",
            "1.9 - Extremely Active (hard daily exercise/physical job)"
    );


    @FXML
    public void initialize() {
        // Setup BMR Gender ComboBox
        bmrGenderComboBox.setItems(genders);

        // Setup TDEE Activity ComboBox
        activityLevelComboBox.setItems(activityLevels);

        // Set default values
        bmrGenderComboBox.getSelectionModel().select("Male");
        activityLevelComboBox.getSelectionModel().select(0);

        // Make the BMR field in TDEE tab read-only if possible
        tdeeBmrField.setEditable(false);
    }

    // --- BMI Calculation (kg / m^2) ---
    @FXML
    private void calculateBMI() {
        try {
            double weight = Double.parseDouble(bmiWeightField.getText());
            double height = Double.parseDouble(bmiHeightField.getText()); // Must be in meters

            if (height <= 0 || weight <= 0) {
                showAlert("Input Error", "Weight and height must be positive values.");
                return;
            }

            double bmi = weight / (height * height);
            String category = getBMICategory(bmi);

            bmiResultLabel.setText("BMI: " + df.format(bmi));
            bmiCategoryLabel.setText("Category: " + category);

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numeric values for Weight and Height.");
        }
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 24.9) return "Normal weight (Healthy)";
        if (bmi < 29.9) return "Overweight";
        return "Obesity";
    }

    // --- BMR Calculation (Mifflin-St Jeor Equation) ---
    @FXML
    private void calculateBMR() {
        try {
            double weight = Double.parseDouble(bmrWeightField.getText());  // kg
            double height = Double.parseDouble(bmrHeightField.getText());  // cm
            int age = Integer.parseInt(bmrAgeField.getText());
            String gender = bmrGenderComboBox.getValue();

            if (gender == null) {
                showAlert("Input Error", "Please select a Gender.");
                return;
            }

            // Mifflin-St Jeor Equation:
            // P = (10.0 * weight_kg) + (6.25 * height_cm) - (5.0 * age_y) + s
            double bmr = (10.0 * weight) + (6.25 * height) - (5.0 * age);

            if ("Male".equals(gender)) {
                bmr += 5;
            } else { // Female
                bmr -= 161;
            }

            lastCalculatedBMR = bmr;

            bmrResultLabel.setText("BMR: " + df.format(bmr) + " Calories/day");

            // Auto-populate TDEE field
            tdeeBmrField.setText(df.format(bmr));

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numeric values for Weight, Height, and Age.");
        }
    }

    // --- TDEE Calculation (BMR * Activity Factor) ---
    @FXML
    private void calculateTDEE() {
        try {
            double bmr = Double.parseDouble(tdeeBmrField.getText());
            String selectedActivity = activityLevelComboBox.getValue();

            if (selectedActivity == null || selectedActivity.isEmpty()) {
                showAlert("Input Error", "Please select an Activity Level.");
                return;
            }

            // Extract the numerical factor from the ComboBox string (e.g., "1.55 - Moderately Active...")
            String factorString = selectedActivity.substring(0, selectedActivity.indexOf(' '));
            double activityFactor = Double.parseDouble(factorString);

            double tdee = bmr * activityFactor;

            tdeeResultLabel.setText("TDEE: " + df.format(tdee) + " Calories/day");

        } catch (NumberFormatException e) {
            showAlert("Input Error", "BMR must be a valid number. Please calculate BMR first or enter a value manually.");
        }
    }

    // --- Utility Methods ---
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Navigation Handler ---
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent dashboardRoot = fxmlLoader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Apollo Trainer - Dashboard");

            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Dashboard screen: " + e.getMessage());
        }
    }
}