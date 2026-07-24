package com.example.sales.ad.dto;


import com.example.sales.ad.model.AdCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;
/**
 * DTO used to represent a compact summary card for an advertisement.
 * <p>
 * This class is typically used in listing pages, search results, or any UI section
 * where only the most important ad information is needed.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdCardSummary {
    private Long id;
    private String title;
    private long price;
    private Instant createdAt;
    private Instant updatedAt;
    private String cityName;
    private AdCategory category;
    private UUID primaryImageId;
    private String primaryImageUrl;
}
