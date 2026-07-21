package com.example.sales.admin;


import com.example.sales.ad.model.AdCardSummary;
import com.example.sales.ad.model.PendingAd;
import com.example.sales.ad.model.moderation.AdModerationRequest;
import com.example.sales.ad.report.AdReportResponse;
import com.example.sales.user.UserInfoResponse;
import com.example.sales.user.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserSummary> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserInfoResponse getUserInfo(@PathVariable Long id) {
        return adminService.getUserInfo(id);
    }

    @GetMapping("/users/{id}/ads")
    public List<AdCardSummary> getUserAds(@PathVariable Long id) {
        return adminService.getUserAds(id);
    }

    @PostMapping("/users/block/{id}")
    public void blockUser(@PathVariable Long id) {
        adminService.blockUser(id);
    }

    @PostMapping("/users/unblock/{id}")
    public void unblockUser(@PathVariable Long id) {
        adminService.unblockUser(id);
    }

    //only admin can access
    @PostMapping("/ads/{id}/moderation")
    public void moderateAd(@PathVariable Long id, @RequestBody AdModerationRequest moderationRequest) {
        adminService.moderateAd(id, moderationRequest);
    }

    @GetMapping("/reported-ads")
    public List<AdReportResponse> getReportedAds() {
        return adminService.getReportedAds();
    }


    @GetMapping("/ads/moderation")
    public List<PendingAd> getPendingAds() {
        return adminService.getAllPendingAds();
    }
}
