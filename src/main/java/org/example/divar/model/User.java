package org.example.divar.model;

public class User {

    private String id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;
    private UserRole role;
    private UserStatus status;
    private double averageRating;

    public User() {
        this.status = UserStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setAverageRating(double sellerRating) {
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getFullName() {
        if (firstname == null && lastname == null) {
            return username;
        }

        String fName = "";
        if (firstname != null) {
            fName = firstname;
        } else {
            fName = "";
        }

        String lName = "";
        if (lastname != null) {
            lName = lastname;
        } else {
            lName = "";
        }

        String fullName = fName + " " + lName;
        return fullName.trim();
    }
}




