package com.example.sales.ad;


import com.example.sales.ad.fav.FavoriteRepository;
import com.example.sales.ad.model.*;
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Ad ad = adMapper.toEntity(request);
        ad.setSeller(user);
        ad.setStatus(AdStatus.PENDING);
        adRepository.save(ad);
    }


    public List<AdCartSummery> getAllActiveAds(String username) {
//        List<AdCartSummery> activeAds = adMapper.toResponseList(adRepository.findAllByStatus(AdStatus.APPROVED));
//        applyFavorite(username, activeAds);
        return adMapper.toCartSummeryList(adRepository.findAllByStatus(AdStatus.APPROVED));
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
        if (isNotLoggedIn(username))
            return adMapper.toResponse(ad);

        //todo: if in future, viewing rejected ads is desired, change here
        //todo: is it neccessary to check this here?
//        if (ad.getStatus() != AdStatus.APPROVED)
//            throw new AdViewNotAllowedException();

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        boolean isFavorite = favoriteRepository.existsFavoriteAdByUserAndAd(user, ad);

        AdResponse adResponse = adMapper.toResponse(ad);
        adResponse.setFavorite(isFavorite);
        adResponse.setMine(username.equals(adResponse.getSellerUsername()));

        return adResponse;
    }


    private boolean isAlreadyRemoved(Ad ad) {
        return ad.getStatus() == AdStatus.REMOVED;
    }

    @Transactional
    public void removeAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        if (!isAdOwner(username, ad))
            throw new OperationNotAllowedException();

        if (isAlreadyRemoved(ad))
            throw new AdNotRemovableException();

        ad.setStatus(AdStatus.REMOVED);

    }

    private static boolean isAdOwner(String username, Ad ad) {
        return ad.getSeller().getUsername().equals(username);
    }


    public List<AdResponse> getAllMyAds(String username) {
        List<AdResponse> ads = adMapper.toResponseList(adRepository.findAllBySeller
                (userRepository.findByUsername(username).
                        orElseThrow(UserNotFoundException::new)));
        ads.forEach(ad -> ad.setMine(true));
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

        if (request.getPrice() != null && request.getPrice() != ad.getPrice()) {
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

        if (request.getCity() != null && request.getCity() != ad.getCity()) {
            ad.setCity(request.getCity());
            contentChanged = true;
        }

        if (request.getImagePaths() != null && !request.getImagePaths().equals(ad.getImagePaths())) {
            ad.setImagePaths(request.getImagePaths());
            contentChanged = true;
        }

        if (contentChanged && ad.getStatus() == AdStatus.APPROVED) {
            ad.setStatus(AdStatus.PENDING);
        }

        return contentChanged;
    }

    public List<AdCartSummery> searchByTitle(String username, String title) {
//        List<AdResponse> matchedAds = adMapper.toResponseList(
//                adRepository.findAll(AdSpecification.titleContains(title)));
//        applyFavorite(username, matchedAds);
        //todo: for later search improvements such as matching with description
//         return adMapper.toCartSummeryList(adRepository.findAll(AdSpecification.titleContains(title)));
        return adMapper.toCartSummeryList(adRepository.findByTitleContainingIgnoreCaseAndStatus(title, AdStatus.APPROVED));
    }
}
