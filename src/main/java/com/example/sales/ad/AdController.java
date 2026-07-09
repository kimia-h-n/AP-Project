package com.example.sales.ad;

import com.example.sales.ad.model.AdRequest;
import com.example.sales.ad.model.AdResponse;
import com.example.sales.ad.model.moderation.AdModerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdController {
    private final AdService adService;

    @PostMapping("/ads")
    public void addAdvertisement(@RequestBody AdRequest request, Authentication authentication) {
        String username = authentication.getName();
        adService.addAd(request, username);
    }


    //sample use case: GET /api/v1/ads?status=APPROVED
    //for now only gets active ads
    @GetMapping("/ads")
    public List<AdResponse> getAllAds(Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAllActiveAds(username);
    }

    @GetMapping("/ads/{id}")
    public AdResponse getAd(@PathVariable Long id, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAd(id, username);
    }

    //only admin can access
    @PostMapping("/admin/ads/{id}/moderation")
    public void moderateAd(@PathVariable Long id, @RequestBody AdModerationRequest moderationRequest) {
        adService.moderateAd(id, moderationRequest);
    }


    @GetMapping("/admin/ads/moderation")
    public List<AdResponse> getPendingAds() {
        return adService.getAllPendingAds();
    }

    /**
     * If token is sent, it means that the user has logged in and proper adjustments must be made
     *
     * @param authentication
     * @return username or null if not logged in.
     */
    private String extractUsernameIfLoggedIn(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return "anonymousUser".equals(username) ? null : username;
    }
}
