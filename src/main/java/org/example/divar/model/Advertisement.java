package org.example.divar.model;

import java.util.ArrayList;

public class Advertisement {
    private long id;
    private boolean favorite;
    private String title;
    private String description;
    private String address;
    private long price;
    private Category category;
    private ProductCondition condition;
    private City city;
    private AdvertisementStatus status;
    private ArrayList<String> imagePaths;
    private User buyer;
    private User seller;

    public Advertisement() {
        this.imagePaths = new ArrayList<>();

    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }

    public void setStatus(AdvertisementStatus status) {
        this.status = status;
    }

    public long getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public ProductCondition getCondition() {
        return condition;
    }

    public AdvertisementStatus getStatus() {
        return status;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public long getId() {
        return id; }

    public void setId(long id) {
        this.id = id; }

    public boolean isFavorite() {
        return favorite; }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite; }
}

