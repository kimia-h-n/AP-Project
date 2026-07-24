package com.example.sales.ad.reported.dto;

import com.example.sales.ad.reported.model.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for ad report records.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdReportResponse {
    private Long adReportId;
    private Long adId;
    private String adTitle;
    private String sellerFirstName;
    private String sellerLastName;
    private Long sellerId;
    private UUID primaryImageId;
    private String primaryImageUrl;
    private ReportReason reportReason;
}
