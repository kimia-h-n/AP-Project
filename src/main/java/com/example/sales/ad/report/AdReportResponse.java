package com.example.sales.ad.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdReportResponse {
    private Long adReportId;
    private Long adId;
    private String adTitle;
    private String sellerFirstName;
    private String sellerLastName;
    private UUID primaryImageId;
    private String primaryImageUrl;
    private ReportReason reportReason;
}
