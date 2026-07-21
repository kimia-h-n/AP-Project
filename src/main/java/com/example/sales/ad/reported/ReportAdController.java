package com.example.sales.ad.reported;


import com.example.sales.ad.reported.model.ReportReason;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ReportAdController {

    private final ReportAdService reportAdService;

    @PostMapping("/report-ad")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reportAd(@RequestParam Long adId,
                         @RequestBody ReportReason reportReason, Authentication authentication) {
        log.info("inside report ad");
        String username = authentication.getName();
        reportAdService.reportAd(adId, reportReason, username);
    }
}
