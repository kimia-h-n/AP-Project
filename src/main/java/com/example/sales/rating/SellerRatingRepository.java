package com.example.sales.rating;


import com.example.sales.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {

    boolean existsBySellerAndUser(User seller, User user);

    List<SellerRating> findAllBySeller(User seller);

}
