package model;

public class User {
    // 1. Encapsulation: Fields are private
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String role; // e.g., Admin, Instructor
    private boolean isActive;

    // Constructor
    public User(int userId, String username, String firstName, String lastName, String role, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = isActive;
    }

    // 2. Encapsulation: Public Getters and Setters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }

    // Setters (only provide setters for fields that can be modified)
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setRole(String role) { this.role = role; }
    public void setActive(boolean active) { isActive = active; }
    // Note: Username and ID are typically not changed, so no setters for them.
}