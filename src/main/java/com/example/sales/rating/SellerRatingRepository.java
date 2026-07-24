package com.example.sales.rating;


import com.example.sales.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for persisting and querying {@link SellerRating} entities.
 */
public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {

    /**
     * Checks whether a specific user has already rated a seller.
     *
     * @param seller seller being rated
     * @param user user who submitted the rating
     * @return true if a rating already exists, otherwise false
     */
    boolean existsBySellerAndUser(User seller, User user);

    /**
     * Calculates the average rating value for a seller.
     *
     * @param sellerId seller identifier
     * @return average rating, or null if no ratings exist
     */
    @Query("""
            select avg(sr.rating)
            from SellerRating sr
            where sr.seller.id = :sellerId
            """)
    Double calculateAverageBySellerId(@Param("sellerId") Long sellerId);
}

