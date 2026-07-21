package com.example.sales.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private UUID id;
    private String url;
    private Integer sortOrder;
    private boolean primary;
}