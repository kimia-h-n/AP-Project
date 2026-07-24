package com.example.sales.chat.controller;


import com.example.sales.chat.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user search operations.
 */
@RequestMapping("/api/v1/")
@RestController
public class SearchController {

    private SearchService searchService;

    /**
     * Searches users by name keyword.
     *
     * @param name search keyword
     * @return matching users
     */
    @GetMapping("search-user")
    public ResponseEntity<?> searchForUser(@RequestParam String name) {
        return ResponseEntity.ok(searchService.searchUser(name));
    }
}
