import com.example.sales.exception.AlreadyVotedException;
import com.example.sales.exception.OperationNotAllowedException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.rating.SellerRating;
import com.example.sales.rating.SellerRatingRepository;
import com.example.sales.rating.SellerRatingRequest;
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
 * Unit tests for {@link SellerRatingService}.
 * Covers: submitting a rating (success, rating yourself, voting twice for the same seller)
 * and calculating a seller's average rating (with ratings, without ratings, missing seller).
 */
@ExtendWith(MockitoExtension.class)
class SellerRatingServiceTest {

    @Mock
    private SellerRatingRepository ratingRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SellerRatingService sellerRatingService;

    private User buyer;
    private User seller;
    private SellerRatingRequest request;

    @BeforeEach
    void setUp() {
        buyer = User.builder().id(1L).username("fateme").role(Role.USER).enabled(true).build();
        seller = User.builder().id(2L).username("kimia").role(Role.USER).enabled(true).build();
        request = new SellerRatingRequest();
        request.setSellerId(2L);
        request.setRating(5);
    }

    @Test
    void submitRating_savesRating_whenValidAndNotVotedBefore() {
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(buyer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(ratingRepository.existsBySellerAndUser(seller, buyer)).thenReturn(false);

        sellerRatingService.submitRating(request, "fateme");

        verify(ratingRepository).save(any(SellerRating.class));
    }

    @Test
    void submitRating_throwsOperationNotAllowedException_whenRatingYourself() {
        request.setSellerId(1L); // buyer trying to rate themselves
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(buyer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));

        assertThrows(OperationNotAllowedException.class,
                () -> sellerRatingService.submitRating(request, "fateme"));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void submitRating_throwsAlreadyVotedException_whenUserAlreadyRatedThisSeller() {
        when(userRepository.findByUsername("fateme")).thenReturn(Optional.of(buyer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(ratingRepository.existsBySellerAndUser(seller, buyer)).thenReturn(true);

        assertThrows(AlreadyVotedException.class,
                () -> sellerRatingService.submitRating(request, "fateme"));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void calculateSellerRatingAvg_returnsRoundedAverage_whenRatingsExist() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(ratingRepository.calculateAverageBySellerId(2L)).thenReturn(4.36);

        Double avg = sellerRatingService.calculateSellerRatingAvg(2L);

        assertEquals(4.4, avg);
    }

    @Test
    void calculateSellerRatingAvg_returnsZero_whenSellerHasNoRatings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(ratingRepository.calculateAverageBySellerId(2L)).thenReturn(null);

        Double avg = sellerRatingService.calculateSellerRatingAvg(2L);

        assertEquals(0.0, avg);
    }

    @Test
    void calculateSellerRatingAvg_throwsUserNotFoundException_whenSellerDoesNotExist() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> sellerRatingService.calculateSellerRatingAvg(99L));
    }
}
