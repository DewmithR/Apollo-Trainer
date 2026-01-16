package model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Payment {

    private final StringProperty paymentID;
    private final StringProperty memberID;
    private final StringProperty memberFullName;
    private final StringProperty membershipTypeID;
    private final ObjectProperty<LocalDate> paymentDate;
    private final DoubleProperty amountPaid;
    private final StringProperty paymentMethod;

    public Payment(String paymentID, String memberID, String memberFullName, String membershipTypeID,
                   LocalDate paymentDate, double amountPaid, String paymentMethod) {
        this.paymentID = new SimpleStringProperty(paymentID);
        this.memberID = new SimpleStringProperty(memberID);
        this.memberFullName = new SimpleStringProperty(memberFullName);
        this.membershipTypeID = new SimpleStringProperty(membershipTypeID);
        this.paymentDate = new SimpleObjectProperty<>(paymentDate);
        this.amountPaid = new SimpleDoubleProperty(amountPaid);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
    }

    // Getters for Properties
    public StringProperty paymentIDProperty() { return paymentID; }
    public StringProperty memberIDProperty() { return memberID; }
    public StringProperty memberFullNameProperty() { return memberFullName; }
    public StringProperty membershipTypeIDProperty() { return membershipTypeID; }
    public ObjectProperty<LocalDate> paymentDateProperty() { return paymentDate; }
    public DoubleProperty amountPaidProperty() { return amountPaid; }
    public StringProperty paymentMethodProperty() { return paymentMethod; }

    // Getters and Setters
    public String getPaymentID() { return paymentID.get(); }
    public String getMemberID() { return memberID.get(); }
    public String getMembershipTypeID() { return membershipTypeID.get(); }
    public LocalDate getPaymentDate() { return paymentDate.get(); }
    public double getAmountPaid() { return amountPaid.get(); }
    public String getPaymentMethod() { return paymentMethod.get(); }

    public void setPaymentID(String paymentID) { this.paymentID.set(paymentID); }
}