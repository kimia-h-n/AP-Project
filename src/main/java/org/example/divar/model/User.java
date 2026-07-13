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
    private ArrayList<Advertisement> favoriteAds = new ArrayList<>();

    public User() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isPasswordCorrect(String password) {
        if (this.password != null && this.password.equals(password)){
            return true;
        }
        return false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public ArrayList<Advertisement> getFavoriteAds() {
        return favoriteAds;
    }

    public void addFavoriteAdvertisement(Advertisement advertisement) {
        if (!favoriteAds.contains(advertisement)) {
            favoriteAds.add(advertisement);
        }
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void removeFavoriteAdvertisement(Advertisement advertisement) {
        favoriteAds.remove(advertisement);
    }

    public boolean isFavoriteAdvertisement(Advertisement advertisement) {
        return favoriteAds.contains(advertisement);
    }
}


