package com.example.sales.ad.fav;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/favorites")
public class FavoriteAdController {

    private final FavoriteAdService favAdService;

    @PostMapping("/{adId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addToFavorites(@PathVariable Long adId, Authentication authentication) {
        String username = authentication.getName();
        favAdService.addToFavorites(adId, username);
    }

    @DeleteMapping("/{adId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFromFavorites(@PathVariable Long adId, Authentication authentication) {
        String username = authentication.getName();
        favAdService.removeFromFavorites(adId, username);
    }

}
