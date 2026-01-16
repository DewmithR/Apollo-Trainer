package model;

import javafx.beans.property.*;

public class WorkoutPlan {

    private final IntegerProperty planID;
    private final StringProperty planName;
    private final StringProperty description;

    public WorkoutPlan(int planID, String planName, String description) {
        this.planID = new SimpleIntegerProperty(planID);
        this.planName = new SimpleStringProperty(planName);
        this.description = new SimpleStringProperty(description);
    }

    // Constructor for creating new plans (ID is auto-generated)
    public WorkoutPlan(String planName, String description) {
        this(0, planName, description);
    }

    // Properties for TableView binding
    public IntegerProperty planIDProperty() { return planID; }
    public StringProperty planNameProperty() { return planName; }
    public StringProperty descriptionProperty() { return description; }

    //Getters and Setters
    public int getPlanID() { return planID.get(); }
    public void setPlanID(int planID) { this.planID.set(planID); }

    public String getPlanName() { return planName.get(); }
    public void setPlanName(String planName) { this.planName.set(planName); }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    @Override
    public String toString() {
        return "P" + getPlanID() + " | " + getPlanName();
    }
}