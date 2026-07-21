package com.example.sales.stats;


import com.example.sales.ad.AdRepository;
import com.example.sales.ad.model.AdStatus;
import com.example.sales.ad.reported.AdReportRepository;
import com.example.sales.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DashboardStatsService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdReportRepository reportRepository;

    public DashboardStatistics getStats() {
        long numActiveUsers = userRepository.countByEnabledTrue();
        long numBlockedUsers = userRepository.countByEnabledFalse();
        long numAds = adRepository.count();
        long numPendingAds = adRepository.countByStatus(AdStatus.PENDING);
        long numReports = reportRepository.count();

        return DashboardStatistics.builder()
                .numActiveUsers((int) numActiveUsers)
                .numBlockedUsers((int) numBlockedUsers)
                .numAds((int) numAds)
                .numPendingAds((int) numPendingAds)
                .numReports((int) numReports)
                .build();
    }
}
