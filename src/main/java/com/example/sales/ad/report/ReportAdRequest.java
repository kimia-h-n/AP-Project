package com.example.sales.ad.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportAdRequest {
    private Long adId;
    private ReportReason reportReason;
}
