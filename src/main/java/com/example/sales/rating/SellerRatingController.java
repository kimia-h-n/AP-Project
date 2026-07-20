package com.example.sales.rating;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class SellerRatingController {

    private final SellerRatingService service;

    @PostMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    public void submitVote(@RequestBody SellerRatingRequest request,
                           Authentication authentication) {
        String username = authentication.getName();
        service.submitRating(request, username);
    }

    @GetMapping("/rating/avg/{sellerId}")
    public Double getAverageRating(@PathVariable Long sellerId) {
        return service.calculateSellerRatingAvg(sellerId);
    }


}
