package com.example.sales.ad;


import com.example.sales.ad.fav.FavoriteRepository;
import com.example.sales.ad.model.*;
import com.example.sales.ad.model.moderation.AdModerationRequest;
import com.example.sales.exception.*;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final AdMapper adMapper;

    public void addAd(AdRequest request, String username) {
        User user = userRepository.findUsersByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Ad ad = adMapper.toEntity(request);
        ad.setSeller(user);
        ad.setStatus(AdStatus.PENDING);
        adRepository.save(ad);
    }


    public List<AdResponse> getAllActiveAds(String username) {
        List<AdResponse> responseList = adMapper.toResponseList(adRepository.findAllByStatus(AdStatus.APPROVED));
        if (isNotLoggedIn(username)) {
            for (AdResponse response : responseList)
                response.setFavorite(false);
            return responseList;
        }
        User user = userRepository.findUsersByUsername(username).orElseThrow(UserNotFoundException::new);
        Set<Long> favAdIds = favoriteRepository.findFavoriteAdIdsByUser(user);
        responseList.forEach(ad -> ad.setFavorite(favAdIds.contains(ad.getId())));
        return responseList;
    }

    private static boolean isNotLoggedIn(String username) {
        return username == null;
    }

    public List<AdResponse> getAllPendingAds() {
        return adMapper.toResponseList(adRepository.findAllByStatus(AdStatus.PENDING));
    }

    public AdResponse getAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
        if (isNotLoggedIn(username))
            return adMapper.toResponse(ad);

        //todo: if in future, viewing rejected ads is desired, change here
        if (ad.getStatus() != AdStatus.APPROVED)
            throw new AdViewNotAllowedException();

        User user = userRepository.findUsersByUsername(username).orElseThrow(UserNotFoundException::new);

        boolean isFavorite = favoriteRepository.existsFavoriteAdByUserAndAd(user, ad);

        AdResponse adResponse = adMapper.toResponse(ad);
        adResponse.setFavorite(isFavorite);

        return adResponse;
    }


    private boolean isAlreadyRemoved(Ad ad) {
        return ad.getStatus() == AdStatus.REMOVED;
    }

    @Transactional
    public void removeAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        if (!(ad.getSeller().getUsername().equals(username)))
            throw new OperationNotAllowedException();

        if (isAlreadyRemoved(ad))
            throw new AdNotRemovableException();

        ad.setStatus(AdStatus.REMOVED);

    }

    public void moderateAd(Long id, AdModerationRequest moderationRequest) {

        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        switch (moderationRequest.getChoice()) {
            case APPROVE -> {
                ad.setStatus(AdStatus.APPROVED);
                ad.setRejectionReason(null);
            }
            case REJECT -> {
                ad.setStatus(AdStatus.REJECTED);
                ad.setRejectionReason(moderationRequest.getRejectReason());
            }
        }
        adRepository.save(ad);
    }

}
