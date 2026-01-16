package ui;

import db.MemberDAO;
import model.Member;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent; // <-- FIX 1: ADDED MISSING IMPORT
import javafx.scene.Node;          // <-- FIX 3: ADDED MISSING IMPORT
import javafx.stage.Stage;         // <-- FIX 3: ADDED MISSING IMPORT
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.LocalDate;

public class MemberController {

    // --- FXML UI Components ---
    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, String> idColumn;
    @FXML private TableColumn<Member, String> firstNameColumn;
    @FXML private TableColumn<Member, String> lastNameColumn;
    @FXML private TableColumn<Member, String> contactColumn;
    @FXML private TableColumn<Member, LocalDate> joiningDateColumn;
    @FXML private TableColumn<Member, String> emailColumn;

    @FXML private TextField searchField;
    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private DatePicker dobPicker;
    @FXML private DatePicker joiningPicker;
    @FXML private TextField addressField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button backToDashboardButton; // <-- FIX 2: RENAMED to match convention (used in FXML)
    @FXML private Label statusLabel;

    // --- Data and Logic ---
    private final MemberDAO memberDAO = new MemberDAO();
    private final ObservableList<Member> masterMemberList = FXCollections.observableArrayList();

    // Initialization method (Called automatically by FXMLLoader)
    @FXML
    public void initialize() {
        // Set up TableView columns to link to Member model properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        joiningDateColumn.setCellValueFactory(new PropertyValueFactory<>("joiningDate"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Load initial data
        loadMembers();

        // 1. Handle row selection: Populate fields with selected member details
        memberTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showMemberDetails(newValue));

        // 2. Implement Search/Filter functionality
        setupSearchFilter();

        // 3. Disable Update/Delete buttons initially until a member is selected
        updateButton.disableProperty().bind(Bindings.isEmpty(memberTable.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(memberTable.getSelectionModel().getSelectedItems()));

        // 4. Set joining date default to today
        joiningPicker.setValue(LocalDate.now());
    }

    // --- Search Implementation ---
    private void setupSearchFilter() {
        FilteredList<Member> filteredData = new FilteredList<>(masterMemberList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(member -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Display all if filter is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();

                // Search logic (checks ID, First Name, Last Name, Email)
                if (member.getMemberId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getEmail().toLowerCase().contains(lowerCaseFilter)) { // Improved search
                    return true;
                }
                return false;
            });
        });

        SortedList<Member> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(memberTable.comparatorProperty());
        memberTable.setItems(sortedData);
    }

    // --- Data Loading ---
    @FXML
    public void loadMembers() {
        masterMemberList.clear();
        masterMemberList.addAll(memberDAO.getAllMembers());
        statusLabel.setText("Loaded " + masterMemberList.size() + " members from database.");
    }

    // --- Detail Panel Population ---
    private void showMemberDetails(Member member) {
        if (member != null) {
            idField.setText(member.getMemberId());
            firstNameField.setText(member.getFirstName());
            lastNameField.setText(member.getLastName());
            contactField.setText(member.getContactNumber());
            emailField.setText(member.getEmail());
            dobPicker.setValue(member.getDateOfBirth());
            joiningPicker.setValue(member.getJoiningDate());
            addressField.setText(member.getAddress());
            addButton.setDisable(true); // Can't add when a member is selected
        } else {
            handleClearForm();
        }
    }

    // --- CRUD Handlers ---
    @FXML
    private void handleAddMember() {
        if (isInputValid()) {
            Member newMember = new Member(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    contactField.getText(),
                    emailField.getText(),
                    dobPicker.getValue(),
                    joiningPicker.getValue(),
                    addressField.getText()
            );

            if (memberDAO.addMember(newMember)) {
                masterMemberList.add(newMember);
                statusLabel.setText("✅ Member added successfully! ID: " + newMember.getMemberId());
                handleClearForm();
            } else {
                statusLabel.setText("❌ Error: Could not add member.");
            }
        }
    }

    @FXML
    private void handleUpdateMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null && isInputValid()) {

            // Update the selected Member object
            selectedMember.setFirstName(firstNameField.getText());
            selectedMember.setLastName(lastNameField.getText());
            selectedMember.setContactNumber(contactField.getText());
            selectedMember.setEmail(emailField.getText());
            selectedMember.setDateOfBirth(dobPicker.getValue());
            selectedMember.setJoiningDate(joiningPicker.getValue());
            selectedMember.setAddress(addressField.getText());

            if (memberDAO.updateMember(selectedMember)) {
                // Refresh the table view to show updated data
                memberTable.refresh();
                statusLabel.setText("🔄 Member ID " + selectedMember.getMemberId() + " updated successfully.");
                handleClearForm();
            } else {
                statusLabel.setText("❌ Error: Could not update member.");
            }
        }
    }

    @FXML
    private void handleDeleteMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {

            if (memberDAO.deleteMember(selectedMember.getMemberId())) {
                masterMemberList.remove(selectedMember);
                statusLabel.setText("🗑️ Member ID " + selectedMember.getMemberId() + " deleted.");
                handleClearForm();
            } else {
                statusLabel.setText("❌ Error: Could not delete member.");
            }
        }
    }

    // --- Utility Methods ---
    @FXML
    private void handleClearForm() {
        idField.clear();
        firstNameField.clear();
        lastNameField.clear();
        contactField.clear();
        emailField.clear();
        dobPicker.setValue(null);
        joiningPicker.setValue(LocalDate.now());
        addressField.clear();
        memberTable.getSelectionModel().clearSelection();
        addButton.setDisable(false);
        statusLabel.setText("Form cleared. Ready to add new member.");
    }

    private boolean isInputValid() {
        String errorMessage = "";

        // Minimal validation checks
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            errorMessage += "No valid first name!\n";
        }
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            errorMessage += "No valid last name!\n";
        }
        if (contactField.getText() == null || contactField.getText().trim().isEmpty()) {
            errorMessage += "No valid contact number!\n";
        }
        if (dobPicker.getValue() == null) {
            errorMessage += "No valid date of birth!\n";
        }
        if (joiningPicker.getValue() == null) {
            errorMessage += "No valid joining date!\n";
        }
        // You might want to add email format validation here later

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            statusLabel.setText("❌ Validation Error:\n" + errorMessage);
            return false;
        }
    }

    // --- Navigation Handler ---
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            // Load the Dashboard FXML
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
            e.printStackTrace();
        }
    }
}