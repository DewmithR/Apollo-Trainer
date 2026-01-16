package model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Member {
    // 1. Encapsulation: Use JavaFX Properties for UI binding
    private final StringProperty memberId;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty contactNumber;
    private final StringProperty email;
    private final ObjectProperty<LocalDate> dateOfBirth;
    private final ObjectProperty<LocalDate> joiningDate;
    private final StringProperty address;

    // Constructor used when retrieving data from the database
    public Member(String memberId, String firstName, String lastName, String contactNumber,
                  String email, LocalDate dateOfBirth, LocalDate joiningDate, String address) {
        this.memberId = new SimpleStringProperty(memberId);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.contactNumber = new SimpleStringProperty(contactNumber);
        this.email = new SimpleStringProperty(email);
        this.dateOfBirth = new SimpleObjectProperty<>(dateOfBirth);
        this.joiningDate = new SimpleObjectProperty<>(joiningDate);
        this.address = new SimpleStringProperty(address);
    }

    // Constructor used when creating a new member (ID is often generated/assigned by the system)
    public Member(String firstName, String lastName, String contactNumber,
                  String email, LocalDate dateOfBirth, LocalDate joiningDate, String address) {
        this(null, firstName, lastName, contactNumber, email, dateOfBirth, joiningDate, address);
    }

    // 2. Public Getters for Properties (Essential for TableView binding)
    public StringProperty memberIdProperty() { return memberId; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty contactNumberProperty() { return contactNumber; }
    public StringProperty emailProperty() { return email; }
    public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }
    public ObjectProperty<LocalDate> joiningDateProperty() { return joiningDate; }
    public StringProperty addressProperty() { return address; }

    // 3. Simple Getters and Setters (for easy data manipulation)
    public String getMemberId() { return memberId.get(); }
    public void setMemberId(String memberId) { this.memberId.set(memberId); }

    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }

    public String getLastName() { return lastName.get(); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }

    public String getContactNumber() { return contactNumber.get(); }
    public void setContactNumber(String contactNumber) { this.contactNumber.set(contactNumber); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth.set(dateOfBirth); }

    public LocalDate getJoiningDate() { return joiningDate.get(); }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate.set(joiningDate); }

    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }

    // Utility method for full name
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}