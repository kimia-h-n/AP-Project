package com.example.sales.test_case;

import com.example.sales.auth.AuthenticationRequest;
import com.example.sales.auth.AuthenticationResponse;
import com.example.sales.auth.AuthenticationService;
import com.example.sales.auth.RegisterRequest;
import com.example.sales.config.JwtService;
import com.example.sales.exception.*;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.Role;
import com.example.sales.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    // ---------- authenticate ----------

    @Test
    void authenticate_badCredentials_throwsInvalidUsernameOrPassword() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("john")
                .password("wrongpass")
                .build();
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("bad creds"));

        assertThrows(InvalidUsernameOrPassword.class, () -> authenticationService.authenticate(request));
        verifyNoInteractions(jwtService);
    }

    @Test
    void authenticate_validCredentialsButUserMissing_throwsUserNotFoundException() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("john")
                .password("secret")
                .build();
        when(authManager.authenticate(any())).thenReturn(null);
        when(repository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticate_success_returnsTokenAndRole() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("john")
                .password("secret")
                .build();
        User user = User.builder().username("john").role(Role.USER).build();

        when(authManager.authenticate(any())).thenReturn(null);
        when(repository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("signed-jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("signed-jwt-token", response.getToken());
        assertEquals(Role.USER, response.getRole());
    }

    // ---------- register ----------

    @Test
    void register_duplicateUsername_throwsDuplicateUsernameException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john")
                .email("john@example.com")
                .phoneNumber("0912")
                .build();
        when(repository.existsByUsername("john")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> authenticationService.register(request));
        verify(repository, never()).save(any());
    }

    @Test
    void register_duplicatePhoneNumber_throwsDuplicatePhoneNumberException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john")
                .email("john@example.com")
                .phoneNumber("0912")
                .build();
        when(repository.existsByUsername("john")).thenReturn(false);
        when(repository.existsByPhoneNumber("0912")).thenReturn(true);

        assertThrows(DuplicatePhoneNumberException.class, () -> authenticationService.register(request));
        verify(repository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throwsDuplicateEmailException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john")
                .email("john@example.com")
                .phoneNumber("0912")
                .build();
        when(repository.existsByUsername("john")).thenReturn(false);
        when(repository.existsByPhoneNumber("0912")).thenReturn(false);
        when(repository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> authenticationService.register(request));
        verify(repository, never()).save(any());
    }

    @Test
    void register_success_encodesPasswordAndReturnsToken() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john")
                .email("john@example.com")
                .phoneNumber("0912")
                .password("plainpass")
                .firstname("John")
                .lastname("Doe")
                .build();

        when(repository.existsByUsername("john")).thenReturn(false);
        when(repository.existsByPhoneNumber("0912")).thenReturn(false);
        when(repository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainpass")).thenReturn("encoded-pass");
        when(jwtService.generateToken(any(User.class))).thenReturn("signed-jwt-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertEquals("signed-jwt-token", response.getToken());
        assertEquals(Role.USER, response.getRole());

        verify(repository).save(argThat(savedUser ->
                savedUser.getPassword().equals("encoded-pass")
                        && savedUser.getRole() == Role.USER
                        && savedUser.isEnabled()
        ));
    }
}
