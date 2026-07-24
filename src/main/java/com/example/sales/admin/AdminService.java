package com.example.sales.admin;

import com.example.sales.ad.Ad;
import com.example.sales.ad.AdRepository;
import com.example.sales.ad.dto.AdCardSummary;
import com.example.sales.ad.dto.PendingAdResponse;
import com.example.sales.ad.mapper.AdMapper;
import com.example.sales.ad.moderation.AdModerationRequest;
import com.example.sales.ad.model.AdStatus;
import com.example.sales.ad.reported.AdReportRepository;
import com.example.sales.ad.reported.dto.AdReportResponse;
import com.example.sales.ad.reported.model.AdReport;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.AlreadyBlockedException;
import com.example.sales.exception.OperationNotAllowedException;
import com.example.sales.exception.UserAlreadyEnabled;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.picture.AdPrimaryImageEnricher;
import com.example.sales.rating.SellerRatingService;
import com.example.sales.user.UserRepository;
import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import com.example.sales.user.dto.UserInfoResponse;
import com.example.sales.user.UserMapper;
import com.example.sales.user.dto.UserSummary;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for administrative operations.
 * <p>
 * This service contains business logic for managing users, moderating ads,
 * reviewing reported ads, and retrieving admin-related user and ad data.
 * </p>
 */
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

    /**
     * Returns all regular users in the system.
     *
     * @return list of user summaries
     */
    public List<UserSummary> getAllUsers() {
        return userMapper.toUserSummary(userRepository.findAllByRole(Role.USER));
    }

    /**
     * Blocks a user account.
     * <p>
     * Admin accounts cannot be blocked, and already blocked users are rejected.
     * </p>
     *
     * @param id user identifier
     * @throws UserNotFoundException if the user does not exist
     * @throws OperationNotAllowedException if the target user is an admin
     * @throws AlreadyBlockedException if the user is already blocked
     */
    @Transactional
    public void blockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.getRole() == Role.ADMIN) {
            throw new OperationNotAllowedException();
        }

        if (!user.isEnabled()) {
            throw new AlreadyBlockedException();
        }

        user.setEnabled(false);
    }

    /**
     * Unblocks a previously blocked user account.
     *
     * @param id user identifier
     * @throws UserNotFoundException if the user does not exist
     * @throws UserAlreadyEnabled if the user is already enabled
     */
    @Transactional
    public void unblockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isEnabled()) {
            throw new UserAlreadyEnabled();
        }

        user.setEnabled(true);
    }

    /**
     * Applies a moderation decision to an advertisement.
     * <p>
     * Approved ads are marked as approved and their rejection reason is cleared.
     * Rejected ads are marked as rejected and the provided rejection reason is stored.
     * </p>
     *
     * @param id advertisement identifier
     * @param moderationRequest moderation decision and optional rejection reason
     * @throws AdNotFoundException if the ad does not exist
     */
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

    /**
     * Returns all ads that are currently pending moderation.
     * <p>
     * The returned items are enriched with primary image metadata when available.
     * </p>
     *
     * @return list of pending ad responses
     */
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

    /**
     * Returns all reported ads together with their report details.
     * <p>
     * The result is enriched with primary image metadata when available.
     * </p>
     *
     * @return list of reported ad responses
     */
    @Transactional(readOnly = true)
    public List<AdReportResponse> getReportedAds() {
        List<AdReport> reports = adReportRepository.findAllWithAdAndSeller();
        List<AdReportResponse> responses = adMapper.toAdReportResponseList(reports);

        primaryImageEnricher.enrich(
                responses,
                AdReportResponse::getAdId,
                AdReportResponse::setPrimaryImageId,
                AdReportResponse::setPrimaryImageUrl
        );

        return responses;
    }

    /**
     * Returns detailed information for a specific user, including their average seller rating.
     *
     * @param id user identifier
     * @return detailed user information
     * @throws UserNotFoundException if the user does not exist
     */
    public UserInfoResponse getUserInfo(Long id) {
        Double averageRating = sellerRatingService.calculateSellerRatingAvg(id);
        UserInfoResponse response = userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(UserNotFoundException::new)
        );
        response.setAvgRating(averageRating);
        return response;
    }

    /**
     * Returns all approved ads for a specific user.
     * <p>
     * The returned ads are enriched with primary image metadata when available.
     * </p>
     *
     * @param id user identifier
     * @return list of the user's approved ads
     * @throws UserNotFoundException if the user does not exist
     */
    public List<AdCardSummary> getUserAds(Long id) {
        List<Ad> myAdsList = adRepository.findAllBySellerAndStatus(
                userRepository.findById(id).orElseThrow(UserNotFoundException::new),
                AdStatus.APPROVED
        );

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
