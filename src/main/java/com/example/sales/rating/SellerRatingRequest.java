package com.example.sales.rating;


import lombok.Data;

/**
 * Request DTO used to submit a seller rating.
 */
@Data
public class SellerRatingRequest {

    /**
     * Identifier of the seller being rated.
     */
    private Long sellerId;

    /**
     * Rating value, typically between 1 and 5.
     */
    private Integer rating;
}
