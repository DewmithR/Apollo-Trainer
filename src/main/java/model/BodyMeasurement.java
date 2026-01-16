package model;

import javafx.beans.property.*;
import java.time.LocalDate; // Keep LocalDate for the record date if you add it later, but remove fields for now

public class BodyMeasurement {

    private final IntegerProperty measurementID;
    private final StringProperty memberID;
    private final StringProperty memberName; // Helper for UI display
    // Keep RecordDate property placeholder (assuming you will add it to the DB later)
    private final ObjectProperty<LocalDate> recordDate = new SimpleObjectProperty<>(LocalDate.now());

    // Measurement Values (Matching your schema)
    private final DoubleProperty weight;           // Renamed from WeightKg
    private final DoubleProperty height;           // New field
    private final DoubleProperty bmi;              // New field
    private final DoubleProperty bodyFatPercentage; // Renamed from BodyFatPercent

    // Full Constructor for existing records
    public BodyMeasurement(int measurementID, String memberID, String memberName, double weight, double height, double bmi, double bodyFatPercentage) {
        this.measurementID = new SimpleIntegerProperty(measurementID);
        this.memberID = new SimpleStringProperty(memberID);
        this.memberName = new SimpleStringProperty(memberName);

        this.weight = new SimpleDoubleProperty(weight);
        this.height = new SimpleDoubleProperty(height);
        this.bmi = new SimpleDoubleProperty(bmi);
        this.bodyFatPercentage = new SimpleDoubleProperty(bodyFatPercentage);
    }

    // Constructor for new records (ID is auto-generated)
    public BodyMeasurement(String memberID, double weight, double height, double bmi, double bodyFatPercentage) {
        this(0, memberID, null, weight, height, bmi, bodyFatPercentage);
    }

    // --- Properties for TableView binding ---
    public IntegerProperty measurementIDProperty() { return measurementID; }
    public StringProperty memberIDProperty() { return memberID; }
    public StringProperty memberNameProperty() { return memberName; }
    // We will keep recordDate property for future proofing/optional use
    public ObjectProperty<LocalDate> recordDateProperty() { return recordDate; }

    public DoubleProperty weightProperty() { return weight; }
    public DoubleProperty heightProperty() { return height; }
    public DoubleProperty bmiProperty() { return bmi; }
    public DoubleProperty bodyFatPercentageProperty() { return bodyFatPercentage; }

    // --- Simple Getters and Setters ---
    public int getMeasurementID() { return measurementID.get(); }
    public void setMeasurementID(int measurementID) { this.measurementID.set(measurementID); }
    public String getMemberID() { return memberID.get(); }

    public double getWeight() { return weight.get(); }
    public double getHeight() { return height.get(); }
    public double getBmi() { return bmi.get(); }
    public double getBodyFatPercentage() { return bodyFatPercentage.get(); }

    public void setWeight(double weight) { this.weight.set(weight); }
    public void setHeight(double height) { this.height.set(height); }
    public void setBmi(double bmi) { this.bmi.set(bmi); }
    public void setBodyFatPercentage(double bodyFatPercentage) { this.bodyFatPercentage.set(bodyFatPercentage); }
}