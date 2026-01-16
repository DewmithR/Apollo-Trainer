package model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Membership {

    // MembershipID (INT PRIMARY KEY IDENTITY)
    private final IntegerProperty membershipID;

    // MemberID (VARCHAR)
    private final StringProperty memberID;

    // StartDate, EndDate, PaymentDate (DATE)
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final ObjectProperty<LocalDate> paymentDate;

    // MembershipType (VARCHAR, the plan name)
    private final StringProperty membershipType;

    // PaymentAmount (DECIMAL)
    private final DoubleProperty paymentAmount;

    // PaymentStatus (VARCHAR)
    private final StringProperty paymentStatus;

    // Constructor for retrieving existing records
    public Membership(int membershipID, String memberID, LocalDate startDate, LocalDate endDate,
                      String membershipType, double paymentAmount, LocalDate paymentDate, String paymentStatus) {
        this.membershipID = new SimpleIntegerProperty(membershipID);
        this.memberID = new SimpleStringProperty(memberID);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.membershipType = new SimpleStringProperty(membershipType);
        this.paymentAmount = new SimpleDoubleProperty(paymentAmount);
        this.paymentDate = new SimpleObjectProperty<>(paymentDate);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
    }

    // Constructor for new records (ID is auto-generated)
    public Membership(String memberID, LocalDate startDate, LocalDate endDate, String membershipType,
                      double paymentAmount, LocalDate paymentDate, String paymentStatus) {
        this(0, memberID, startDate, endDate, membershipType, paymentAmount, paymentDate, paymentStatus);
    }

    // Getters for Properties (for TableView binding)
    public IntegerProperty membershipIDProperty() { return membershipID; }
    public StringProperty memberIDProperty() { return memberID; }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }
    public ObjectProperty<LocalDate> endDateProperty() { return endDate; }
    public StringProperty membershipTypeProperty() { return membershipType; }
    public DoubleProperty paymentAmountProperty() { return paymentAmount; }
    public ObjectProperty<LocalDate> paymentDateProperty() { return paymentDate; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }

    // Getters and Setters (Partial list for manipulation)
    public int getMembershipID() { return membershipID.get(); }

    public String getMemberID() { return memberID.get(); }
    public void setMemberID(String memberID) { this.memberID.set(memberID); }

    public LocalDate getStartDate() { return startDate.get(); }
    public void setStartDate(LocalDate startDate) { this.startDate.set(startDate); }

    public String getMembershipType() { return membershipType.get(); }
    public void setMembershipType(String membershipType) { this.membershipType.set(membershipType); }

    public double getPaymentAmount() { return paymentAmount.get(); }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount.set(paymentAmount); }

    public LocalDate getEndDate() { return endDate.get(); }
    public void setEndDate(LocalDate endDate) { this.endDate.set(endDate); }

    public LocalDate getPaymentDate() { return paymentDate.get(); }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate.set(paymentDate); }

    public String getPaymentStatus() { return paymentStatus.get(); }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus.set(paymentStatus); }
}