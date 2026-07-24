package com.example.sales.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after creating a new advertisement.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdInsertResponse {
    Long id;
}
