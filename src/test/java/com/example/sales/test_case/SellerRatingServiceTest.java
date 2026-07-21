package com.example.sales.test_case;

import com.example.sales.ad.report.ReportAdService;
import com.example.sales.exception.AlreadyVotedException;
import com.example.sales.exception.OperationNotAllowedException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.rating.SellerRating;
import com.example.sales.rating.SellerRatingRepository;
import com.example.sales.rating.SellerRatingRequest;
import com.example.sales.rating.SellerRatingService;
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
class SellerRatingServiceTest {

    @Mock
    private ReportAdService reportAdService;
    @Mock
    private SellerRatingRepository ratingRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SellerRatingService sellerRatingService;

    // ---------- submitRating ----------

    @Test
    void submitRating_userRatesSelf_throwsOperationNotAllowedException() {
        User user = User.builder().id(1L).username("john").build();
        SellerRatingRequest request = new SellerRatingRequest();
        request.setSellerId(1L);
        request.setRating(5);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(OperationNotAllowedException.class, () -> sellerRatingService.submitRating(request, "john"));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void submitRating_alreadyVoted_throwsAlreadyVotedException() {
        User user = User.builder().id(1L).username("john").build();
        User seller = User.builder().id(2L).username("seller1").build();
        SellerRatingRequest request = new SellerRatingRequest();
        request.setSellerId(2L);
        request.setRating(4);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(ratingRepository.existsBySellerAndUser(seller, user)).thenReturn(true);

        assertThrows(AlreadyVotedException.class, () -> sellerRatingService.submitRating(request, "john"));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void submitRating_validRating_savesRating() {
        User user = User.builder().id(1L).username("john").build();
        User seller = User.builder().id(2L).username("seller1").build();
        SellerRatingRequest request = new SellerRatingRequest();
        request.setSellerId(2L);
        request.setRating(4);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(ratingRepository.existsBySellerAndUser(seller, user)).thenReturn(false);

        sellerRatingService.submitRating(request, "john");

        verify(ratingRepository).save(argThat(sellerRating ->
                sellerRating.getUser().equals(user)
                        && sellerRating.getSeller().equals(seller)
                        && sellerRating.getRating().equals(4)
        ));
    }

    @Test
    void submitRating_sellerNotFound_throwsUserNotFoundException() {
        User user = User.builder().id(1L).username("john").build();
        SellerRatingRequest request = new SellerRatingRequest();
        request.setSellerId(99L);
        request.setRating(3);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> sellerRatingService.submitRating(request, "john"));
    }

    // ---------- calculateSellerRatingAvg ----------

    @Test
    void calculateSellerRatingAvg_noRatings_returnsZero() {
        User seller = User.builder().id(2L).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(ratingRepository.findAllBySeller(seller)).thenReturn(List.of());

        Double avg = sellerRatingService.calculateSellerRatingAvg(2L);

        assertEquals(0.0, avg);
    }

    @Test
    void calculateSellerRatingAvg_withRatings_returnsCorrectAverage() {
        User seller = User.builder().id(2L).build();
        SellerRating r1 = SellerRating.builder().rating(5).build();
        SellerRating r2 = SellerRating.builder().rating(3).build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(ratingRepository.findAllBySeller(seller)).thenReturn(List.of(r1, r2));

        Double avg = sellerRatingService.calculateSellerRatingAvg(2L);

        assertEquals(4.0, avg);
    }

    @Test
    void calculateSellerRatingAvg_sellerNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> sellerRatingService.calculateSellerRatingAvg(2L));
    }
}
