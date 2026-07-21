package com.example.sales.test_case;

import com.example.sales.ad.AdRepository;
import com.example.sales.ad.AdService;
import com.example.sales.ad.fav.FavoriteRepository;
import com.example.sales.ad.model.*;
import com.example.sales.exception.*;
import com.example.sales.picture.StorageRepository;
import com.example.sales.province.City;
import com.example.sales.province.ProvinceRepository;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.Role;
import com.example.sales.user.User;
import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private AdService adService;

    private User seller;
    private Ad ad;

    @BeforeEach
    void setUp() {
        seller = User.builder()
                .id(1L)
                .username("seller1")
                .role(Role.USER)
                .build();

        ad = Ad.builder()
                .id(10L)
                .title("Old title")
                .description("Old description")
                .price(1000L)
                .status(AdStatus.APPROVED)
                .seller(seller)
                .city(City.builder().id(2L).name("Tehran").build())
                .build();
    }

    // ---------- addAd ----------

    @Test
    void addAd_userNotFound_throwsUserNotFoundException() {
        AdRequest request = AdRequest.builder().cityId(2L).build();
        when(userRepository.findByUsername("seller1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adService.addAd(request, "seller1"));
        verifyNoInteractions(adRepository);
    }

    @Test
    void addAd_cityNotFound_throwsCityNotFoundException() {
        AdRequest request = AdRequest.builder().cityId(99L).build();
        when(userRepository.findByUsername("seller1")).thenReturn(Optional.of(seller));
        when(adMapper.toEntity(request)).thenReturn(new Ad());
        when(provinceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> adService.addAd(request, "seller1"));
        verify(adRepository, never()).save(any());
    }

    @Test
    void addAd_success_savesAdWithPendingStatusAndReturnsId() {
        AdRequest request = AdRequest.builder().title("New ad").cityId(2L).build();
        City city = City.builder().id(2L).name("Tehran").build();
        Ad mappedAd = new Ad();

        when(userRepository.findByUsername("seller1")).thenReturn(Optional.of(seller));
        when(adMapper.toEntity(request)).thenReturn(mappedAd);
        when(provinceRepository.findById(2L)).thenReturn(Optional.of(city));
        when(adRepository.save(mappedAd)).thenAnswer(invocation -> {
            Ad savedAd = invocation.getArgument(0);
            savedAd.setId(55L);
            return savedAd;
        });

        AdInsertResponse response = adService.addAd(request, "seller1");

        assertEquals(55L, response.getId());
        assertEquals(AdStatus.PENDING, mappedAd.getStatus());
        assertEquals(seller, mappedAd.getSeller());
        assertEquals(city, mappedAd.getCity());
    }

    // ---------- getAd ----------

    @Test
    void getAd_notFound_throwsAdNotFoundException() {
        when(adRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(AdNotFoundException.class, () -> adService.getAd(10L, "someone"));
    }

    @Test
    void getAd_anonymousUser_doesNotTouchUserOrFavoriteRepositories() {
        AdResponse mapped = new AdResponse();
        mapped.setSellerUsername("seller1");
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(adMapper.toResponse(ad)).thenReturn(mapped);
        when(storageRepository.findMetaByAdId(10L)).thenReturn(List.of());

        AdResponse result = adService.getAd(10L, null);

        assertNotNull(result);
        verifyNoInteractions(userRepository, favoriteRepository);
    }

    @Test
    void getAd_loggedInOwner_marksAdAsMine() {
        AdResponse mapped = new AdResponse();
        mapped.setSellerUsername("seller1");
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(adMapper.toResponse(ad)).thenReturn(mapped);
        when(storageRepository.findMetaByAdId(10L)).thenReturn(List.of());
        when(userRepository.findByUsername("seller1")).thenReturn(Optional.of(seller));
        when(favoriteRepository.existsFavoriteAdByUserAndAd(seller, ad)).thenReturn(false);

        AdResponse result = adService.getAd(10L, "seller1");

        assertTrue(result.isMine());
        assertFalse(result.isFavorite());
    }

    // ---------- removeAd ----------

    @Test
    void removeAd_notOwnerAndNotAdmin_throwsOperationNotAllowedException() {
        User otherUser = User.builder().username("intruder").role(Role.USER).build();
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(userRepository.findByUsername("intruder")).thenReturn(Optional.of(otherUser));

        assertThrows(OperationNotAllowedException.class, () -> adService.removeAd(10L, "intruder"));
        assertNotEquals(AdStatus.REMOVED, ad.getStatus());
    }

    @Test
    void removeAd_alreadyRemoved_throwsAdNotRemovableException() {
        ad.setStatus(AdStatus.REMOVED);
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));

        assertThrows(AdNotRemovableException.class, () -> adService.removeAd(10L, "seller1"));
    }

    @Test
    void removeAd_byOwner_setsStatusToRemoved() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));

        adService.removeAd(10L, "seller1");

        assertEquals(AdStatus.REMOVED, ad.getStatus());
    }

    @Test
    void removeAd_byAdmin_setsStatusToRemoved() {
        User admin = User.builder().username("admin1").role(Role.ADMIN).build();
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));

        adService.removeAd(10L, "admin1");

        assertEquals(AdStatus.REMOVED, ad.getStatus());
    }

    // ---------- updateAd ----------

    @Test
    void updateAd_notOwner_throwsOperationNotAllowedException() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        AdUpdateRequest request = AdUpdateRequest.builder().title("Hacked title").build();

        assertThrows(OperationNotAllowedException.class, () -> adService.updateAd(10L, "intruder", request));
        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_noFieldsChanged_doesNotSave() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        AdUpdateRequest request = AdUpdateRequest.builder().title(ad.getTitle()).build();

        adService.updateAd(10L, "seller1", request);

        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_approvedAdWithContentChange_revertsStatusToPending() {
        when(adRepository.findById(10L)).thenReturn(Optional.of(ad));
        AdUpdateRequest request = AdUpdateRequest.builder().title("Updated title").build();

        adService.updateAd(10L, "seller1", request);

        assertEquals("Updated title", ad.getTitle());
        assertEquals(AdStatus.PENDING, ad.getStatus());
        verify(adRepository).save(ad);
    }
}
