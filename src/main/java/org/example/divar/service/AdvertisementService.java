package org.example.divar.service;

import org.example.divar.model.*;

import java.util.ArrayList;
import java.util.List;

public interface AdvertisementService {
    void createAdvertisement(String title, String description, String address,
                             long price, Category category, ProductCondition condition, City city,
                             ArrayList<String> imagePaths, User seller) throws RuntimeException;

    ArrayList<Advertisement> getActiveAdvertisements();
    Advertisement getAdvertisementById(long id) throws RuntimeException;
    void addToFavorites(long adId) throws RuntimeException;
    void removeFromFavorites(long adId) throws RuntimeException;
    ArrayList<Advertisement> getAdvertisementsByUser(String userId);
    boolean isFavorite(long adId);
    List<Advertisement> getFavoriteAdvertisements(String username);
    void deleteAdvertisement(long adId) throws RuntimeException;
}

