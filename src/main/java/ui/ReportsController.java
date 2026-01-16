package ui;

import db.ReportDAO;
import model.MemberReportEntry;
import model.FinancialReportEntry;
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
import java.text.DecimalFormat;
import java.time.LocalDate;

public class ReportsController {

    private final ReportDAO reportDAO = new ReportDAO();
    private final DecimalFormat currencyFormat = new DecimalFormat("¤#,##0.00"); // Use local currency symbol if needed

    // --- Member Report Components ---
    @FXML private TableView<MemberReportEntry> memberReportTable;
    @FXML private TableColumn<MemberReportEntry, String> mrMemberIDColumn;
    @FXML private TableColumn<MemberReportEntry, String> mrFullNameColumn;
    @FXML private TableColumn<MemberReportEntry, String> mrMembershipTypeColumn;
    @FXML private TableColumn<MemberReportEntry, LocalDate> mrStartDateColumn;
    @FXML private TableColumn<MemberReportEntry, LocalDate> mrEndDateColumn;
    @FXML private TableColumn<MemberReportEntry, String> mrPaymentStatusColumn;

    // --- Financial Report Components ---
    @FXML private TableView<FinancialReportEntry> financialReportTable;
    @FXML private TableColumn<FinancialReportEntry, String> frMemberIDColumn;
    @FXML private TableColumn<FinancialReportEntry, String> frMemberNameColumn;
    @FXML private TableColumn<FinancialReportEntry, String> frMembershipTypeColumn;
    @FXML private TableColumn<FinancialReportEntry, LocalDate> frPaymentDateColumn;
    @FXML private TableColumn<FinancialReportEntry, Double> frPaymentAmountColumn;
    @FXML private TableColumn<FinancialReportEntry, String> frPaymentStatusColumn;
    @FXML private Label totalRevenueLabel;


    @FXML
    public void initialize() {
        // --- Setup Member Report Table ---
        mrMemberIDColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        mrFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        mrMembershipTypeColumn.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        mrStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        mrEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        mrPaymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // --- Setup Financial Report Table ---
        frMemberIDColumn.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        frMemberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        frMembershipTypeColumn.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        frPaymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        frPaymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        frPaymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // Format the currency column
        frPaymentAmountColumn.setCellFactory(tc -> new TableCell<FinancialReportEntry, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(amount));
                }
            }
        });

        // --- Load Data ---
        loadReportsData();
    }

    private void loadReportsData() {
        // Load Member Report
        ObservableList<MemberReportEntry> memberData = FXCollections.observableArrayList(reportDAO.getMemberReport());
        memberReportTable.setItems(memberData);

        // Load Financial Report
        ObservableList<FinancialReportEntry> financialData = FXCollections.observableArrayList(reportDAO.getFinancialReport());
        financialReportTable.setItems(financialData);

        // Calculate and display Total Revenue
        double totalRevenue = reportDAO.getTotalRevenue();
        totalRevenueLabel.setText(currencyFormat.format(totalRevenue));
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load dashboard view.");
            alert.showAndWait();
        }
    }
}