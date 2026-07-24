package com.example.sales.authentication;

import com.example.sales.authentication.dto.AuthenticationRequest;
import com.example.sales.authentication.dto.AuthenticationResponse;
import com.example.sales.authentication.dto.RegisterRequest;
import com.example.sales.config.JwtService;
import com.example.sales.exception.*;
import com.example.sales.user.UserRepository;
import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user authentication and registration.
 * <p>
 * Handles login validation, account creation, password encoding,
 * and JWT token generation.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    /**
     * Authenticates a user using username and password.
     *
     * @param request authentication credentials
     * @return authentication response containing JWT token and user role
     * @throws BlockedUserLoginException if the user account is disabled
     * @throws InvalidUsernameOrPassword if credentials are invalid
     * @throws UserNotFoundException if the user cannot be found after authentication
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (DisabledException e) {
            throw new BlockedUserLoginException();
        } catch (AuthenticationException e) {
            throw new InvalidUsernameOrPassword();
        }

        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(UserNotFoundException::new);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }

    /**
     * Registers a new user account.
     * <p>
     * Validates uniqueness of username, phone number, and email before creating
     * the user. The password is stored in encoded form.
     * </p>
     *
     * @param request registration data
     * @return authentication response containing JWT token and user role
     * @throws DuplicateUsernameException if the username already exists
     * @throws DuplicatePhoneNumberException if the phone number already exists
     * @throws DuplicateEmailException if the email already exists
     */
    public AuthenticationResponse register(RegisterRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();

        if (repository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
        if (repository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicatePhoneNumberException();
        }
        if (repository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(email)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .enabled(true)
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }
}
