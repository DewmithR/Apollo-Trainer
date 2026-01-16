package model;

import javafx.beans.property.*;

public class SystemUser {

    // Corresponds to UserID (INT PRIMARY KEY IDENTITY)
    private final IntegerProperty userID;

    // Corresponds to Username, PasswordHash, FirstName, LastName, Role (VARCHAR)
    private final StringProperty username;
    private final StringProperty passwordHash; // Used for initial display/setting, though a secure app stores only the hash
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty role;

    // Corresponds to IsActive (BIT)
    private final BooleanProperty isActive;

    // Full Constructor for retrieving existing records
    public SystemUser(int userID, String username, String passwordHash, String firstName, String lastName, String role, boolean isActive) {
        this.userID = new SimpleIntegerProperty(userID);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.role = new SimpleStringProperty(role);
        this.isActive = new SimpleBooleanProperty(isActive);
    }

    // Constructor for new records (ID is auto-generated)
    // Note: We include passwordHash here as the DAO will handle hashing before insertion
    public SystemUser(String username, String passwordHash, String firstName, String lastName, String role) {
        this(0, username, passwordHash, firstName, lastName, role, true); // New users are active by default
    }

    // --- Getters for Properties (Essential for TableView binding) ---
    public IntegerProperty userIDProperty() { return userID; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty roleProperty() { return role; }
    public BooleanProperty isActiveProperty() { return isActive; }

    // --- Simple Getters and Setters ---
    public int getUserID() { return userID.get(); }
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String passwordHash) { this.passwordHash.set(passwordHash); }
    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    public String getLastName() { return lastName.get(); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    public boolean getIsActive() { return isActive.get(); }
    public void setIsActive(boolean isActive) { this.isActive.set(isActive); }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
    public void setUserID(int userID) {
        this.userID.set(userID);
    }
}