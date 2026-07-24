package com.example.sales.authentication;

import com.example.sales.authentication.dto.AuthenticationRequest;
import com.example.sales.authentication.dto.AuthenticationResponse;
import com.example.sales.authentication.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication-related operations.
 * <p>
 * Exposes endpoints for user registration and login.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    /**
     * Registers a new user account.
     *
     * @param request registration payload
     * @return authentication response containing JWT token and user role
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates an existing user.
     *
     * @param request login credentials
     * @return authentication response containing JWT token and user role
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
