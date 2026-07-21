package com.example.sales.ad.fav;


import com.example.sales.ad.AdRepository;
import com.example.sales.ad.AdService;
import com.example.sales.ad.model.Ad;
import com.example.sales.ad.model.AdCardSummary;
import com.example.sales.ad.model.AdMapper;
import com.example.sales.exception.AdNotFavoriteException;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyFavoriteAdException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FavoriteAdService {
    private final FavoriteRepository favRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AdPrimaryImageEnricher primaryImageEnricher;

    @Transactional
    public void addToFavorites(Long adId, String username) {
        RequestInfo requestInfo = getRequestInfo(adId, username);

        if (isAdFavorite(requestInfo))
            throw new AlreadyFavoriteAdException();

        FavoriteAd favoriteAd = FavoriteAd.builder()
                .user(requestInfo.user())
                .ad(requestInfo.ad())
                .build();
        favRepository.save(favoriteAd);
    }

    @Transactional
    public void removeFromFavorites(Long adId, String username) {
        RequestInfo requestInfo = getRequestInfo(adId, username);

        long deletedCount = favRepository.deleteByUserAndAd(
                requestInfo.user(),
                requestInfo.ad()
        );

        if (deletedCount == 0) {
            throw new AdNotFavoriteException();
        }

    }

    private boolean isAdFavorite(RequestInfo requestInfo) {
        return favRepository.existsFavoriteAdByUserAndAd(requestInfo.user(), requestInfo.ad());
    }

    private RequestInfo getRequestInfo(Long adId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Ad ad = adRepository.findById(adId).orElseThrow(AdNotFoundException::new);
        return new RequestInfo(user, ad);
    }

    //todo: consider using Pageable if the list of favorites are too long
    public List<AdCardSummary> getAllUserFavoriteAds(String username) {
        if (!userRepository.existsByUsername(username))
            throw new UserNotFoundException();
        List<AdCardSummary> ads = adMapper.toCartSummeryFromFavorites(favRepository.getAllByUser_Username(username));
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }

    private record RequestInfo(User user, Ad ad) {
    }


}
