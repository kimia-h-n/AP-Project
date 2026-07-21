package com.example.sales.ad.dto;


import com.example.sales.ad.model.AdCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingAdResponse {
    private Long id;
    private String title;
    private AdCategory category;
    private String sellerFirstName;
    private String sellerLastName;
    private String cityName;
    private Instant createdAt;
    private Instant updatedAt;
    private Long sellerId;
    private UUID primaryImageId;
    private String primaryImageUrl;
}
