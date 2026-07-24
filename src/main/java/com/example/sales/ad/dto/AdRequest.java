package com.example.sales.ad.dto;

import com.example.sales.ad.model.AdCategory;
import com.example.sales.ad.model.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
 * Request DTO used for creating or updating an advertisement.
 * <p>
 * This object contains the core user-provided fields required to register an ad.
 * </p>
 */

public class AdRequest {
    private String title;
    private String description;
    private String address;
    private long price;
    private AdCategory category;
    private ProductCondition condition;
    private Long cityId;
}
