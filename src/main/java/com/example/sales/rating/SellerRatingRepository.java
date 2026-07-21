package com.example.sales.rating;


import com.example.sales.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {
//

    boolean existsBySellerAndUser(User seller, User user);

    @Query("""
            select avg(sr.rating)
            from SellerRating sr
            where sr.seller.id = :sellerId
            """)
    Double calculateAverageBySellerId(@Param("sellerId") Long sellerId);
}
