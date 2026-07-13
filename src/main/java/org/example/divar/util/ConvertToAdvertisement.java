package org.example.divar.util;

import org.example.divar.model.*;
import org.example.divar.dto.ad.AdResponseDTO;

public class ConvertToAdvertisement {

    public static Advertisement convertToAdvertisement(AdResponseDTO dto) {
        Advertisement ad = new Advertisement();
        ad.setId(dto.getId());
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setAddress(dto.getAddress());
        ad.setPrice(dto.getPrice());
        ad.setImagePaths(dto.getImagePaths());
        ad.setFavorite(dto.isFavorite());

        if (dto.getCategory() != null) {
            ad.setCategory(Category.valueOf(dto.getCategory()));
        }
        if (dto.getCondition() != null) {
            ad.setCondition(ProductCondition.valueOf(dto.getCondition()));
        }
        if (dto.getCity() != null) {
            ad.setCity(City.valueOf(dto.getCity()));
        }
        if (dto.getStatus() != null) {
            ad.setStatus(AdvertisementStatus.valueOf(dto.getStatus()));
        }

        if (dto.getSellerUsername() != null) {
            User seller = new User();
            seller.setUsername(dto.getSellerUsername());
            ad.setSeller(seller);
        }

        return ad;
    }
}
