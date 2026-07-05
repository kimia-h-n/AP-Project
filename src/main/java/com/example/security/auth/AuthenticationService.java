package com.example.security.auth;

import com.example.security.config.JwtService;
import com.example.security.exception.DuplicateEmailException;
import com.example.security.exception.DuplicateUsernameException;
import com.example.security.exception.DuplicatePhoneNumberException;
import com.example.security.exception.UserNotFoundException;
import com.example.security.repository.UserRepository;
import com.example.security.user.Role;
import com.example.security.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        var user = repository.findUsersByUsername(request.getUsername()).orElseThrow(UserNotFoundException::new);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        String username = request.getUsername(), email = request.getEmail(), phoneNumber = request.getPhoneNumber();
        if (repository.existsByUsername(username))
            throw new DuplicateUsernameException();
        if (repository.existsByPhoneNumber(phoneNumber))
            throw new DuplicatePhoneNumberException();
        if (repository.existsByEmail(email))
            throw new DuplicateEmailException();

        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))//save encoded password
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(email)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
