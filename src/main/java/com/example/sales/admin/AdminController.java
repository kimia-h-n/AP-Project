package com.example.sales.admin;

import com.example.sales.ad.dto.AdCardSummary;
import com.example.sales.ad.dto.PendingAdResponse;
import com.example.sales.ad.moderation.AdModerationRequest;
import com.example.sales.ad.reported.dto.AdReportResponse;
import com.example.sales.user.dto.UserInfoResponse;
import com.example.sales.user.dto.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for admin operations.
 * <p>
 * This controller exposes administrative endpoints for managing users,
 * reviewing ads, moderating pending ads, and handling reported ads.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Returns a list of all registered users.
     *
     * @return list of user summaries
     */
    @GetMapping("/users")
    public List<UserSummary> getAllUsers() {
        return adminService.getAllUsers();
    }

    /**
     * Returns detailed information for a specific user.
     *
     * @param id user identifier
     * @return detailed user information
     */
    @GetMapping("/users/{id}")
    public UserInfoResponse getUserInfo(@PathVariable Long id) {
        return adminService.getUserInfo(id);
    }

    /**
     * Returns all ads belonging to a specific user.
     *
     * @param id user identifier
     * @return list of the user's ads
     */
    @GetMapping("/users/{id}/ads")
    public List<AdCardSummary> getUserAds(@PathVariable Long id) {
        return adminService.getUserAds(id);
    }

    /**
     * Blocks a user account.
     *
     * @param id user identifier
     */
    @PostMapping("/users/block/{id}")
    public void blockUser(@PathVariable Long id) {
        adminService.blockUser(id);
    }

    /**
     * Unblocks a previously blocked user account.
     *
     * @param id user identifier
     */
    @PostMapping("/users/unblock/{id}")
    public void unblockUser(@PathVariable Long id) {
        adminService.unblockUser(id);
    }

    /**
     * Moderates an advertisement.
     * <p>
     * This endpoint is intended to be accessed only by administrators.
     * </p>
     *
     * @param id advertisement identifier
     * @param moderationRequest moderation decision and optional rejection reason
     */
    @PostMapping("/ads/{id}/moderation")
    public void moderateAd(@PathVariable Long id,
                           @RequestBody AdModerationRequest moderationRequest) {
        adminService.moderateAd(id, moderationRequest);
    }

    /**
     * Returns all reported advertisements.
     *
     * @return list of reported ad responses
     */
    @GetMapping("/reported-ads")
    public List<AdReportResponse> getReportedAds() {
        return adminService.getReportedAds();
    }

    /**
     * Returns all ads that are waiting for moderation.
     *
     * @return list of pending ads
     */
    @GetMapping("/ads/moderation")
    public List<PendingAdResponse> getPendingAds() {
        return adminService.getAllPendingAds();
    }
}
