import com.example.sales.ad.Ad;
import com.example.sales.ad.AdRepository;
import com.example.sales.ad.favorite.FavoriteAd;
import com.example.sales.ad.favorite.FavoriteAdService;
import com.example.sales.ad.favorite.FavoriteRepository;
import com.example.sales.ad.mapper.AdMapper;
import com.example.sales.exception.AdNotFavoriteException;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyFavoriteAdException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.user.UserRepository;
import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FavoriteAdService}.
 * Covers: adding an ad to favorites (success + already-favorite case),
 * removing an ad from favorites (success + not-favorite case),
 * and missing user/ad error handling.
 */
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
    private AdPrimaryImageEnricher primaryImageEnricher;

    @InjectMocks
    private FavoriteAdService favoriteAdService;

    private User user;
    private Ad ad;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("fateme").role(Role.USER).enabled(true).build();
        ad = Ad.builder().id(10L).title("Old bicycle").build();
    }

    @Test
    void addToFavorites_savesFavorite_whenNotAlreadyFavorited() {
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(user));
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(favRepository.existsFavoriteAdByUserAndAd(user, ad)).thenReturn(false);

        favoriteAdService.addToFavorites(10L, "fateme");

        verify(favRepository).save(any(FavoriteAd.class));
    }

    @Test
    void addToFavorites_throwsAlreadyFavoriteAdException_whenAlreadyFavorited() {
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(user));
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(favRepository.existsFavoriteAdByUserAndAd(user, ad)).thenReturn(true);

        assertThrows(AlreadyFavoriteAdException.class,
                () -> favoriteAdService.addToFavorites(10L, "fateme"));
        verify(favRepository, never()).save(any());
    }

    @Test
    void addToFavorites_throwsUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> favoriteAdService.addToFavorites(10L, "ghost"));
    }

    @Test
    void addToFavorites_throwsAdNotFoundException_whenAdDoesNotExist() {
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(user));
        when(adRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AdNotFoundException.class,
                () -> favoriteAdService.addToFavorites(999L, "fateme"));
    }

    @Test
    void removeFromFavorites_succeeds_whenFavoriteExisted() {
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(user));
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(favRepository.deleteByUserAndAd(user, ad)).thenReturn(1L);

        assertDoesNotThrow(() -> favoriteAdService.removeFromFavorites(10L, "fateme"));
    }

    @Test
    void removeFromFavorites_throwsAdNotFavoriteException_whenNothingWasDeleted() {
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(user));
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(favRepository.deleteByUserAndAd(user, ad)).thenReturn(0L);

        assertThrows(AdNotFavoriteException.class,
                () -> favoriteAdService.removeFromFavorites(10L, "fateme"));
    }
}
