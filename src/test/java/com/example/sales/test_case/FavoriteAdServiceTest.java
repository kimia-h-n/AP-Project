package com.example.sales.test_case;

import com.example.sales.ad.AdRepository;
import com.example.sales.ad.AdService;
import com.example.sales.ad.fav.FavoriteAd;
import com.example.sales.ad.fav.FavoriteAdService;
import com.example.sales.ad.fav.FavoriteRepository;
import com.example.sales.ad.model.Ad;
import com.example.sales.ad.model.AdCardSummary;
import com.example.sales.ad.model.AdMapper;
import com.example.sales.exception.AdNotFavoriteException;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyFavoriteAdException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteAdServiceTest {

    @Mock
    private FavoriteRepository favRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private AdMapper adMapper;
    @Mock
    private AdService adService;

    @InjectMocks
    private FavoriteAdService favoriteAdService;

    private User user;
    private Ad ad;

    private void stubUserAndAd() {
        user = User.builder().id(1L).username("john").build();
        ad = Ad.builder().id(10L).build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
    }

    // ---------- addToFavorites ----------

    @Test
    void addToFavorites_alreadyFavorite_throwsAlreadyFavoriteAdException() {
        stubUserAndAd();
        when(favRepository.existsFavoriteAdByUserAndAd(user, ad)).thenReturn(true);

        assertThrows(AlreadyFavoriteAdException.class, () -> favoriteAdService.addToFavorites(10L, "john"));
        verify(favRepository, never()).save(any());
    }

    @Test
    void addToFavorites_adNotFound_throwsAdNotFoundException() {
        user = User.builder().id(1L).username("john").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(adRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(AdNotFoundException.class, () -> favoriteAdService.addToFavorites(10L, "john"));
    }

    @Test
    void addToFavorites_notYetFavorite_savesFavorite() {
        stubUserAndAd();
        when(favRepository.existsFavoriteAdByUserAndAd(user, ad)).thenReturn(false);

        favoriteAdService.addToFavorites(10L, "john");

        verify(favRepository).save(argThat(fav ->
                fav.getUser().equals(user) && fav.getAd().equals(ad)
        ));
    }

    // ---------- removeFromFavorites ----------

    @Test
    void removeFromFavorites_notFavorite_throwsAdNotFavoriteException() {
        stubUserAndAd();
        when(favRepository.existsFavoriteAdByUserAndAd(user, ad)).thenReturn(false);

        assertThrows(AdNotFavoriteException.class, () -> favoriteAdService.removeFromFavorites(10L, "john"));
        verify(favRepository, never()).deleteByUserAndAd(any(), any());
    }

    @Test
    void removeFromFavorites_isFavorite_deletesFavorite() {
        stubUserAndAd();
        when(favRepository.existsFavoriteAdByUserAndAd(user, ad)).thenReturn(true);

        favoriteAdService.removeFromFavorites(10L, "john");

        verify(favRepository).deleteByUserAndAd(user, ad);
    }

    // ---------- getAllUserFavoriteAds ----------

    @Test
    void getAllUserFavoriteAds_userNotFound_throwsUserNotFoundException() {
        when(userRepository.existsByUsername("ghost")).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> favoriteAdService.getAllUserFavoriteAds("ghost"));
        verifyNoInteractions(favRepository, adMapper);
    }

    @Test
    void getAllUserFavoriteAds_userExists_returnsMappedListWithPrimaryImages() {
        List<FavoriteAd> favorites = List.of(FavoriteAd.builder().id(1L).build());
        List<AdCardSummary> summaries = List.of(new AdCardSummary());

        when(userRepository.existsByUsername("john")).thenReturn(true);
        when(favRepository.getAllByUser_Username("john")).thenReturn(favorites);
        when(adMapper.toCartSummeryFromFavorites(favorites)).thenReturn(summaries);

        List<AdCardSummary> result = favoriteAdService.getAllUserFavoriteAds("john");

        assertEquals(summaries, result);
        verify(adService).addPrimaryImage(summaries);
    }
}
