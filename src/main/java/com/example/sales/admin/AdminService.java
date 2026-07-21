package com.example.sales.admin;


import com.example.sales.ad.AdRepository;
import com.example.sales.ad.AdService;
import com.example.sales.ad.model.*;
import com.example.sales.ad.model.moderation.AdModerationRequest;
import com.example.sales.ad.report.AdReport;
import com.example.sales.ad.report.AdReportRepository;
import com.example.sales.ad.report.AdReportResponse;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyBlockedException;
import com.example.sales.exception.UserAlreadyEnabled;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import com.example.sales.user.UserMapper;
import com.example.sales.user.UserResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdRepository adRepository;
    private final AdReportRepository adReportRepository;
    private final AdMapper adMapper;
    private final AdPrimaryImageEnricher adPrimaryImageEnricher;

    public List<UserResponse> getAllUsers() {
        return userMapper.toUserResponse(userRepository.findAll());
    }


    @Transactional
    public void blockUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (!user.isEnabled())
            throw new AlreadyBlockedException();
        user.setEnabled(false);

    }

    @Transactional
    public void unblockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isEnabled())
            throw new UserAlreadyEnabled();
        user.setEnabled(true);
    }


    public void moderateAd(Long id, AdModerationRequest moderationRequest) {

        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        switch (moderationRequest.getChoice()) {
            case APPROVE -> {
                ad.setStatus(AdStatus.APPROVED);
                ad.setRejectionReason(null);
            }
            case REJECT -> {
                ad.setStatus(AdStatus.REJECTED);
                ad.setRejectionReason(moderationRequest.getRejectReason());
            }
        }
        adRepository.save(ad);
    }

    public List<PendingAd> getAllPendingAds() {
        List<PendingAd> ads = adMapper.toPendingAdList(adRepository.findAllByStatus(AdStatus.PENDING));
        adPrimaryImageEnricher.enrich(
                ads,
                PendingAd::getId,
                PendingAd::setPrimaryImageId,
                PendingAd::setPrimaryImageUrl
        );
        return ads;
    }

    public List<AdReportResponse> getReportedAds() {
        List<AdReportResponse> ads = adMapper.toAdReportResponseList(adReportRepository.findAll());
        adPrimaryImageEnricher.enrich(
                ads,
                AdReportResponse::getAdId,
                AdReportResponse::setPrimaryImageId,
                AdReportResponse::setPrimaryImageUrl
        );
        return ads;
    }
}
