package com.example.sales.ad.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdCartSummery {
    private Long id;

    private String title;
    private long price;
    private Instant createdAt;
    private Instant updatedAt;
    private City city;
}
