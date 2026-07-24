package com.example.sales.rating;


import com.example.sales.exception.AlreadyVotedException;
import com.example.sales.exception.OperationNotAllowedException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.user.UserRepository;
import com.example.sales.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service containing business logic for seller rating submission and averaging.
 */
@Service
@RequiredArgsConstructor
public class SellerRatingService {

    private final SellerRatingRepository ratingRepository;
    private final UserRepository userRepository;

    /**
     * Submits a new rating for a seller from the authenticated user.
     * <p>
     * The method prevents self-rating and duplicate ratings from the same user.
     * </p>
     *
     * @param request rating submission payload
     * @param username authenticated username
     * @throws UserNotFoundException if the user or seller does not exist
     * @throws OperationNotAllowedException if the user tries to rate themselves
     * @throws AlreadyVotedException if the user has already rated this seller
     */
    @Transactional
    public void submitRating(SellerRatingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(UserNotFoundException::new);

        if (user.getId().equals(seller.getId())) {
            throw new OperationNotAllowedException();
        }

        if (ratingRepository.existsBySellerAndUser(seller, user)) {
            throw new AlreadyVotedException();
        }

        SellerRating sellerRating = SellerRating.builder()
                .seller(seller)
                .user(user)
                .rating(request.getRating())
                .build();

        ratingRepository.save(sellerRating);
    }

    /**
     * Calculates the average rating for a seller and rounds it to one decimal place.
     *
     * @param sellerId seller identifier
     * @return average rating rounded to one decimal place, or 0.0 if no ratings exist
     * @throws UserNotFoundException if the seller does not exist
     */
    @Transactional(readOnly = true)
    public Double calculateSellerRatingAvg(Long sellerId) {
        if (!userRepository.existsById(sellerId)) {
            throw new UserNotFoundException();
        }

        Double average = ratingRepository.calculateAverageBySellerId(sellerId);

        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }
}
