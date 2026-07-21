package com.example.sales.ad.reported.dto;


import com.example.sales.ad.reported.model.ReportReason;
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
