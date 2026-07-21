package com.example.sales.rating;


import com.example.sales.exception.AlreadyVotedException;
import com.example.sales.exception.OperationNotAllowedException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
//@AllArgsConstructor
//public class SellerRatingService {
//
//    SellerRatingRepository ratingRepository;
//    UserRepository userRepository;
//
//    //todo: later for editing rating
//    public void submitRating(SellerRatingRequest request, String username) {
//        //if user has already voted to this person!
//        //if user == seller
//
//        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
//        User seller = userRepository.findById(request.getSellerId()).orElseThrow(UserNotFoundException::new);
//
//        if (user.getId().equals(seller.getId()))
//            throw new OperationNotAllowedException();
//
//        if (ratingRepository.existsBySellerAndUser(seller, user))
//            throw new AlreadyVotedException();
//
//
//        SellerRating sellerRating = SellerRating.builder()
//                .seller(seller)
//                .user(user)
//                .rating(request.getRating())
//                .build();
//
//        ratingRepository.save(sellerRating);
//    }
//
//    /**
//     *
//     * @param sellerId
//     * @return avgRatingScore for seller, 0.0 if no rating
//     */
//    public Double calculateSellerRatingAvg(Long sellerId) {
//        User seller = userRepository.findById(sellerId).orElseThrow(UserNotFoundException::new);
//
//        List<SellerRating> ratings = ratingRepository.findAllBySeller(seller);
//
//        return ratings.stream()
//                .map(SellerRating::getRating)
//                .filter(Objects::nonNull)
//                .mapToInt(Integer::intValue)
//                .average()
//                .orElse(0.0);
//    }
//
//}
@Service
@RequiredArgsConstructor
public class SellerRatingService {

    private final SellerRatingRepository ratingRepository;
    private final UserRepository userRepository;

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

    @Transactional(readOnly = true)
    public Double calculateSellerRatingAvg(Long sellerId) {
        if (!userRepository.existsById(sellerId)) {
            throw new UserNotFoundException();
        }

        Double average =
                ratingRepository.calculateAverageBySellerId(sellerId);

        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

}