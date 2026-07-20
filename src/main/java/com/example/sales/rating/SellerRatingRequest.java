package com.example.sales.rating;


import com.example.sales.user.User;
import lombok.Builder;
import lombok.Data;

@Data
public class SellerRatingRequest {
    private Long sellerId;
    private Integer rating;
}
