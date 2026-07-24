package org.example.divar.service;

import org.example.divar.model.*;
import org.example.divar.dto.ad.AdRequestDTO;
import org.example.divar.dto.ad.AdResponseDTO;
import org.example.divar.util.ApiClient;
import org.example.divar.util.ConvertToAdvertisement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvertisementServiceHttp implements AdvertisementService {

    @Override
    public long createAdvertisement(String title, String description, String address,
                                    long price, Category category, ProductCondition condition, City city,
                                    User seller) throws RuntimeException {

        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("description", description);
        json.put("address", address);
        json.put("price", price);
        json.put("category", category.name());
        json.put("condition", condition.name());
        if (city != null) {
            json.put("cityId", city.getId());
        }

        JSONObject response = ApiClient.post("/api/v1/ads", json);

        if (response == null) {
            throw new RuntimeException("No response received from the server.");
        }

        return response.getLong("id");
    }

    @Override
    public void uploadAdvertisementImages(long adId, ArrayList<String> imagePaths) throws RuntimeException {
        if (imagePaths == null || imagePaths.isEmpty()) return;
        ApiClient.postMultipartImages("/api/v1/ads/" + adId + "/images", imagePaths);
    }

    @Override
    public void deleteAdvertisementImage(String imageId) throws RuntimeException {
        if (imageId == null || imageId.isBlank()) return;
        ApiClient.delete("/api/v1/images/" + imageId);
    }

    @Override
    public void replaceAdvertisementImage(String imageId, String newLocalFilePath) throws RuntimeException {
        if (imageId == null || imageId.isBlank()) return;
        if (newLocalFilePath == null || newLocalFilePath.isBlank()) return;

        ApiClient.putMultipartImage("/api/v1/images/" + imageId, newLocalFilePath);
    }

    @Override
    public void updateAdvertisement(long adId, String title, String description, String address,
                                    long price, Category category, ProductCondition condition, City city,
                                    ArrayList<String> imagePaths) throws RuntimeException {

        AdRequestDTO requestDTO = new AdRequestDTO(
                title, description, address, price, category, condition, city, imagePaths);

        ApiClient.patch("/api/v1/ads/" + adId, requestDTO.toJson());
    }

    @Override
    public ArrayList<Advertisement> getMyAdvertisements() {
        JSONArray responseArray = ApiClient.getList("/api/v1/me/ads/");

        ArrayList<Advertisement> result = new ArrayList<>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject adJson = responseArray.getJSONObject(i);
            AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
            result.add(ConvertToAdvertisement.convertToAdvertisement(dto));
        }
        return result;
    }

    @Override
    public ArrayList<Advertisement> getActiveAdvertisements() {
        JSONArray responseArray = ApiClient.getList("/api/v1/ads");

        ArrayList<Advertisement> result = new ArrayList<>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject adJson = responseArray.getJSONObject(i);
            AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
            result.add(ConvertToAdvertisement.convertToAdvertisement(dto));
        }
        return result;
    }

    @Override
    public Advertisement getAdvertisementById(long id) throws RuntimeException {
        JSONObject responseJson = ApiClient.get("/api/v1/ads/" + id);
        AdResponseDTO dto = AdResponseDTO.fromJson(responseJson);
        return ConvertToAdvertisement.convertToAdvertisement(dto);
    }

    @Override
    public void addToFavorites(long adId) throws RuntimeException {
        ApiClient.post("/api/v1/favorites/" + adId, new JSONObject());
    }

    @Override
    public void removeFromFavorites(long adId) throws RuntimeException {
        ApiClient.delete("/api/v1/favorites/" + adId);
    }

    @Override
    public boolean isFavorite(long adId) {
        JSONObject responseJson = ApiClient.get("/api/v1/ads/" + adId);
        AdResponseDTO dto = AdResponseDTO.fromJson(responseJson);
        return dto.isFavorite();
    }

    @Override
    public List<Advertisement> getFavoriteAdvertisements(String username) {
        JSONArray responseArray = ApiClient.getList("/api/v1/favorites");

        List<Advertisement> result = new ArrayList<>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject adJson = responseArray.getJSONObject(i);
            AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
            Advertisement ad = ConvertToAdvertisement.convertToAdvertisement(dto);
            result.add(ad);
        }
        return result;
    }

    @Override
    public ArrayList<Advertisement> searchAdvertisements(String query) throws RuntimeException {
        try {
            if (query == null || query.isBlank()) {
                return getActiveAdvertisements();
            }

            String encodedQuery = java.net.URLEncoder.encode(query.trim(), "UTF-8");

            JSONArray responseArray = ApiClient.getList("/api/v1/search?title=" + encodedQuery);

            ArrayList<Advertisement> result = new ArrayList<>();
            if (responseArray != null) {
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject adJson = responseArray.getJSONObject(i);
                    AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
                    result.add(ConvertToAdvertisement.convertToAdvertisement(dto));
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error searching advertisements: " + e.getMessage());
        }
    }

    @Override
    public void deleteAdvertisement(long adId) throws RuntimeException {
        ApiClient.delete("/api/v1/ads/" + adId);
    }

    @Override
    public void reportAdvertisement(long adId, ReportReason reason) throws RuntimeException {
        String body = "\"" + reason.name() + "\"";
        ApiClient.postRaw("/api/v1/report-ad?adId=" + adId, body);
    }

    public ArrayList<Advertisement> filterAdvertisements(Long minPrice, Long maxPrice, Category category, Long cityId, DateFilter dateFilter) {
        StringBuilder query = new StringBuilder("/api/v1/filter?");

        if (minPrice != null) query.append("minPrice=").append(minPrice).append("&");
        if (maxPrice != null) query.append("maxPrice=").append(maxPrice).append("&");
        if (category != null) query.append("category=").append(category.name()).append("&");
        if (cityId != null) query.append("cityId=").append(cityId).append("&");
        if (dateFilter != null) query.append("dateFilter=").append(dateFilter.name()).append("&");

        String url = query.toString();
        if (url.endsWith("&") || url.endsWith("?")) {
            url = url.substring(0, url.length() - 1);
        }

        JSONArray responseArray = ApiClient.getList(url);
        ArrayList<Advertisement> result = new ArrayList<>();
        for (int i = 0; i < responseArray.length(); i++) {
            AdResponseDTO dto = AdResponseDTO.fromJson(responseArray.getJSONObject(i));
            result.add(ConvertToAdvertisement.convertToAdvertisement(dto));
        }
        return result;
    }

    @Override
    public ArrayList<City> getAllProvinces() throws RuntimeException {
        try {
            JSONArray responseArray = ApiClient.getList("/api/v1/province");
            ArrayList<City> cities = new ArrayList<>();

            if (responseArray != null) {
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject obj = responseArray.getJSONObject(i);
                    Long id = obj.getLong("id");
                    String name = obj.getString("name");

                    cities.add(new City(id, name));
                }
            }
            return cities;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching provinces list from server: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Advertisement> getSortedAds(AdSortChoice sortChoice) {
        String endpoint = "/api/v1/ads/sortBy?adSortChoice=" + sortChoice.name();
        JSONArray responseArray = ApiClient.getList(endpoint);
        ArrayList<Advertisement> result = new ArrayList<>();

        if (responseArray != null) {
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject adJson = responseArray.getJSONObject(i);
                AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
                result.add(ConvertToAdvertisement.convertToAdvertisement(dto));
            }
        }
        return result;
    }
}