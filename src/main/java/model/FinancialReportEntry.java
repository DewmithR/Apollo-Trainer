package model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class FinancialReportEntry {

    private final StringProperty memberID;
    private final StringProperty memberName;
    private final StringProperty membershipType;
    private final ObjectProperty<LocalDate> paymentDate;
    private final DoubleProperty paymentAmount;
    private final StringProperty paymentStatus;

    public FinancialReportEntry(String memberID, String memberName, String membershipType, LocalDate paymentDate, double paymentAmount, String paymentStatus) {
        this.memberID = new SimpleStringProperty(memberID);
        this.memberName = new SimpleStringProperty(memberName);
        this.membershipType = new SimpleStringProperty(membershipType);
        this.paymentDate = new SimpleObjectProperty<>(paymentDate);
        this.paymentAmount = new SimpleDoubleProperty(paymentAmount);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
    }

    // Properties for TableView binding
    public StringProperty memberIDProperty() { return memberID; }
    public StringProperty memberNameProperty() { return memberName; }
    public StringProperty membershipTypeProperty() { return membershipType; }
    public ObjectProperty<LocalDate> paymentDateProperty() { return paymentDate; }
    public DoubleProperty paymentAmountProperty() { return paymentAmount; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }

    public double getPaymentAmount() { return paymentAmount.get(); }
}