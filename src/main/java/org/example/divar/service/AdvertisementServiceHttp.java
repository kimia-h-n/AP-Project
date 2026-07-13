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
    public void createAdvertisement(String title, String description, String address,
                                    long price, Category category, ProductCondition condition, City city,
                                    ArrayList<String> imagePaths, User seller) throws RuntimeException {

        AdRequestDTO requestDTO = new AdRequestDTO(
                title, description, address, price, category, condition, city, imagePaths
        );

        ApiClient.post("/api/v1/ads", requestDTO.toJson());
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
    public ArrayList<Advertisement> getAdvertisementsByUser(String userId) {
        ArrayList<Advertisement> allAd = getActiveAdvertisements();
        ArrayList<Advertisement> result = new ArrayList<>();
        for (Advertisement ad : allAd) {
            if (ad.getSeller() != null && userId.equals(ad.getSeller().getUsername())) {
                result.add(ad);
            }
        }
        return result;
    }

    @Override
    public boolean isFavorite(long adId) {
        JSONObject responseJson = ApiClient.get("/api/v1/ads/" + adId);
        AdResponseDTO dto = AdResponseDTO.fromJson(responseJson);
        return dto.isFavorite();
    }

    @Override
    public List<Advertisement> getFavoriteAdvertisements(String username) {
        ArrayList<Advertisement> allAd = getActiveAdvertisements();
        List<Advertisement> favorites = new ArrayList<>();
        for (Advertisement ad : allAd) {
            if (ad.isFavorite()) {
                favorites.add(ad);
            }
        }
        return favorites;
    }
}