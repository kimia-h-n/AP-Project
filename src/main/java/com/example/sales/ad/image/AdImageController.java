package com.example.sales.ad.image;

import com.example.sales.picture.ImageData;
import com.example.sales.picture.ImageDownload;
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

/**
 * REST controller for ad image upload, download, replacement, and deletion.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdImageController {

    private final AdImageService adImageService;

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
            ImageData saved = adImageService.upload(adId, file, username);
            ids.add(saved.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ids);
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> download(@PathVariable UUID imageId) {
        ImageDownload result = adImageService.download(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.data());
    }

    @DeleteMapping("/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeImage(@PathVariable UUID imageId, Authentication authentication) {
        String username = authentication.getName();
        adImageService.removeImage(imageId, username);
    }

    @PutMapping(value = "/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> replaceImage(
            @PathVariable UUID imageId,
            @RequestPart("files") MultipartFile file,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        adImageService.replaceImage(imageId, file, username);
        return ResponseEntity.ok(imageId);
    }
}
