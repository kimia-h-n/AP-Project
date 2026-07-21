package org.example.divar.model;

public class AdminReport {
    private long id;
    private long adId;
    private String adTitle;
    private String sellerFullName;
    private String reason;
    private String imageUrl;

    public AdminReport() {
    }

    public AdminReport(long id, long adId, String adTitle, String sellerFullName, String reason, String imageUrl) {
        this.id = id;
        this.adId = adId;
        this.adTitle = adTitle;
        this.sellerFullName = sellerFullName;
        this.reason = reason;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAdId() {
        return adId;
    }

    public void setAdId(long adId) {
        this.adId = adId;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getSellerFullName() {
        return sellerFullName;
    }

    public void setSellerFullName(String sellerFullName) {
        this.sellerFullName = sellerFullName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
