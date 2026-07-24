package com.example.sales.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO representing a user summary.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSummary {
    private Long id;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private boolean enabled;
}
