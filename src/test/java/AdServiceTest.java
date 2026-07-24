import com.example.sales.ad.Ad;
import com.example.sales.ad.AdRepository;
import com.example.sales.ad.AdService;
import com.example.sales.ad.dto.AdUpdateRequest;
import com.example.sales.ad.favorite.FavoriteRepository;
import com.example.sales.ad.mapper.AdMapper;
import com.example.sales.ad.model.AdStatus;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AdNotRemovableException;
import com.example.sales.exception.OperationNotAllowedException;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.picture.StorageRepository;
import com.example.sales.province.ProvinceRepository;
import com.example.sales.rating.SellerRatingService;
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
 * Unit tests for {@link AdService}.
 * Covers: removeAd (owner can remove, admin can remove, a stranger cannot,
 * an already-removed ad cannot be removed again) and updateAd
 * (only the owner may update; an approved ad goes back to PENDING after an edit).
 */
@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private StorageRepository storageRepository;
    @Mock
    private ProvinceRepository provinceRepository;
    @Mock
    private AdMapper adMapper;
    @Mock
    private AdPrimaryImageEnricher primaryImageEnricher;
    @Mock
    private SellerRatingService sellerRatingService;

    @InjectMocks
    private AdService adService;

    private User owner;
    private User otherUser;
    private User admin;
    private Ad ad;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("kimia").role(Role.USER).enabled(true).build();
        otherUser = User.builder().id(2L).username("fateme").role(Role.USER).enabled(true).build();
        admin = User.builder().id(3L).username("admin").role(Role.ADMIN).enabled(true).build();

        ad = Ad.builder()
                .id(100L)
                .title("Laptop")
                .price(1000L)
                .status(AdStatus.APPROVED)
                .seller(owner)
                .build();
    }

    @Test
    void removeAd_succeeds_whenCallerIsOwner() {
        when(adRepository.findById(100L)).thenReturn(Optional.of(ad));

        adService.removeAd(100L, "kimia");

        assertEquals(AdStatus.REMOVED, ad.getStatus());
    }

    @Test
    void removeAd_succeeds_whenCallerIsAdmin() {
        when(adRepository.findById(100L)).thenReturn(Optional.of(ad));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        adService.removeAd(100L, "admin");

        assertEquals(AdStatus.REMOVED, ad.getStatus());
    }

    @Test
    void removeAd_throwsOperationNotAllowedException_whenCallerIsNeitherOwnerNorAdmin() {
        when(adRepository.findById(100L)).thenReturn(Optional.of(ad));
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(otherUser));

        assertThrows(OperationNotAllowedException.class,
                () -> adService.removeAd(100L, "fateme"));
        assertEquals(AdStatus.APPROVED, ad.getStatus()); // unchanged
    }

    @Test
    void removeAd_throwsAdNotRemovableException_whenAdAlreadyRemoved() {
        ad.setStatus(AdStatus.REMOVED);
        when(adRepository.findById(100L)).thenReturn(Optional.of(ad));

        assertThrows(AdNotRemovableException.class,
                () -> adService.removeAd(100L, "kimia"));
    }

    @Test
    void removeAd_throwsAdNotFoundException_whenAdDoesNotExist() {
        when(adRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(AdNotFoundException.class,
                () -> adService.removeAd(404L, "kimia"));
    }

    @Test
    void updateAd_throwsOperationNotAllowedException_whenCallerIsNotOwner() {
        when(adRepository.findById(100L)).thenReturn(Optional.of(ad));
        AdUpdateRequest request = new AdUpdateRequest();
        request.setTitle("Hacked title");

        assertThrows(OperationNotAllowedException.class,
                () -> adService.updateAd(100L, "fateme", request));
        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_setsStatusBackToPending_whenApprovedAdContentChanges() {
        when(adRepository.findById(100L)).thenReturn(Optional.of(ad));
        AdUpdateRequest request = new AdUpdateRequest();
        request.setTitle("New laptop title");

        adService.updateAd(100L, "kimia", request);

        assertEquals("New laptop title", ad.getTitle());
        assertEquals(AdStatus.PENDING, ad.getStatus());
        verify(adRepository).save(ad);
    }

    @Test
    void updateAd_doesNotSave_whenNothingActuallyChanged() {
        when(adRepository.findById(100L)).thenReturn(Optional.of(ad));
        AdUpdateRequest request = new AdUpdateRequest();
        request.setTitle(ad.getTitle()); // same as current title -> no real change

        adService.updateAd(100L, "kimia", request);

        verify(adRepository, never()).save(any());
        assertEquals(AdStatus.APPROVED, ad.getStatus()); // unchanged
    }
}
