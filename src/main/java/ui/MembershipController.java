package ui;

import db.MemberDAO;
import db.MembershipDAO;
import model.Member;
import model.Membership;
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
import java.time.LocalDate;
import java.util.List;

public class MembershipController {

    // DAOs
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final MemberDAO memberDAO = new MemberDAO();

    // Data Lists
    private final ObservableList<Membership> masterMembershipList = FXCollections.observableArrayList();
    private final ObservableList<Member> memberLookupList = FXCollections.observableArrayList();
    private final ObservableList<String> planTypes = FXCollections.observableArrayList("Monthly", "Quarterly", "Annual", "Premium");
    private final ObservableList<String> paymentStatuses = FXCollections.observableArrayList("Paid", "Pending", "Cancelled");

    // FXML Components
    @FXML private TableView<Membership> membershipTable;
    @FXML private TableColumn<Membership, Integer> membershipIDColumn;
    @FXML private TableColumn<Membership, String> memberIDColumn;
    @FXML private TableColumn<Membership, String> membershipTypeColumn;
    @FXML private TableColumn<Membership, LocalDate> startDateColumn;
    @FXML private TableColumn<Membership, LocalDate> endDateColumn;
    @FXML private TableColumn<Membership, Double> paymentAmountColumn;
    @FXML private TableColumn<Membership, LocalDate> paymentDateColumn;
    @FXML private TableColumn<Membership, String> paymentStatusColumn;

    @FXML private ComboBox<Member> memberComboBox;
    @FXML private ComboBox<String> planTypeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField amountPaidField;
    @FXML private DatePicker paymentDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button deleteRecordButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // Setup Tables
        membershipIDColumn.setCellValueFactory(new PropertyValueFactory<>("membershipID"));
        memberIDColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        membershipTypeColumn.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        membershipTable.setItems(masterMembershipList);

        // Setup ComboBoxes
        planTypeComboBox.setItems(planTypes);
        statusComboBox.setItems(paymentStatuses);

        loadAllData();

        // Bindings & Defaults
        deleteRecordButton.disableProperty().bind(Bindings.isEmpty(membershipTable.getSelectionModel().getSelectedItems()));
        startDatePicker.setValue(LocalDate.now());
        paymentDatePicker.setValue(LocalDate.now());

        // Set ComboBox display for Member (ID | Name)
        memberComboBox.setCellFactory(lv -> new ListCell<Member>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getMemberId() + " | " + item.getFullName());
            }
        });
        memberComboBox.setButtonCell(memberComboBox.getCellFactory().call(null));
    }

    private void loadAllData() {
        // Load existing membership records
        masterMembershipList.clear();
        masterMembershipList.addAll(membershipDAO.getAllMemberships());

        // Load member data for ComboBox lookup
        memberLookupList.clear();
        memberLookupList.addAll(memberDAO.getAllMembers());
        memberComboBox.setItems(memberLookupList);
        statusLabel.setText("Loaded " + masterMembershipList.size() + " membership records.");
    }

    // CRUD Handlers
    @FXML
    private void handleClearForm() {
        memberComboBox.getSelectionModel().clearSelection();
        planTypeComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(null);
        amountPaidField.clear();
        paymentDatePicker.setValue(LocalDate.now());
        statusComboBox.getSelectionModel().select("Paid");
        membershipTable.getSelectionModel().clearSelection();
        statusLabel.setText("Ready to record new membership.");
    }

    @FXML
    private void handleRecordMembership() {
        if (isInputValid()) {
            Member member = memberComboBox.getSelectionModel().getSelectedItem();

            Membership newMembership = new Membership(
                    member.getMemberId(),
                    startDatePicker.getValue(),
                    endDatePicker.getValue(),
                    planTypeComboBox.getValue(),
                    Double.parseDouble(amountPaidField.getText()),
                    paymentDatePicker.getValue(),
                    statusComboBox.getValue()
            );

            if (membershipDAO.addMembership(newMembership)) {
                loadAllData(); // Reload to get the new ID
                statusLabel.setText("✅ Membership recorded successfully!");
                handleClearForm();
            } else {
                statusLabel.setText("❌ Error: Could not record membership.");
            }
        }
    }

    @FXML
    private void handleDeleteRecord() {
        Membership selectedRecord = membershipTable.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            int recordId = selectedRecord.getMembershipID();

            if (membershipDAO.deleteMembership(recordId)) {
                masterMembershipList.remove(selectedRecord);
                statusLabel.setText("🗑️ Membership Record ID " + recordId + " deleted.");
            } else {
                statusLabel.setText("❌ Error: Could not delete record.");
            }
        }
    }

    // Validation
    private boolean isInputValid() {
        String errorMessage = "";

        if (memberComboBox.getSelectionModel().isEmpty()) {
            errorMessage += "Must select a Member!\n";
        }
        if (planTypeComboBox.getSelectionModel().isEmpty()) {
            errorMessage += "Must select a Plan Type!\n";
        }
        if (startDatePicker.getValue() == null) {
            errorMessage += "Start Date is required!\n";
        }
        if (endDatePicker.getValue() == null) {
            errorMessage += "End Date is required!\n";
        }
        if (paymentDatePicker.getValue() == null) {
            errorMessage += "Payment Date is required!\n";
        }
        if (statusComboBox.getSelectionModel().isEmpty()) {
            errorMessage += "Payment Status is required!\n";
        }
        try {
            double amount = Double.parseDouble(amountPaidField.getText());
            if (amount <= 0) errorMessage += "Amount must be greater than zero!\n";
        } catch (NumberFormatException e) {
            errorMessage += "Amount Paid must be a valid number!\n";
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