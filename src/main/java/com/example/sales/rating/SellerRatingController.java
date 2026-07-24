package com.example.sales.rating;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.Double;

/**
 * REST controller for submitting and retrieving seller ratings.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class SellerRatingController {

    private final SellerRatingService service;

    /**
     * Submits a rating for a seller on behalf of the authenticated user.
     *
     * @param request        rating submission payload
     * @param authentication current authentication context
     */
    @PostMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    public void submitVote(@RequestBody SellerRatingRequest request,
                           Authentication authentication) {
        String username = authentication.getName();
        service.submitRating(request, username);
    }

    /**
     * Returns the average rating for a seller.
     *
     * @param sellerId seller identifier
     * @return average seller rating
     */
    @GetMapping("/rating/avg/{sellerId}")
    public Double getAverageRating(@PathVariable Long sellerId) {
        return service.calculateSellerRatingAvg(sellerId);
    }
}
