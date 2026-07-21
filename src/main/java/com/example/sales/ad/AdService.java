package com.example.sales.ad;


import com.example.sales.ad.filter.AdSpecifications;
import com.example.sales.ad.dto.*;
import com.example.sales.ad.favorite.FavoriteRepository;
import com.example.sales.ad.filter.DateFilter;
import com.example.sales.ad.mapper.AdMapper;
import com.example.sales.ad.model.*;
import com.example.sales.exception.*;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.picture.ImageMetaView;
import com.example.sales.picture.StorageRepository;
import com.example.sales.province.ProvinceRepository;
import com.example.sales.rating.SellerRatingService;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.Role;
import com.example.sales.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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


    public List<AdCardSummary> getAllActiveAds(String username) {
//        applyFavorite(username, activeAds);
//        return adMapper.toCartSummeryList(adRepository.findAllByStatus(AdStatus.APPROVED));
        List<AdCardSummary> ads = adMapper.toCartSummeryList
                (adRepository.findAllByStatus(AdStatus.APPROVED));
//        addPrimaryImage(ads);
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }

    public void addPrimaryImage(List<AdCardSummary> ads) {
        List<Long> adIds = ads.stream()
                .map(AdCardSummary::getId)
                .toList();

        Map<Long, UUID> primaryByAdId = storageRepository
                .findPrimaryMetaByAdIdIn(adIds)
                .stream()
                .collect(Collectors.toMap(
                        ImageMetaView::getAdId,
                        ImageMetaView::getId,
                        (existing, ignored) -> existing
                ));
        for (AdCardSummary ad : ads) {
            UUID primaryImageId = primaryByAdId.get(ad.getId());
            ad.setPrimaryImageId(primaryImageId);
            ad.setPrimaryImageUrl(primaryImageId != null ? "/api/v1/images/" + primaryImageId : null);

        }
    }

    private void applyFavorite(String username, List<AdResponse> responseList) {
        if (isNotLoggedIn(username)) {
            for (AdResponse response : responseList) {
                response.setFavorite(false);
                response.setMine(false);
            }
            return;
        }
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Set<Long> favAdIds = favoriteRepository.findFavoriteAdIdsByUser(user);
        responseList.forEach(ad -> {
            ad.setFavorite(favAdIds.contains(ad.getId()));
            ad.setMine(ad.getSellerUsername().equals(username));
        });
    }

    private static boolean isNotLoggedIn(String username) {
        return username == null;
    }

    public AdResponse getAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
        AdResponse adResponse = adMapper.toResponse(ad);
        adResponse.setImages(buildImageResponses(id));
        Double averageRating = sellerRatingService.calculateSellerRatingAvg(ad.getSeller().getId());
        adResponse.setSellerRatingAvg(averageRating);
        if (isNotLoggedIn(username))
            return adResponse;

        //todo: if in future, viewing rejected ads is desired, change here
        //todo: is it neccessary to check this here?
//        if (ad.getStatus() != AdStatus.APPROVED)
//            throw new AdViewNotAllowedException();

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
//        List<AdResponse> ads = adMapper.toResponseList(myAds);
//        for (int i = 0; i < ads.size(); i++) {
//            ads.get(i).setMine(true);
//            ads.get(i).setImages(buildImageResponses(myAds.get(i).getId()));
//        }
        return ads;
    }

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

    public List<AdCardSummary> searchByTitle(String username, String title) {
//        List<AdResponse> matchedAds = adMapper.toResponseList(
//                adRepository.findAll(AdSpecification.titleContains(title)));
//        applyFavorite(username, matchedAds);
        //todo: for later search improvements such as matching with description
//         return adMapper.toCartSummeryList(adRepository.findAll(AdSpecification.titleContains(title)));
        List<AdCardSummary> ads = adMapper.toCartSummeryList(adRepository.findByTitleContainingIgnoreCaseAndStatus(title, AdStatus.APPROVED));
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }

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
//        addPrimaryImage(ads);
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }
}
