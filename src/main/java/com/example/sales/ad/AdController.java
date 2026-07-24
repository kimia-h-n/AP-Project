package com.example.sales.ad;

import com.example.sales.ad.dto.*;
import com.example.sales.ad.filter.AdSortChoice;
import com.example.sales.ad.filter.DateFilter;
import com.example.sales.ad.model.*;
import com.example.sales.ad.image.AdImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * REST controller for advertisement-related operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
class AdController {
    private final AdService adService;

    /**
     * Creates a new advertisement.
     *
     * @param request advertisement data
     * @param authentication current authentication context
     * @return created ad id
     */
    @PostMapping("/ads")
    public AdInsertResponse addAdvertisement(@RequestBody AdRequest request, Authentication authentication) {
        String username = authentication.getName();
        return adService.addAd(request, username);
    }

    /**
     * Returns all approved advertisements.
     *
     * @param authentication current authentication context, may be null
     * @return list of ad summaries
     */
    @GetMapping("/ads")
    public List<AdCardSummary> getAllAds(Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAllActiveAds(username);
    }

    /**
     * Returns full details for a single advertisement.
     *
     * @param id advertisement id
     * @param authentication current authentication context, may be null
     * @return advertisement details
     */
    @GetMapping("/ads/{id}")
    public AdResponse getAd(@PathVariable Long id, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAd(id, username);
    }

    /**
     * Returns all approved advertisements belonging to the current user.
     *
     * @param authentication current authentication context
     * @return list of the user's ads
     */
    @GetMapping("/me/ads/")
    public List<AdCardSummary> getAllMyAds(Authentication authentication) {
        String username = authentication.getName();
        return adService.getAllMyAds(username);
    }

    /**
     * Updates an existing advertisement.
     *
     * @param id advertisement id
     * @param adRequest updated data
     * @param authentication current authentication context
     */
    @PatchMapping("/ads/{id}")
    public void updateAd(@PathVariable Long id, @RequestBody AdUpdateRequest adRequest, Authentication authentication) {
        String username = authentication.getName();
        adService.updateAd(id, username, adRequest);
    }

    /**
     * Removes an advertisement.
     *
     * @param id advertisement id
     * @param authentication current authentication context
     */
    @DeleteMapping("/ads/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAd(@PathVariable Long id, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        adService.removeAd(id, username);
    }

    /**
     * Returns the username only when the caller is authenticated.
     *
     * @param authentication current authentication context
     * @return username, or null if the caller is anonymous
     */
    private String extractUsernameIfLoggedIn(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return "anonymousUser".equals(username) ? null : username;
    }

    /**
     * Searches approved ads by title.
     *
     * @param title title fragment
     * @return matching ad summaries
     */
    @GetMapping("/ads/search")
    public List<AdCardSummary> searchByTitle(@RequestParam String title) {
        return adService.searchByTitle(title);
    }

    /**
     * Filters approved ads by price, category, date and city.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param category ad category
     * @param dataFilter date filter
     * @param cityId city id
     * @return filtered ad summaries
     */
    @GetMapping("/ads/filter")
    public List<AdCardSummary> searchAds(
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) AdCategory category,
            @RequestParam(required = false) DateFilter dataFilter,
            @RequestParam(required = false) Long cityId
    ) {
        return adService.filterAds(minPrice, maxPrice, category, dataFilter, cityId);
    }

    /**
     * Sorts approved ads using the requested sort choice.
     *
     * @param adSortChoice sort strategy
     * @return sorted ad summaries
     */
    @GetMapping("/ads/sortBy")
    public List<AdCardSummary> sortAds(@RequestParam AdSortChoice adSortChoice) {
        return adService.sortAdsBy(adSortChoice);
    }
}
