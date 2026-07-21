package org.example.divar.model;

import java.util.ArrayList;

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
    private String blockReason;
    private double averageRating;
    private ArrayList<Advertisement> favoriteAds = new ArrayList<>();

    public User() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password != null && this.password.equals(password);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getFullName() {
        boolean hasFirst = firstname != null && !firstname.isBlank();
        boolean hasLast = lastname != null && !lastname.isBlank();

        if (hasFirst && hasLast) {
            return firstname + " " + lastname;
        } else if (hasFirst) {
            return firstname;
        } else if (hasLast) {
            return lastname;
        }

        return "-";
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

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public ArrayList<Advertisement> getFavoriteAds() {
        return favoriteAds;
    }

    public void addFavoriteAdvertisement(Advertisement advertisement) {
        if (!favoriteAds.contains(advertisement)) {
            favoriteAds.add(advertisement);
        }
    }

    public void removeFavoriteAdvertisement(Advertisement advertisement) {
        favoriteAds.remove(advertisement);
    }

    public boolean isFavoriteAdvertisement(Advertisement advertisement) {
        return favoriteAds.contains(advertisement);
    }
}

