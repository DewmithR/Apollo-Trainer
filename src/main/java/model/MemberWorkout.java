package model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class MemberWorkout {

    private final IntegerProperty assignmentID;
    private final StringProperty memberID;
    private final IntegerProperty planID;
    private final StringProperty memberName; // Helper for UI display
    private final StringProperty planName;   // Helper for UI display
    private final ObjectProperty<LocalDate> assignedDate;

    // Standard constructor
    public MemberWorkout(int assignmentID, String memberID, int planID, String memberName, String planName, LocalDate assignedDate) {
        this.assignmentID = new SimpleIntegerProperty(assignmentID);
        this.memberID = new SimpleStringProperty(memberID);
        this.planID = new SimpleIntegerProperty(planID);
        this.memberName = new SimpleStringProperty(memberName);
        this.planName = new SimpleStringProperty(planName);
        this.assignedDate = new SimpleObjectProperty<>(assignedDate);
    }

    // Constructor for new assignments (ID is auto-generated)
    public MemberWorkout(String memberID, int planID, LocalDate assignedDate) {
        this(0, memberID, planID, null, null, assignedDate);
    }

    // Properties for TableView binding
    public IntegerProperty assignmentIDProperty() { return assignmentID; }
    public StringProperty memberIDProperty() { return memberID; }
    public IntegerProperty planIDProperty() { return planID; }
    public StringProperty memberNameProperty() { return memberName; }
    public StringProperty planNameProperty() { return planName; }
    public ObjectProperty<LocalDate> assignedDateProperty() { return assignedDate; }

    // Simple Getters and Setters
    public int getAssignmentID() { return assignmentID.get(); }
    public void setAssignmentID(int assignmentID) { this.assignmentID.set(assignmentID); }

    public String getMemberID() { return memberID.get(); }
    public int getPlanID() { return planID.get(); }
    public LocalDate getAssignedDate() { return assignedDate.get(); }
    public String getMemberName() { return memberName.get(); }
    public String getPlanName() { return planName.get(); }
}