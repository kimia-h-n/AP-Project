package com.example.sales.ad.fav;


import com.example.sales.ad.AdRepository;
import com.example.sales.ad.model.Ad;
import com.example.sales.exception.AdNotFavoriteException;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyFavoriteAdException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FavoriteAdService {
    private final FavoriteRepository favRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;

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

    public void removeFromFavorites(Long adId, String username) {
        RequestInfo requestInfo = getRequestInfo(adId, username);

        if (!isAdFavorite(requestInfo))
            throw new AdNotFavoriteException();

        favRepository.deleteByUserAndAd(requestInfo.user(), requestInfo.ad());

    }

    private boolean isAdFavorite(RequestInfo requestInfo) {
        return favRepository.existsFavoriteAdByUserAndAd(requestInfo.user(), requestInfo.ad());
    }

    private RequestInfo getRequestInfo(Long adId, String username) {
        User user = userRepository.findUsersByUsername(username).orElseThrow(UserNotFoundException::new);
        Ad ad = adRepository.findById(adId).orElseThrow(AdNotFoundException::new);
        return new RequestInfo(user, ad);
    }

    private record RequestInfo(User user, Ad ad) {
    }

}
