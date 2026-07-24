package org.example.divar.service;

import org.example.divar.util.ApiClient;
import org.json.JSONObject;
import java.net.http.HttpClient;

public class RatingServiceHttp implements RatingService {

    /**
     * Submits a rating score for a specific seller via HTTP POST request.
     *
     * @param sellerId the ID of the seller
     * @param rating the score given by the user
     * @throws RuntimeException if the request fails
     */
    @Override
    public void submitRating(long sellerId, int rating) throws RuntimeException {
        JSONObject json = new JSONObject();
        json.put("sellerId", sellerId);
        json.put("rating", rating);

        ApiClient.post("/api/v1/rating", json);
    }

    /**
     * Retrieves the average rating score of a seller from the server via HTTP GET request.
     *
     * @param sellerId the ID of the seller
     * @return the average rating, or 0.0 if an error occurs or no rating exists
     * @throws RuntimeException if fetching fails
     */
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