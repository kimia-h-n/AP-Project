package org.example.divar.service;

import org.example.divar.model.*;

import java.util.ArrayList;
import java.util.List;

public interface AdvertisementService {

    long createAdvertisement(String title, String description, String address,
                             long price, Category category, ProductCondition condition, City city,
                             User seller) throws RuntimeException;

    void updateAdvertisement(long adId, String title, String description, String address,
                             long price, Category category, ProductCondition condition, City city,
                             ArrayList<String> imagePaths) throws RuntimeException;

    void deleteAdvertisement(long adId) throws RuntimeException;

    void uploadAdvertisementImages(long adId, ArrayList<String> imagePaths) throws RuntimeException;

    void deleteAdvertisementImage(String imageId) throws RuntimeException;

    void replaceAdvertisementImage(String imageId, String newLocalFilePath) throws RuntimeException;

    ArrayList<Advertisement> getActiveAdvertisements();

    Advertisement getAdvertisementById(long id) throws RuntimeException;

    ArrayList<Advertisement> getMyAdvertisements() throws RuntimeException;

    void addToFavorites(long adId) throws RuntimeException;

    void removeFromFavorites(long adId) throws RuntimeException;

    boolean isFavorite(long adId);

    List<Advertisement> getFavoriteAdvertisements(String username);

    ArrayList<Advertisement> searchAdvertisements(String query) throws RuntimeException;

    ArrayList<Advertisement> filterAdvertisements(Long minPrice, Long maxPrice, Category category,
                                                  Long cityId, DateFilter dateFilter) throws RuntimeException;

    void reportAdvertisement(long adId, ReportReason reason) throws RuntimeException;

    ArrayList<City> getAllProvinces() throws RuntimeException;
}



