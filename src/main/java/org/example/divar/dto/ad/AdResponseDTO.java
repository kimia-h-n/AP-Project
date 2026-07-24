package org.example.divar.dto.ad;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Data Transfer Object (DTO) for parsing and mapping advertisement response data from the server.
 */
public class AdResponseDTO {

    private final long id;
    private final String title;
    private final String description;
    private final String address;
    private final long price;
    private final String category;
    private final String condition;
    private final String city;
    private final ArrayList<String> imagePaths;
    private final String primaryImageUrl;
    private final ArrayList<String> imageUrls;
    private final ArrayList<String> imageIds;
    private final String status;
    private final String sellerUsername;
    private final long sellerId;
    private final boolean favorite;
    private final String createdAt;
    private final String updatedAt;
    private final String sellerFirstName;
    private final String sellerLastName;
    private final double sellerRating;

    private AdResponseDTO(long id, String title, String description, String address, long price,
                          String category, String condition, String city, ArrayList<String> imagePaths,
                          String primaryImageUrl, ArrayList<String> imageUrls, ArrayList<String> imageIds,
                          String status, String sellerUsername, long sellerId, boolean favorite,
                          String createdAt, String updatedAt, String sellerFirstName, String sellerLastName,
                          double sellerRating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.city = city;
        this.imagePaths = imagePaths;
        this.primaryImageUrl = primaryImageUrl;
        this.imageUrls = imageUrls;
        this.imageIds = imageIds;
        this.status = status;
        this.sellerUsername = sellerUsername;
        this.sellerId = sellerId;
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sellerFirstName = sellerFirstName;
        this.sellerLastName = sellerLastName;
        this.sellerRating = sellerRating;
    }

    public static AdResponseDTO fromJson(JSONObject json) {
        ArrayList<String> images = new ArrayList<>();
        JSONArray imagesArray = json.optJSONArray("imagePaths");
        if (imagesArray != null) {
            for (int i = 0; i < imagesArray.length(); i++) {
                images.add(imagesArray.getString(i));
            }
        }

        boolean isFavorite = json.optBoolean("isFavorite", false);

        String primaryImageUrl = json.optString("primaryImageUrl", null);

        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<String> imageIds = new ArrayList<>();
        JSONArray imagesObjArray = json.optJSONArray("images");
        if (imagesObjArray != null) {
            for (int i = 0; i < imagesObjArray.length(); i++) {
                JSONObject imgObj = imagesObjArray.optJSONObject(i);
                if (imgObj != null) {
                    String url = imgObj.optString("url", null);
                    if (url != null) {
                        imageUrls.add(url);
                        imageIds.add(imgObj.optString("id", null));
                    }
                }
            }
        }
        if (primaryImageUrl == null && !imageUrls.isEmpty()) {
            primaryImageUrl = imageUrls.get(0);
        }

        String cityName = json.optString("cityName", null);

        if (cityName == null && json.has("city")) {
            Object cityObj = json.get("city");
            if (cityObj instanceof JSONObject) {
                cityName = ((JSONObject) cityObj).optString("name", null);
            } else if (cityObj instanceof String) {
                cityName = (String) cityObj;
            }
        }

        double rating = json.optDouble("sellerRatingAvg", 0.0);

        return new AdResponseDTO(
                json.optLong("id", 0),
                json.optString("title", ""),
                json.optString("description", ""),
                json.optString("address", ""),
                json.optLong("price", 0),
                json.optString("category", null),
                json.optString("condition", null),
                cityName,
                images,
                primaryImageUrl,
                imageUrls,
                imageIds,
                json.optString("status", null),
                json.optString("sellerUsername", null),
                json.optLong("sellerId", 0),
                isFavorite,
                json.optString("createdAt", null),
                json.optString("updatedAt", null),
                json.optString("sellerFirstname", ""),
                json.optString("sellerLastname", ""),
                rating
        );
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public long getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public String getCity() {
        return city;
    }

    public String getCityName() {
        return city;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public ArrayList<String> getImageIds() {
        return imageIds;
    }

    public String getStatus() {
        return status;
    }

    public String getSellerId() {
        return String.valueOf(sellerId);
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public String getSellerFirstName() {
        return sellerFirstName;
    }

    public String getSellerLastName() {
        return sellerLastName;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public double getSellerRating() {
        return sellerRating;
    }

    public double getSellerRatingAvg() {
        return sellerRating;
    }
}


