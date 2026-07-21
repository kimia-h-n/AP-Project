package org.example.divar.service;

import org.example.divar.util.ApiClient;
import org.example.divar.util.ApiConfig;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RatingServiceHttp implements RatingService {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/api/v1/rating/avg/" + sellerId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300 && response.body() != null) {
                return Double.parseDouble(response.body().trim());
            }
            return 0.0;
        } catch (Exception e) {
            System.err.println("خطا در دریافت میانگین امتیاز: " + e.getMessage());
            return 0.0;
        }
    }
}