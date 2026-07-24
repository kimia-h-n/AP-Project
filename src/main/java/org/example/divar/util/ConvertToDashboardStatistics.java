package org.example.divar.util;

import org.example.divar.model.DashboardStatistics;
import org.json.JSONObject;

public class ConvertToDashboardStatistics {

    public static DashboardStatistics convert(JSONObject json) {
        if (json == null) return null;

        DashboardStatistics stats = new DashboardStatistics();

        if (json.has("numActiveUsers") && !json.isNull("numActiveUsers")) {
            stats.setNumActiveUsers(json.getInt("numActiveUsers"));
        } else {
            stats.setNumActiveUsers(0);
        }

        if (json.has("numBlockedUsers") && !json.isNull("numBlockedUsers")) {
            stats.setNumBlockedUsers(json.getInt("numBlockedUsers"));
        } else {
            stats.setNumBlockedUsers(0);
        }

        if (json.has("numAds") && !json.isNull("numAds")) {
            stats.setNumAds(json.getInt("numAds"));
        } else {
            stats.setNumAds(0);
        }

        if (json.has("numPendingAds") && !json.isNull("numPendingAds")) {
            stats.setNumPendingAds(json.getInt("numPendingAds"));
        } else {
            stats.setNumPendingAds(0);
        }

        if (json.has("numReports") && !json.isNull("numReports")) {
            stats.setNumReports(json.getInt("numReports"));
        } else {
            stats.setNumReports(0);
        }

        return stats;
    }
}