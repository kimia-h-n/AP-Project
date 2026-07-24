package com.example.sales.ad.dto;

import com.example.sales.ad.model.AdCategory;
import com.example.sales.ad.model.AdStatus;
import com.example.sales.ad.model.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
/**
 * Detailed response DTO representing a full advertisement view.
 * <p>
 * This object is typically used in ad detail pages where both ad information
 * and seller-related metadata are required.
 * </p>
 */
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
