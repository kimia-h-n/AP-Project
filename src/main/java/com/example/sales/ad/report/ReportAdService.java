package com.example.sales.ad.report;

import com.example.sales.ad.AdRepository;
import com.example.sales.ad.model.Ad;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.SpamNotAllowedException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReportAdService {

    private final AdRepository adRepository;
    private final AdReportRepository adReportRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reportAd(Long adId, ReportReason reportReason, String username) {
        Ad ad = adRepository.findById(adId).orElseThrow(AdNotFoundException::new);
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (ad.getSeller().getUsername().equals(username) || !ad.isAdSpammable())
            throw new SpamNotAllowedException();
        ad.spam();

        AdReport adReport = AdReport.builder()
                .ad(ad)
                .reporter(user)
                .reason(reportReason)
                .build();

        adReportRepository.save(adReport);

    }
}
