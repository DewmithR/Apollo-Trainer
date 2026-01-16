package ui;

import db.SystemUserDAO;
import model.SystemUser;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;

public class UserController {

    private final SystemUserDAO userDAO = new SystemUserDAO();
    private final ObservableList<SystemUser> masterUserList = FXCollections.observableArrayList();
    private final ObservableList<String> roles = FXCollections.observableArrayList("Admin", "Instructor", "Staff");

    // FXML Components
    @FXML private TableView<SystemUser> userTable;
    @FXML private TableColumn<SystemUser, Integer> userIDColumn;
    @FXML private TableColumn<SystemUser, String> usernameColumn;
    @FXML private TableColumn<SystemUser, String> firstNameColumn;
    @FXML private TableColumn<SystemUser, String> lastNameColumn;
    @FXML private TableColumn<SystemUser, String> roleColumn;
    @FXML private TableColumn<SystemUser, Boolean> isActiveColumn;

    @FXML private TextField userIDField;
    @FXML private TextField usernameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private CheckBox isActiveCheckBox;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;


    @FXML
    public void initialize() {
        // Setup Table
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        isActiveColumn.setCellValueFactory(new PropertyValueFactory<>("isActive"));
        userTable.setItems(masterUserList);

        roleComboBox.setItems(roles);

        loadAllUsers();

        // Event Listeners and Bindings
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showUserDetails(newVal));

        // Disable Update/Delete if nothing is selected
        updateButton.disableProperty().bind(Bindings.isEmpty(userTable.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(userTable.getSelectionModel().getSelectedItems()));
    }

    private void loadAllUsers() {
        masterUserList.clear();
        masterUserList.addAll(userDAO.getAllUsers());
        statusLabel.setText("Loaded " + masterUserList.size() + " system user accounts.");
    }

    private void showUserDetails(SystemUser user) {
        if (user != null) {
            userIDField.setText(String.valueOf(user.getUserID()));
            usernameField.setText(user.getUsername());
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            roleComboBox.getSelectionModel().select(user.getRole());
            isActiveCheckBox.setSelected(user.getIsActive());

            // Clear password field for security
            passwordField.clear();

            // Enable update/delete mode
            addButton.setDisable(true);
        } else {
            handleClearForm();
        }
    }

    // CRUD Handlers
    @FXML
    private void handleClearForm() {
        userIDField.clear();
        usernameField.clear();
        firstNameField.clear();
        lastNameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        isActiveCheckBox.setSelected(true);
        userTable.getSelectionModel().clearSelection();
        statusLabel.setText("Ready to add new user.");
        addButton.setDisable(false);
    }

    @FXML
    private void handleAddUser() {
        if (isInputValid(true)) {
            SystemUser newUser = new SystemUser(
                    usernameField.getText(),
                    passwordField.getText(), // Raw password passed to DAO for hashing
                    firstNameField.getText(),
                    lastNameField.getText(),
                    roleComboBox.getValue()
            );
            newUser.setIsActive(isActiveCheckBox.isSelected());

            if (userDAO.addUser(newUser, passwordField.getText())) {
                masterUserList.add(newUser);
                statusLabel.setText("✅ User '" + newUser.getUsername() + "' added successfully!");
                handleClearForm();
            } else {
                statusLabel.setText("❌ Error: Could not add user (Username may already exist).");
            }
        }
    }

    @FXML
    private void handleUpdateUser() {
        SystemUser selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null && isInputValid(false)) {

            String newPassword = passwordField.getText();

            // Update model properties
            selectedUser.setUsername(usernameField.getText());
            selectedUser.setFirstName(firstNameField.getText());
            selectedUser.setLastName(lastNameField.getText());
            selectedUser.setRole(roleComboBox.getValue());
            selectedUser.setIsActive(isActiveCheckBox.isSelected());

            if (userDAO.updateUser(selectedUser, newPassword)) {
                userTable.refresh();
                statusLabel.setText("🔄 User ID " + selectedUser.getUserID() + " updated successfully!");
                handleClearForm();
            } else {
                statusLabel.setText("❌ Error: Could not update user.");
            }
        }
    }

    @FXML
    private void handleDeleteUser() {
        SystemUser selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            int userId = selectedUser.getUserID();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete user " + selectedUser.getUsername() + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (userDAO.deleteUser(userId)) {
                    masterUserList.remove(selectedUser);
                    statusLabel.setText("🗑️ User ID " + userId + " deleted.");
                    handleClearForm();
                } else {
                    statusLabel.setText("❌ Error: Could not delete user.");
                }
            }
        }
    }

    // Validation Methods
    private boolean isInputValid(boolean isNewUser) {
        String errorMessage = "";

        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            errorMessage += "Username cannot be empty!\n";
        }
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            errorMessage += "First Name cannot be empty!\n";
        }
        if (roleComboBox.getSelectionModel().isEmpty()) {
            errorMessage += "Role must be selected!\n";
        }

        if (isNewUser && (passwordField.getText() == null || passwordField.getText().isEmpty())) {
            errorMessage += "Password is required for a new user!\n";
        }

        if (errorMessage.isEmpty()) { return true; }
        statusLabel.setText("❌ Validation Error:\n" + errorMessage);
        return false;
    }

    // Navigation Handler
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load dashboard view.");
            alert.showAndWait();
        }
    }
}