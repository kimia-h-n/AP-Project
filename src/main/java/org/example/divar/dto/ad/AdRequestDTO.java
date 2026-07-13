package org.example.divar.dto.ad;

import org.example.divar.model.Category;
import org.example.divar.model.City;
import org.example.divar.model.ProductCondition;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdRequestDTO {

    private final String title;
    private final String description;
    private final String address;
    private final long price;
    private final Category category;
    private final ProductCondition condition;
    private final City city;
    private final ArrayList<String> imagePaths;

    public AdRequestDTO(String title, String description, String address,
                        long price, Category category, ProductCondition condition,
                        City city, ArrayList<String> imagePaths) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.city = city;
        this.imagePaths = (imagePaths != null) ? imagePaths : new ArrayList<>();
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("description", description);
        json.put("address", address);
        json.put("price", price);
        json.put("category", category.name());
        json.put("condition", condition.name());
        json.put("city", city.name());
        json.put("imagePaths", new JSONArray(imagePaths));
        return json;
    }
}
