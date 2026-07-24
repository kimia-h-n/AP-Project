package org.example.divar.dto.admin;

import org.json.JSONObject;

/**
 * Data Transfer Object (DTO) for parsing admin report response data from the server.
 */
public class AdminReportResponseDTO {

    private final long id;
    private final long adId;
    private final String adTitle;
    private final String sellerFirstName;
    private final String sellerLastName;
    private final String reason;
    private final String imageUrl;

    public AdminReportResponseDTO(long id, long adId, String adTitle, String sellerFirstName,
                                  String sellerLastName, String reason, String imageUrl) {
        this.id = id;
        this.adId = adId;
        this.adTitle = adTitle;
        this.sellerFirstName = sellerFirstName;
        this.sellerLastName = sellerLastName;
        this.reason = reason;
        this.imageUrl = imageUrl;
    }

    public static AdminReportResponseDTO fromJson(JSONObject obj) {
        if (obj == null) return null;

        long id = obj.optLong("adReportId", obj.optLong("id", 0));
        long adId = obj.optLong("adId", 0);
        String adTitle = obj.optString("adTitle", "");

        String firstName = obj.optString("sellerFirstname", obj.optString("sellerFirstName", ""));
        String lastName = obj.optString("sellerLastname", obj.optString("sellerLastName", ""));

        String reason = obj.optString("reportReason", obj.optString("reason", ""));
        String imageUrl = obj.optString("primaryImageUrl", "");

        return new AdminReportResponseDTO(id, adId, adTitle, firstName, lastName, reason, imageUrl);
    }

    public long getId() {
        return id;
    }

    public long getAdId() {
        return adId;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public String getSellerFirstName() {
        return sellerFirstName;
    }

    public String getSellerLastName() {
        return sellerLastName;
    }

    public String getReason() {
        return reason;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
