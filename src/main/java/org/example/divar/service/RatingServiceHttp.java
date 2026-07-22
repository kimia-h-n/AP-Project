package org.example.divar.service;

import org.example.divar.util.ApiClient;
import org.json.JSONObject;
import java.net.http.HttpClient;

public class RatingServiceHttp implements RatingService {

    @Override
    public void submitRating(long sellerId, int rating) throws RuntimeException {
        JSONObject json = new JSONObject();
        json.put("sellerId", sellerId);
        json.put("rating", rating);

        ApiClient.post("/api/v1/rating", json);
    }

    @Override
    public double getAverageRating(long sellerId) throws RuntimeException {
        try {
            String response = ApiClient.getString("/api/v1/rating/avg/" + sellerId);
            if (response != null && !response.isBlank()) {
                return Double.parseDouble(response.trim());
            }
            return 0.0;
        } catch (Exception e) {
            System.err.println("Error fetching average rating: " + e.getMessage());
            return 0.0;
        }
    }
}