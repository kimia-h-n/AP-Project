package com.example.sales.rating;


import com.example.sales.ad.report.ReportAdService;
import com.example.sales.exception.AlreadyVotedException;
import com.example.sales.exception.OperationNotAllowedException;
import com.example.sales.exception.UserAlreadyEnabled;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import com.example.sales.user.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SellerRatingService {

    private final ReportAdService reportAdService;
    SellerRatingRepository ratingRepository;
    UserRepository userRepository;

    //todo: later for editing rating
    public void submitRating(SellerRatingRequest request, String username) {
        //if user has already voted to this person!
        //if user == seller

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findById(request.getSellerId()).orElseThrow(UserNotFoundException::new);

        if (user.getId().equals(seller.getId()))
            throw new OperationNotAllowedException();

        if (ratingRepository.existsBySellerAndUser(seller, user))
            throw new AlreadyVotedException();


        SellerRating sellerRating = SellerRating.builder()
                .seller(seller)
                .user(user)
                .rating(request.getRating())
                .build();

        ratingRepository.save(sellerRating);
    }

    /**
     *
     * @param sellerId
     * @return avgRatingScore for seller, 0.0 if no rating
     */
    public Double calculateSellerRatingAvg(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(UserNotFoundException::new);

        List<SellerRating> ratings = ratingRepository.findAllBySeller(seller);

        return ratings.stream()
                .map(SellerRating::getRating)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

}
