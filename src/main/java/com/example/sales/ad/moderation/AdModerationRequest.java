package com.example.sales.ad.moderation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload used for moderation of an advertisement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdModerationRequest {
    private AdModerationChoice choice;
    private String rejectReason;
}
