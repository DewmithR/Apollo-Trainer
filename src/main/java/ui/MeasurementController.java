package ui;

import db.BodyMeasurementDAO;
import db.MemberDAO;
import model.BodyMeasurement;
import model.Member;
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
import java.util.Optional;

public class MeasurementController {

    private final BodyMeasurementDAO measurementDAO = new BodyMeasurementDAO();
    private final MemberDAO memberDAO = new MemberDAO();

    private final ObservableList<BodyMeasurement> masterMeasurementList = FXCollections.observableArrayList();
    private final ObservableList<Member> memberLookupList = FXCollections.observableArrayList();

    // --- FXML Components (Table) ---
    @FXML private TableView<BodyMeasurement> measurementTable;
    @FXML private TableColumn<BodyMeasurement, Integer> idColumn;
    @FXML private TableColumn<BodyMeasurement, String> memberIDColumn;
    @FXML private TableColumn<BodyMeasurement, String> memberNameColumn;
    // @FXML private TableColumn<BodyMeasurement, LocalDate> dateColumn; // Removed Date Column
    @FXML private TableColumn<BodyMeasurement, Double> weightColumn;
    @FXML private TableColumn<BodyMeasurement, Double> heightColumn; // New Column
    @FXML private TableColumn<BodyMeasurement, Double> bmiColumn;    // New Column
    @FXML private TableColumn<BodyMeasurement, Double> bodyFatColumn;

    // --- FXML Components (Form) ---
    @FXML private TextField measurementIDField;
    @FXML private ComboBox<Member> memberComboBox;
    // @FXML private DatePicker datePicker; // Removed Date Picker
    @FXML private TextField weightField;
    @FXML private TextField heightField; // New Field
    @FXML private TextField bmiField;    // New Field
    @FXML private TextField bodyFatField;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;


    @FXML
    public void initialize() {
        // --- Setup TableView ---
        idColumn.setCellValueFactory(new PropertyValueFactory<>("measurementID"));
        memberIDColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        // dateColumn.setCellValueFactory(new PropertyValueFactory<>("recordDate")); // Removed
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        bmiColumn.setCellValueFactory(new PropertyValueFactory<>("bmi"));
        bodyFatColumn.setCellValueFactory(new PropertyValueFactory<>("bodyFatPercentage"));
        measurementTable.setItems(masterMeasurementList);

        // --- Load Data ---
        loadAllData();

        // --- Event Listeners and Bindings ---
        measurementTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showMeasurementDetails(newVal));

        updateButton.disableProperty().bind(Bindings.isEmpty(measurementTable.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(measurementTable.getSelectionModel().getSelectedItems()));

        // Setup Member ComboBox display format (reusing logic)
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
        // Load measurements
        masterMeasurementList.clear();
        masterMeasurementList.addAll(measurementDAO.getAllMeasurements());

        // Load members for ComboBox lookup
        memberLookupList.clear();
        memberLookupList.addAll(memberDAO.getAllMembers());
        memberComboBox.setItems(memberLookupList);

        statusLabel.setText("Loaded " + masterMeasurementList.size() + " body measurement records.");
    }

    private void showMeasurementDetails(BodyMeasurement record) {
        if (record != null) {
            measurementIDField.setText(String.valueOf(record.getMeasurementID()));
            weightField.setText(String.valueOf(record.getWeight()));
            heightField.setText(String.valueOf(record.getHeight()));
            bmiField.setText(String.valueOf(record.getBmi()));
            bodyFatField.setText(String.valueOf(record.getBodyFatPercentage()));

            // Select the member in the ComboBox
            Optional<Member> selectedMember = memberLookupList.stream()
                    .filter(m -> m.getMemberId().equals(record.getMemberID()))
                    .findFirst();
            selectedMember.ifPresent(memberComboBox.getSelectionModel()::select);

        } else {
            handleClearForm();
        }
    }

    // --- CRUD Handlers ---
    @FXML
    private void handleClearForm() {
        measurementIDField.clear();
        memberComboBox.getSelectionModel().clearSelection();
        weightField.clear();
        heightField.clear();
        bmiField.clear();
        bodyFatField.clear();
        measurementTable.getSelectionModel().clearSelection();
        statusLabel.setText("Ready to add new measurement record.");
    }

    @FXML
    private void handleAddMeasurement() {
        if (isInputValid()) {
            Member selectedMember = memberComboBox.getSelectionModel().getSelectedItem();

            BodyMeasurement newRecord = new BodyMeasurement(
                    selectedMember.getMemberId(),
                    Double.parseDouble(weightField.getText()),
                    Double.parseDouble(heightField.getText()),
                    Double.parseDouble(bmiField.getText()),
                    Double.parseDouble(bodyFatField.getText())
            );

            if (measurementDAO.addMeasurement(newRecord)) {
                loadAllData(); // Reload to get member names and ID
                statusLabel.setText("✅ Measurement added for " + selectedMember.getFullName() + ".");
                handleClearForm();
            } else {
                statusLabel.setText("❌ Error: Could not add measurement.");
            }
        }
    }

    @FXML
    private void handleUpdateMeasurement() {
        BodyMeasurement selectedRecord = measurementTable.getSelectionModel().getSelectedItem();
        if (selectedRecord != null && isInputValid()) {

            // Update model properties
            selectedRecord.setWeight(Double.parseDouble(weightField.getText()));
            selectedRecord.setHeight(Double.parseDouble(heightField.getText()));
            selectedRecord.setBmi(Double.parseDouble(bmiField.getText()));
            selectedRecord.setBodyFatPercentage(Double.parseDouble(bodyFatField.getText()));

            if (measurementDAO.updateMeasurement(selectedRecord)) {
                measurementTable.refresh();
                statusLabel.setText("🔄 Record ID " + selectedRecord.getMeasurementID() + " updated.");
                handleClearForm();
                loadAllData();
            } else {
                statusLabel.setText("❌ Error: Could not update record.");
            }
        }
    }

    @FXML
    private void handleDeleteMeasurement() {
        // ... (Deletion logic remains the same)
        BodyMeasurement selectedRecord = measurementTable.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            int recordId = selectedRecord.getMeasurementID();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete record ID " + recordId + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (measurementDAO.deleteMeasurement(recordId)) {
                    masterMeasurementList.remove(selectedRecord);
                    statusLabel.setText("🗑️ Record ID " + recordId + " deleted.");
                    handleClearForm();
                } else {
                    statusLabel.setText("❌ Error: Could not delete record.");
                }
            }
        }
    }

    // --- Validation Methods ---
    private boolean isInputValid() {
        String errorMessage = "";

        if (memberComboBox.getSelectionModel().isEmpty()) {
            errorMessage += "Must select a Member!\n";
        }

        // Simple numeric validation for all measurement fields
        if (!isNumeric(weightField.getText())) { errorMessage += "Weight must be a valid number.\n"; }
        if (!isNumeric(heightField.getText())) { errorMessage += "Height must be a valid number.\n"; }
        if (!isNumeric(bmiField.getText())) { errorMessage += "BMI must be a valid number.\n"; }
        if (!isNumeric(bodyFatField.getText())) { errorMessage += "Body Fat must be a valid number.\n"; }

        if (errorMessage.isEmpty()) { return true; }
        statusLabel.setText("❌ Validation Error:\n" + errorMessage);
        return false;
    }

    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) { return false; }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // --- Navigation Handler ---
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        // ... (Navigation code remains the same)
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