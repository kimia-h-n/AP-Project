package com.example.sales.ad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private long price;
    private AdCategory category;
    private ProductCondition condition;
    private String cityName;
    private List<ImageResponse> images;
    private AdStatus status;
    private String sellerUsername;
    private String sellerFirstname;
    private String sellerLastname;
    private Long sellerId;
    private Double sellerRatingAvg;
    private boolean isFavorite;
    private boolean isMine;
    private Instant createdAt;
    private Instant updatedAt;
}
