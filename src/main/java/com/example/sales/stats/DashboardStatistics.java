package com.example.sales.stats;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 * DTO that holds aggregated statistics for the admin dashboard.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatistics {

    /**
     * Number of enabled users.
     */
    private Integer numActiveUsers;

    /**
     * Number of disabled or blocked users.
     */
    private Integer numBlockedUsers;

    /**
     * Total number of advertisements.
     */
    private Integer numAds;

    /**
     * Number of advertisements waiting for approval.
     */
    private Integer numPendingAds;

    /**
     * Total number of reports submitted in the system.
     */
    private Integer numReports;
}
