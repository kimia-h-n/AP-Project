package org.example.divar.util;

import org.example.divar.model.*;
import org.example.divar.dto.ad.AdResponseDTO;

import java.time.Instant;

public class ConvertToAdvertisement {

    public static Advertisement convertToAdvertisement(AdResponseDTO dto) {
        Advertisement ad = new Advertisement();
        ad.setId(dto.getId());
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setAddress(dto.getAddress());
        ad.setPrice(dto.getPrice());
        ad.setImagePaths(dto.getImageUrls());
        ad.setImageIds(dto.getImageIds());
        ad.setPrimaryImageUrl(normalizeImageRef(dto.getPrimaryImageUrl()));
        ad.setFavorite(dto.isFavorite());

        if (dto.getCategory() != null) {
            ad.setCategory(Category.valueOf(dto.getCategory()));
        }

        if (dto.getCondition() != null) {
            ad.setCondition(ProductCondition.valueOf(dto.getCondition()));
        }

        if (dto.getCityName() != null) {
            City cityObj = new City(null, dto.getCityName());
            ad.setCity(cityObj);
        }

        if (dto.getStatus() != null) {
            ad.setStatus(AdvertisementStatus.valueOf(dto.getStatus()));
        }

        if (dto.getSellerUsername() != null) {
            User seller = new User();
            seller.setUsername(dto.getSellerUsername());
            seller.setId(dto.getSellerId());
            seller.setFirstname(dto.getSellerFirstName());
            seller.setLastname(dto.getSellerLastName());
            seller.setAverageRating(dto.getSellerRating());
            ad.setSeller(seller);
        }

        if (dto.getCreatedAt() != null) {
            ad.setCreatedAt(Instant.parse(dto.getCreatedAt()));
        }

        if (dto.getUpdatedAt() != null) {
            ad.setUpdatedAt(Instant.parse(dto.getUpdatedAt()));
        }

        return ad;
    }

    /**
     * Normalizes the image reference path by ensuring it has the correct base URL or endpoint prefix.
     */
    private static String normalizeImageRef(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String v = value.trim();

        if (v.startsWith("http://") || v.startsWith("https://")) {
            return v;
        }

        if (v.startsWith(ApiConfig.BASE_URL)) {
            return v;
        }

        if (v.startsWith("/api/v1/images/")) {
            return v;
        }

        return "/api/v1/images/" + v.replaceFirst("^/+", "");
    }
}


