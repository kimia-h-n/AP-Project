package com.example.sales.ad;


import com.example.sales.ad.filter.AdSortChoice;
import com.example.sales.ad.filter.AdSpecifications;
import com.example.sales.ad.dto.*;
import com.example.sales.ad.favorite.FavoriteRepository;
import com.example.sales.ad.filter.DateFilter;
import com.example.sales.ad.mapper.AdMapper;
import com.example.sales.ad.model.*;
import com.example.sales.exception.*;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.picture.StorageRepository;
import com.example.sales.province.ProvinceRepository;
import com.example.sales.rating.SellerRatingService;
import com.example.sales.user.UserRepository;
import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service containing advertisement business logic.
 */
@Service
@AllArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final StorageRepository storageRepository;
    private final ProvinceRepository provinceRepository;
    private final AdMapper adMapper;
    private final AdPrimaryImageEnricher primaryImageEnricher;
    private final SellerRatingService sellerRatingService;

    /**
     * Creates a new advertisement for the given user.
     */
    public AdInsertResponse addAd(AdRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Ad ad = adMapper.toEntity(request);
        ad.setCity(provinceRepository.findById(request.getCityId()).orElseThrow(CityNotFoundException::new));
        ad.setSeller(user);
        ad.setStatus(AdStatus.PENDING);
        Ad savedAd = adRepository.save(ad);
        return new AdInsertResponse(savedAd.getId());
    }

    /**
     * Returns all approved advertisements.
     */
    public List<AdCardSummary> getAllActiveAds(String username) {
        List<AdCardSummary> ads = adMapper.toCartSummeryList
                (adRepository.findAllByStatus(AdStatus.APPROVED));
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }

    private static boolean isNotLoggedIn(String username) {
        return username == null;
    }

    /**
     * Returns the full ad details together with images and seller data.
     */
    public AdResponse getAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
        AdResponse adResponse = adMapper.toResponse(ad);
        adResponse.setImages(buildImageResponses(id));
        Double averageRating = sellerRatingService.calculateSellerRatingAvg(ad.getSeller().getId());
        adResponse.setSellerRatingAvg(averageRating);
        if (isNotLoggedIn(username))
            return adResponse;

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        boolean isFavorite = favoriteRepository.existsFavoriteAdByUserAndAd(user, ad);
        adResponse.setFavorite(isFavorite);
        adResponse.setMine(username.equals(adResponse.getSellerUsername()));

        return adResponse;
    }

    private List<ImageResponse> buildImageResponses(Long adId) {
        return storageRepository.findMetaByAdId(adId).stream()
                .map(m -> new ImageResponse(
                        m.getId(),
                        "/api/v1/images/" + m.getId(),
                        m.getSortOrder(),
                        m.isPrimaryImage()))
                .toList();
    }

    private boolean isAlreadyRemoved(Ad ad) {
        return ad.getStatus() == AdStatus.REMOVED;
    }

    private boolean isAdmin(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new)
                .getRole() == Role.ADMIN;
    }

    /**
     * Removes an advertisement if the current user is allowed to do so.
     */
    @Transactional
    public void removeAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        if (!(isAdOwner(username, ad) || isAdmin(username)))
            throw new OperationNotAllowedException();

        if (isAlreadyRemoved(ad))
            throw new AdNotRemovableException();

        ad.setStatus(AdStatus.REMOVED);

    }

    private static boolean isAdOwner(String username, Ad ad) {
        return ad.getSeller().getUsername().equals(username);
    }

    /**
     * Returns all approved ads created by the current user.
     */
    public List<AdCardSummary> getAllMyAds(String username) {

        List<Ad> myAdsList = adRepository.findAllBySellerAndStatus(
                userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new),
                AdStatus.APPROVED);
        List<AdCardSummary> ads = adMapper.toCartSummeryList(myAdsList);
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }

    /**
     * Updates an advertisement if the current user owns it.
     */
    public void updateAd(Long id, String username, AdUpdateRequest request) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
        if (!isAdOwner(username, ad))
            throw new OperationNotAllowedException();

        if (updateAdFields(request, ad))
            adRepository.save(ad);
    }

    private boolean updateAdFields(AdUpdateRequest request, Ad ad) {
        boolean contentChanged = false;

        if (request.getTitle() != null && !request.getTitle().equals(ad.getTitle())) {
            ad.setTitle(request.getTitle());
            contentChanged = true;
        }

        if (request.getDescription() != null && !request.getDescription().equals(ad.getDescription())) {
            ad.setDescription(request.getDescription());
            contentChanged = true;
        }

        if (request.getAddress() != null && !request.getAddress().equals(ad.getAddress())) {
            ad.setAddress(request.getAddress());
            contentChanged = true;
        }

        if (request.getPrice() != null && !(request.getPrice().equals(ad.getPrice()))) {
            ad.setPrice(request.getPrice());
            contentChanged = true;
        }

        if (request.getCategory() != null && request.getCategory() != ad.getCategory()) {
            ad.setCategory(request.getCategory());
            contentChanged = true;
        }

        if (request.getCondition() != null && request.getCondition() != ad.getCondition()) {
            ad.setCondition(request.getCondition());
            contentChanged = true;
        }

        if (request.getCityId() != null && !Objects.equals(request.getCityId(), ad.getCity().getId())) {
            ad.setCity(provinceRepository.findById(request.getCityId())
                    .orElseThrow(CityNotFoundException::new));
            contentChanged = true;
        }

        if (contentChanged && ad.getStatus() == AdStatus.APPROVED) {
            ad.setStatus(AdStatus.PENDING);
        }

        return contentChanged;
    }

    /**
     * Searches approved ads by title.
     */
    public List<AdCardSummary> searchByTitle(String title) {
        List<AdCardSummary> ads = adMapper.toCartSummeryList(adRepository.findByTitleContainingIgnoreCaseAndStatus(title, AdStatus.APPROVED));
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }

    /**
     * Filters approved ads using the provided criteria.
     */
    public List<AdCardSummary> filterAds(Long minPrice, Long maxPrice, AdCategory category,
                                         DateFilter dateFilter, Long cityId) {
        Specification<Ad> spec = Specification.where(AdSpecifications.hasStatus(AdStatus.APPROVED));

        if (minPrice != null || maxPrice != null) {
            spec = spec.and(AdSpecifications.priceBetween(minPrice, maxPrice));
        }
        if (category != null) {
            spec = spec.and(AdSpecifications.hasCategory(category));
        }
        if (cityId != null) {
            spec = spec.and(AdSpecifications.hasCityId(cityId));
        }
        if (dateFilter != null) {
            spec = spec.and(AdSpecifications.hasDateFilter(dateFilter));
        }

        List<AdCardSummary> ads = adMapper.toCartSummeryList(adRepository.findAll(spec));
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }
    /**
     * Sorts approved ads using the given sort option.
     */
    public List<AdCardSummary> sortAdsBy(AdSortChoice adSortChoice) {
        Specification<Ad> spec = Specification.where(AdSpecifications.hasStatus(AdStatus.APPROVED));
        spec = spec.and(AdSpecifications.applySorting(adSortChoice));
        List<AdCardSummary> ads = adMapper.toCartSummeryList(adRepository.findAll(spec));
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }
}
