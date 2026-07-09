package com.example.sales.ad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private City city;
    private List<String> imagePaths;
    private AdStatus status;
    private String sellerUsername;
    private boolean isFavorite;
}
