import com.example.sales.authentication.AuthenticationService;
import com.example.sales.authentication.dto.AuthenticationRequest;
import com.example.sales.authentication.dto.AuthenticationResponse;
import com.example.sales.authentication.dto.RegisterRequest;
import com.example.sales.config.JwtService;
import com.example.sales.exception.*;
import com.example.sales.user.UserRepository;
import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthenticationService}.
 * Covers: successful register/login, duplicate username/email/phone on register,
 * wrong credentials, and blocked (disabled) user login.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("kimia")
                .password("plainPassword")
                .firstname("Kimia")
                .lastname("Hosseininejad")
                .phoneNumber("09120000000")
                .email("kimia@example.com")
                .build();
    }

    @Test
    void register_savesUserAndReturnsToken_whenDataIsUnique() {
        when(userRepository.existsByUsername("kimia")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("09120000000")).thenReturn(false);
        when(userRepository.existsByEmail("kimia@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(Role.USER, response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsDuplicateUsernameException_whenUsernameTaken() {
        when(userRepository.existsByUsername("kimia")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class,
                () -> authenticationService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsDuplicatePhoneNumberException_whenPhoneTaken() {
        when(userRepository.existsByUsername("kimia")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("09120000000")).thenReturn(true);

        assertThrows(DuplicatePhoneNumberException.class,
                () -> authenticationService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsDuplicateEmailException_whenEmailTaken() {
        when(userRepository.existsByUsername("kimia")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("09120000000")).thenReturn(false);
        when(userRepository.existsByEmail("kimia@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> authenticationService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_returnsToken_whenCredentialsAreValid() {
        AuthenticationRequest request = new AuthenticationRequest("kimia", "plainPassword");
        User user = User.builder().username("kimia").password("encodedPassword")
                .role(Role.USER).enabled(true).build();

        when(userRepository.findByUsername("kimia")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(Role.USER, response.getRole());
        verify(authManager).authenticate(any());
    }

    @Test
    void authenticate_throwsInvalidUsernameOrPassword_whenCredentialsAreWrong() {
        AuthenticationRequest request = new AuthenticationRequest("kimia", "wrongPassword");
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(InvalidUsernameOrPassword.class,
                () -> authenticationService.authenticate(request));
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void authenticate_throwsBlockedUserLoginException_whenUserIsDisabled() {
        AuthenticationRequest request = new AuthenticationRequest("kimia", "plainPassword");
        when(authManager.authenticate(any())).thenThrow(new DisabledException("user disabled"));

        assertThrows(BlockedUserLoginException.class,
                () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticate_throwsUserNotFoundException_whenUserMissingAfterAuth() {
        AuthenticationRequest request = new AuthenticationRequest("kimia", "plainPassword");
        when(userRepository.findByUsername("kimia")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authenticationService.authenticate(request));
    }
}
