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

public class AdUpdateRequest {
    private Long id;
    private String title;
    private String description;
    private String address;
    private Long price;
    private AdCategory category;
    private ProductCondition condition;
    private Long cityId;
}