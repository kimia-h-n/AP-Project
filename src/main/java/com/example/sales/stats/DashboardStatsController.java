package com.example.sales.stats;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin/dashboard/stats")
public class DashboardStatsController {
    private final DashboardStatsService service;

    @GetMapping
    public DashboardStatistics getStats() {
        return service.getStats();
    }
}
