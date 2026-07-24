package com.example.sales.province;


import lombok.Data;

import lombok.Data;

/**
 * Response DTO representing a province/city item.
 */
@Data
public class ProvinceResponse {
    private Long id;

    /**
     * Province display name.
     */
    private String name;
}
