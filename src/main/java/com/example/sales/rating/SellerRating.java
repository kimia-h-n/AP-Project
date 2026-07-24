package com.example.sales.rating;


import com.example.sales.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a rating submitted by a user for a seller.
 * <p>
 * Each record stores a single rating value in the range of 1 to 5.
 * </p>
 */
@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SellerRating {

    /**
     * Unique identifier of the rating record.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Rating value assigned to the seller, typically from 1 to 5.
     */
    private Integer rating; // 1 -> 5

    /**
     * The user who submitted the rating.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The seller who received the rating.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
}
