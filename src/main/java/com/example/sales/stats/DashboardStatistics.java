package com.example.sales.stats;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatistics {
    private Integer numActiveUsers;
    private Integer numBlockedUsers;
    private Integer numAds;
    private Integer numPendingAds;
    private Integer numReports;
}
