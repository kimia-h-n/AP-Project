package com.example.sales.ad.model.moderation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdModerationRequest {
    private AdModerationChoice choice;
    private String rejectReason;
}
