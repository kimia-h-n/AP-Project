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

/**
 * HTTP implementation of the AdvertisementService interface.
 * Handles all advertisement-related API calls to the server.
 */
public class AdvertisementServiceHttp implements AdvertisementService {

    /**
     * Creates a new advertisement.
     *
     * @param title       advertisement title
     * @param description advertisement description
     * @param address     location address
     * @param price       item price
     * @param category    product category
     * @param condition   product condition
     * @param city        city where the item is located
     * @param seller      user creating the advertisement
     * @return ID of the created advertisement
     * @throws RuntimeException if request fails or response is null
     */
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

    /**
     * Uploads multiple images for an existing advertisement.
     *
     * @param adId       ID of the advertisement
     * @param imagePaths list of local image file paths
     * @throws RuntimeException if upload fails
     */
    @Override
    public void uploadAdvertisementImages(long adId, ArrayList<String> imagePaths) throws RuntimeException {
        if (imagePaths == null || imagePaths.isEmpty()) return;
        ApiClient.postMultipartImages("/api/v1/ads/" + adId + "/images", imagePaths);
    }

    /**
     * Deletes an image from an advertisement.
     *
     * @param imageId ID of the image to delete
     * @throws RuntimeException if deletion fails
     */
    @Override
    public void deleteAdvertisementImage(String imageId) throws RuntimeException {
        if (imageId == null || imageId.isBlank()) return;
        ApiClient.delete("/api/v1/images/" + imageId);
    }

    /**
     * Replaces an existing advertisement image with a new one.
     *
     * @param imageId          ID of the image to replace
     * @param newLocalFilePath path to the new image file
     * @throws RuntimeException if replacement fails
     */
    @Override
    public void replaceAdvertisementImage(String imageId, String newLocalFilePath) throws RuntimeException {
        if (imageId == null || imageId.isBlank()) return;
        if (newLocalFilePath == null || newLocalFilePath.isBlank()) return;

        ApiClient.putMultipartImage("/api/v1/images/" + imageId, newLocalFilePath);
    }

    /**
     * Updates an existing advertisement's details.
     *
     * @param adId        ID of the advertisement
     * @param title       new title
     * @param description new description
     * @param address     new address
     * @param price       new price
     * @param category    new category
     * @param condition   new condition
     * @param city        new city
     * @param imagePaths  new image paths
     * @throws RuntimeException if update fails
     */
    @Override
    public void updateAdvertisement(long adId, String title, String description, String address,
                                    long price, Category category, ProductCondition condition, City city,
                                    ArrayList<String> imagePaths) throws RuntimeException {

        AdRequestDTO requestDTO = new AdRequestDTO(
                title, description, address, price, category, condition, city, imagePaths);

        ApiClient.patch("/api/v1/ads/" + adId, requestDTO.toJson());
    }

    /**
     * Retrieves all advertisements created by the current user.
     *
     * @return list of user's advertisements
     */
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

    /**
     * Retrieves all active (published) advertisements.
     *
     * @return list of active advertisements
     */
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

    /**
     * Retrieves a single advertisement by its ID.
     *
     * @param id advertisement ID
     * @return Advertisement object
     * @throws RuntimeException if advertisement not found or request fails
     */
    @Override
    public Advertisement getAdvertisementById(long id) throws RuntimeException {
        JSONObject responseJson = ApiClient.get("/api/v1/ads/" + id);
        AdResponseDTO dto = AdResponseDTO.fromJson(responseJson);
        return ConvertToAdvertisement.convertToAdvertisement(dto);
    }

    /**
     * Adds an advertisement to the current user's favorites.
     *
     * @param adId advertisement ID
     * @throws RuntimeException if request fails
     */
    @Override
    public void addToFavorites(long adId) throws RuntimeException {
        ApiClient.post("/api/v1/favorites/" + adId, new JSONObject());
    }

    /**
     * Removes an advertisement from the current user's favorites.
     *
     * @param adId advertisement ID
     * @throws RuntimeException if request fails
     */
    @Override
    public void removeFromFavorites(long adId) throws RuntimeException {
        ApiClient.delete("/api/v1/favorites/" + adId);
    }

    /**
     * Checks if an advertisement is in the current user's favorites.
     *
     * @param adId advertisement ID
     * @return true if favorited, false otherwise
     */
    @Override
    public boolean isFavorite(long adId) {
        JSONObject responseJson = ApiClient.get("/api/v1/ads/" + adId);
        AdResponseDTO dto = AdResponseDTO.fromJson(responseJson);
        return dto.isFavorite();
    }

    /**
     * Retrieves all favorite advertisements of the current user.
     *
     * @param username current username (for context)
     * @return list of favorite advertisements
     */
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

    /**
     * Searches advertisements by title query.
     *
     * @param query search keyword
     * @return list of matching advertisements
     * @throws RuntimeException if search fails
     */
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

    /**
     * Deletes an advertisement.
     *
     * @param adId ID of the advertisement to delete
     * @throws RuntimeException if deletion fails
     */
    @Override
    public void deleteAdvertisement(long adId) throws RuntimeException {
        ApiClient.delete("/api/v1/ads/" + adId);
    }

    /**
     * Reports an advertisement with a specific reason.
     *
     * @param adId   ID of the advertisement
     * @param reason reason for reporting
     * @throws RuntimeException if report fails
     */
    @Override
    public void reportAdvertisement(long adId, ReportReason reason) throws RuntimeException {
        String body = "\"" + reason.name() + "\"";
        ApiClient.postRaw("/api/v1/report-ad?adId=" + adId, body);
    }

    /**
     * Filters advertisements by various criteria.
     *
     * @param minPrice    minimum price filter
     * @param maxPrice    maximum price filter
     * @param category    category filter
     * @param cityId      city ID filter
     * @param dateFilter  date filter
     * @return list of filtered advertisements
     */
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

    /**
     * Retrieves all provinces/cities from the server.
     *
     * @return list of cities
     * @throws RuntimeException if request fails
     */
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

    /**
     * Retrieves advertisements sorted by a specific choice.
     *
     * @param sortChoice sorting criteria
     * @return sorted list of advertisements
     */
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