package com.example.sales.admin;


import com.example.sales.ad.Ad;
import com.example.sales.ad.AdRepository;
import com.example.sales.ad.dto.AdCardSummary;
import com.example.sales.ad.dto.PendingAdResponse;
import com.example.sales.ad.mapper.AdMapper;
import com.example.sales.rating.SellerRatingService;
import com.example.sales.user.UserInfoResponse;
import com.example.sales.user.UserSummary;
import org.springframework.transaction.annotation.Transactional;
import com.example.sales.ad.model.*;
import com.example.sales.ad.moderation.AdModerationRequest;
import com.example.sales.ad.reported.model.AdReport;
import com.example.sales.ad.reported.AdReportRepository;
import com.example.sales.ad.reported.dto.AdReportResponse;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyBlockedException;
import com.example.sales.exception.UserAlreadyEnabled;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import com.example.sales.user.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdRepository adRepository;
    private final AdReportRepository adReportRepository;
    private final AdMapper adMapper;
    private final AdPrimaryImageEnricher primaryImageEnricher;
    private final SellerRatingService sellerRatingService;

    public List<UserSummary> getAllUsers() {
        return userMapper.toUserSummary(userRepository.findAll());
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

    public List<PendingAdResponse> getAllPendingAds() {
        List<PendingAdResponse> ads = adMapper.toPendingAdList(adRepository.findAllByStatus(AdStatus.PENDING));
        primaryImageEnricher.enrich(
                ads,
                PendingAdResponse::getId,
                PendingAdResponse::setPrimaryImageId,
                PendingAdResponse::setPrimaryImageUrl
        );
        return ads;
    }

    @Transactional(readOnly = true)
    public List<AdReportResponse> getReportedAds() {
        List<AdReport> reports =
                adReportRepository.findAllWithAdAndSeller();

        List<AdReportResponse> responses =
                adMapper.toAdReportResponseList(reports);

        primaryImageEnricher.enrich(
                responses,
                AdReportResponse::getAdId,
                AdReportResponse::setPrimaryImageId,
                AdReportResponse::setPrimaryImageUrl
        );

        return responses;
    }

    public UserInfoResponse getUserInfo(Long id) {
        Double averageRating = sellerRatingService.calculateSellerRatingAvg(id);
        UserInfoResponse response = userMapper.toUserResponse(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
        response.setAvgRating(averageRating);
        return response;
    }

    public List<AdCardSummary> getUserAds(Long id) {
        List<Ad> myAdsList = adRepository.findAllBySellerAndStatus(
                userRepository.findById(id).orElseThrow(UserNotFoundException::new),
                AdStatus.APPROVED);
        List<AdCardSummary> ads = adMapper.toCartSummeryList(myAdsList);
        primaryImageEnricher.enrich(
                ads,
                AdCardSummary::getId,
                AdCardSummary::setPrimaryImageId,
                AdCardSummary::setPrimaryImageUrl
        );
        return ads;
    }
}
