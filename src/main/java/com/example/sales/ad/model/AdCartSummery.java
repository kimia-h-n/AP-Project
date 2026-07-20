package com.example.sales.ad.model;


import com.example.sales.province.City;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdCartSummery {
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
