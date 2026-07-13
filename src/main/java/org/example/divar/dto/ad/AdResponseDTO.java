package org.example.divar.dto.ad;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private final String status;
    private final String sellerUsername;
    private final boolean favorite;

    private AdResponseDTO(long id, String title, String description, String address, long price,
                          String category, String condition, String city, ArrayList<String> imagePaths,
                          String status, String sellerUsername, boolean favorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.city = city;
        this.imagePaths = imagePaths;
        this.status = status;
        this.sellerUsername = sellerUsername;
        this.favorite = favorite;
    }

    public static AdResponseDTO fromJson(JSONObject json) {
        ArrayList<String> images = new ArrayList<>();
        JSONArray imagesArray = json.optJSONArray("imagePaths");
        if (imagesArray != null) {
            for (int i = 0; i < imagesArray.length(); i++) {
                images.add(imagesArray.getString(i));
            }
        }

        return new AdResponseDTO(
                json.optLong("id", 0),
                json.optString("title", ""),
                json.optString("description", ""),
                json.optString("address", ""),
                json.optLong("price", 0),
                json.optString("category", null),
                json.optString("condition", null),
                json.optString("city", null),
                images,
                json.optString("status", null),
                json.optString("sellerUsername", null),
                // نام فیلد boolean توی جاوا معمولا "isFavorite" است ولی وقتی به JSON تبدیل میشه
                // معمولا "is" حذف میشه و فقط "favorite" باقی می‌مونه؛ برای اطمینان هردو رو چک می‌کنیم
                json.optBoolean("favorite", json.optBoolean("isFavorite", false))
        );
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public long getPrice() { return price; }
    public String getCategory() { return category; }
    public String getCondition() { return condition; }
    public String getCity() { return city; }
    public ArrayList<String> getImagePaths() { return imagePaths; }
    public String getStatus() { return status; }
    public String getSellerUsername() { return sellerUsername; }
    public boolean isFavorite() { return favorite; }
}
