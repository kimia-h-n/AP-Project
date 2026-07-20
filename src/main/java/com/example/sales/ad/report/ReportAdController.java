package com.example.sales.ad.report;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class ReportAdController {

    private final ReportAdService reportAdService;

    @PostMapping("report-ad")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reportAd(@RequestParam Long adId,
                         @RequestBody ReportReason reportReason, Authentication authentication) {
        String username = authentication.getName();
        reportAdService.reportAd(adId, reportReason, username);
    }
}
