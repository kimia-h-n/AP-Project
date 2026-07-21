package com.example.sales.ad;

import com.example.sales.ad.model.*;
import com.example.sales.picture.ImageData;
import com.example.sales.picture.ImageDownload;
import com.example.sales.picture.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdController {
    private final AdService adService;
    private final StorageService storageService;

    @PostMapping("/ads")
    public AdInsertResponse addAdvertisement(@RequestBody AdRequest request, Authentication authentication) {
        String username = authentication.getName();
        return adService.addAd(request, username);
    }

    //sample use case: GET /api/v1/ads?status=APPROVED
    //for now only gets active ads
    @GetMapping("/ads")
    public List<AdCardSummary> getAllAds(Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAllActiveAds(username);
    }

    @GetMapping("/ads/{id}")
    public AdResponse getAd(@PathVariable Long id, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAd(id, username);
    }

    @GetMapping("/me/ads/")
    public List<AdCardSummary> getAllMyAds(Authentication authentication) {
        String username = authentication.getName();
        return adService.getAllMyAds(username);
    }

    @PatchMapping("/ads/{id}")
    public void updateAd(@PathVariable Long id, @RequestBody AdUpdateRequest adRequest, Authentication authentication) {
        String username = authentication.getName();
        adService.updateAd(id, username, adRequest);
    }


    @DeleteMapping("/ads/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAd(@PathVariable Long id, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        adService.removeAd(id, username);
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

    @GetMapping("/search")
    public List<AdCardSummary> searchByTitle(@RequestParam String title, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.searchByTitle(username, title);
    }

    @GetMapping("/filter")
    public List<AdCardSummary> searchAds(
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) AdCategory category,
            @RequestParam(required = false) DateFilter dataFilter,
            @RequestParam(required = false) Long cityId
    ) {
        return adService.filterAds(minPrice, maxPrice, category, dataFilter, cityId);
    }

    @PostMapping(value = "/ads/{adId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @PathVariable Long adId,
            @RequestPart("files") List<MultipartFile> files, Authentication authentication) throws IOException {
        log.info("UPLOAD IMAGE -> adId={}, fileCount={}, authPresent={}",
                adId,
                files != null ? files.size() : 0,
                authentication != null);

        if (authentication != null) {
            log.info("UPLOAD IMAGE -> username={}", authentication.getName());
        }

        List<UUID> ids = new ArrayList<>();
        String username = authentication.getName();
        for (MultipartFile file : files) {
            ImageData saved = storageService.upload(adId, file, username);
            ids.add(saved.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ids);
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> download(@PathVariable UUID imageId) {
        ImageDownload result = storageService.download(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.data());
    }

    @DeleteMapping("/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeImage(@PathVariable UUID imageId, Authentication authentication) {
        String username = authentication.getName();
        storageService.removeImage(imageId, username);
    }

    @PutMapping(value = "/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> replaceImage(
            @PathVariable UUID imageId,
            @RequestPart("files") MultipartFile file,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        storageService.replaceImage(imageId, file, username);
        return ResponseEntity.ok(imageId);
    }


}
