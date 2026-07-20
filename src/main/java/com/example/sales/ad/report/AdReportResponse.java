package com.example.sales.ad.report;


import com.example.sales.user.UserName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdReportResponse {
    private Long adReportId;
    private Long adId;
    private String adTitle;
    //todo: later change to UserName
//     private UserName userName;
    private String sellerFirstName;
    private String sellerLastname;
    private ReportReason reportReason;
}
