package com.example.sales.province;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 * DTO used to deserialize city seed data from JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitySeedDto {

    /**
     * English label of the city.
     */
    private String label;

    /**
     * Persian name of the city.
     */
    private String name;
}

