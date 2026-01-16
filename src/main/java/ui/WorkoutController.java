package ui;

import db.MemberDAO;
import db.MemberWorkoutDAO;
import db.WorkoutPlanDAO;
import model.Member;
import model.MemberWorkout;
import model.WorkoutPlan;
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

public class WorkoutController {

    // DAOs
    private final WorkoutPlanDAO planDAO = new WorkoutPlanDAO();
    private final MemberWorkoutDAO assignmentDAO = new MemberWorkoutDAO();
    private final MemberDAO memberDAO = new MemberDAO(); // Reusing the existing MemberDAO

    // Data Lists
    private final ObservableList<WorkoutPlan> masterPlanList = FXCollections.observableArrayList();
    private final ObservableList<MemberWorkout> masterAssignmentList = FXCollections.observableArrayList();
    private final ObservableList<Member> memberLookupList = FXCollections.observableArrayList();

    // FXML Components (Plan Library Tab)
    @FXML private TableView<WorkoutPlan> planTable;
    @FXML private TableColumn<WorkoutPlan, Integer> planIDColumn;
    @FXML private TableColumn<WorkoutPlan, String> planNameColumn;
    @FXML private TableColumn<WorkoutPlan, String> planDescriptionColumn;

    @FXML private TextField planIDField;
    @FXML private TextField planNameField;
    @FXML private TextArea planDescriptionArea;
    @FXML private Button updatePlanButton;
    @FXML private Button deletePlanButton;
    @FXML private Label planStatusLabel;

    // FXML Components (Assignment Tracking Tab)
    @FXML private TableView<MemberWorkout> assignmentTable;
    @FXML private TableColumn<MemberWorkout, Integer> assignmentIDColumn;
    @FXML private TableColumn<MemberWorkout, String> assignedMemberIDColumn;
    @FXML private TableColumn<MemberWorkout, String> assignedMemberNameColumn;
    @FXML private TableColumn<MemberWorkout, Integer> assignedPlanIDColumn;
    @FXML private TableColumn<MemberWorkout, String> assignedPlanNameColumn;
    @FXML private TableColumn<MemberWorkout, LocalDate> assignedDateColumn;

    @FXML private ComboBox<Member> memberComboBox;
    @FXML private ComboBox<WorkoutPlan> planComboBox;
    @FXML private DatePicker assignedDatePicker;
    @FXML private Button deleteAssignmentButton;
    @FXML private Label assignmentStatusLabel;


    @FXML
    public void initialize() {
        // Setup TableViews
        setupPlanTable();
        setupAssignmentTable();

        // Load Data
        loadAllData();

        // Event Listeners
        planTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showPlanDetails(newVal));

        assignmentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> deleteAssignmentButton.setDisable(newVal == null));

        // Bindings & Defaults
        updatePlanButton.disableProperty().bind(Bindings.isEmpty(planTable.getSelectionModel().getSelectedItems()));
        deletePlanButton.disableProperty().bind(Bindings.isEmpty(planTable.getSelectionModel().getSelectedItems()));
        assignedDatePicker.setValue(LocalDate.now());

        // Set ComboBox display formats (reusing logic from MembershipController)
        memberComboBox.setCellFactory(lv -> new ListCell<Member>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getMemberId() + " | " + item.getFullName());
            }
        });
        memberComboBox.setButtonCell(memberComboBox.getCellFactory().call(null));

        planComboBox.setCellFactory(lv -> new ListCell<WorkoutPlan>() {
            @Override
            protected void updateItem(WorkoutPlan item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : "P" + item.getPlanID() + " | " + item.getPlanName());
            }
        });
        planComboBox.setButtonCell(planComboBox.getCellFactory().call(null));
    }

    // Setup Methods
    private void setupPlanTable() {
        planIDColumn.setCellValueFactory(new PropertyValueFactory<>("planID"));
        planNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));
        planDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        planTable.setItems(masterPlanList);
    }

    private void setupAssignmentTable() {
        assignmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentID"));
        assignedMemberIDColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        assignedMemberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        assignedPlanIDColumn.setCellValueFactory(new PropertyValueFactory<>("planID"));
        assignedPlanNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));
        assignedDateColumn.setCellValueFactory(new PropertyValueFactory<>("assignedDate"));
        assignmentTable.setItems(masterAssignmentList);
    }

    private void loadAllData() {
        // Load plan data
        masterPlanList.clear();
        masterPlanList.addAll(planDAO.getAllPlans());
        planComboBox.setItems(masterPlanList);

        // Load member data for ComboBox lookup
        memberLookupList.clear();
        memberLookupList.addAll(memberDAO.getAllMembers());
        memberComboBox.setItems(memberLookupList);

        // Load assignment records
        masterAssignmentList.clear();
        masterAssignmentList.addAll(assignmentDAO.getAllAssignments());
        planStatusLabel.setText("Loaded " + masterPlanList.size() + " workout plans.");
        assignmentStatusLabel.setText("Loaded " + masterAssignmentList.size() + " member assignments.");
    }

    // Workout Plan CRUD Handlers
    private void showPlanDetails(WorkoutPlan plan) {
        if (plan != null) {
            planIDField.setText(String.valueOf(plan.getPlanID()));
            planNameField.setText(plan.getPlanName());
            planDescriptionArea.setText(plan.getDescription());
        } else {
            handleClearPlanForm();
        }
    }

    @FXML
    private void handleClearPlanForm() {
        planIDField.clear();
        planNameField.clear();
        planDescriptionArea.clear();
        planTable.getSelectionModel().clearSelection();
        planStatusLabel.setText("Ready to add new plan.");
    }

    @FXML
    private void handleAddPlan() {
        if (isPlanInputValid()) {
            WorkoutPlan newPlan = new WorkoutPlan(
                    planNameField.getText(),
                    planDescriptionArea.getText()
            );
            if (planDAO.addPlan(newPlan)) {
                masterPlanList.add(newPlan);
                planStatusLabel.setText("✅ Plan added: " + newPlan.getPlanName());
                handleClearPlanForm();
                planComboBox.setItems(masterPlanList); // Refresh Plan ComboBox
            } else {
                planStatusLabel.setText("❌ Error: Could not add plan.");
            }
        }
    }

    @FXML
    private void handleUpdatePlan() {
        WorkoutPlan selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null && isPlanInputValid()) {
            selectedPlan.setPlanName(planNameField.getText());
            selectedPlan.setDescription(planDescriptionArea.getText());

            if (planDAO.updatePlan(selectedPlan)) {
                planTable.refresh();
                planStatusLabel.setText("🔄 Plan updated: " + selectedPlan.getPlanName());
                handleClearPlanForm();
            } else {
                planStatusLabel.setText("❌ Error: Could not update plan.");
            }
        }
    }

    @FXML
    private void handleDeletePlan() {
        WorkoutPlan selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            if (planDAO.deletePlan(selectedPlan.getPlanID())) {
                masterPlanList.remove(selectedPlan);
                planStatusLabel.setText("🗑️ Plan deleted: " + selectedPlan.getPlanName());
                handleClearPlanForm();
                planComboBox.setItems(masterPlanList); // Refresh Plan ComboBox
            } else {
                planStatusLabel.setText("❌ Error: Could not delete plan. Check for existing member assignments linked to this plan.");
            }
        }
    }

    // Assignment Handlers
    @FXML
    private void handleClearAssignmentForm() {
        memberComboBox.getSelectionModel().clearSelection();
        planComboBox.getSelectionModel().clearSelection();
        assignedDatePicker.setValue(LocalDate.now());
        assignmentTable.getSelectionModel().clearSelection();
        assignmentStatusLabel.setText("Ready to assign new plan.");
    }

    @FXML
    private void handleAssignPlan() {
        if (isAssignmentInputValid()) {
            Member member = memberComboBox.getSelectionModel().getSelectedItem();
            WorkoutPlan plan = planComboBox.getSelectionModel().getSelectedItem();

            MemberWorkout newAssignment = new MemberWorkout(
                    member.getMemberId(),
                    plan.getPlanID(),
                    assignedDatePicker.getValue()
            );

            if (assignmentDAO.assignPlan(newAssignment)) {
                loadAllData(); // Reload to get the new ID and names
                assignmentStatusLabel.setText("✅ Plan '" + plan.getPlanName() + "' assigned to " + member.getFullName() + ".");
                handleClearAssignmentForm();
            } else {
                assignmentStatusLabel.setText("❌ Error: Could not assign plan. (Possible duplicate assignment on the same date)");
            }
        }
    }

    @FXML
    private void handleDeleteAssignment() {
        MemberWorkout selectedAssignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null) {
            int assignmentId = selectedAssignment.getAssignmentID();

            if (assignmentDAO.deleteAssignment(assignmentId)) {
                masterAssignmentList.remove(selectedAssignment);
                assignmentStatusLabel.setText("🗑️ Assignment ID " + assignmentId + " deleted.");
            } else {
                assignmentStatusLabel.setText("❌ Error: Could not delete assignment.");
            }
        }
    }

    // Validation Methods
    private boolean isPlanInputValid() {
        String errorMessage = "";
        if (planNameField.getText() == null || planNameField.getText().trim().isEmpty()) {
            errorMessage += "Plan Name cannot be empty!\n";
        }
        if (errorMessage.isEmpty()) { return true; }
        planStatusLabel.setText("❌ Validation Error:\n" + errorMessage);
        return false;
    }

    private boolean isAssignmentInputValid() {
        String errorMessage = "";
        if (memberComboBox.getSelectionModel().isEmpty()) {
            errorMessage += "Must select a Member!\n";
        }
        if (planComboBox.getSelectionModel().isEmpty()) {
            errorMessage += "Must select a Workout Plan!\n";
        }
        if (assignedDatePicker.getValue() == null) {
            errorMessage += "Assigned Date is required!\n";
        }

        if (errorMessage.isEmpty()) { return true; }
        assignmentStatusLabel.setText("❌ Validation Error:\n" + errorMessage);
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