import com.example.sales.config.JwtService;
import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtService}.
 * Covers: token generation, username extraction, token validity checks
 * (matching user, mismatched user, expired token) and malformed-token handling.
 */
class JwtServiceTest {

    private JwtService jwtService;

    // 256-bit (32 byte) random key, base64 encoded, valid for HS256 signing.
    private static final String SECRET_KEY =
            Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86_400_000L); // 1 day
    }

    private User buildUser(String username) {
        return User.builder()
                .username(username)
                .password("encoded-pass")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void generateToken_thenExtractUsername_returnsOriginalUsername() {
        User user = buildUser("kimia");

        String token = jwtService.generateToken(user);
        String extracted = jwtService.extractUsername(token);

        assertEquals("kimia", extracted);
    }

    @Test
    void isTokenValid_returnsTrue_whenUsernameMatchesAndNotExpired() {
        User user = buildUser("fateme");
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_returnsFalse_whenTokenBelongsToAnotherUser() {
        User tokenOwner = buildUser("kimia");
        User otherUser = buildUser("fateme");
        String token = jwtService.generateToken(tokenOwner);

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_throwsExpiredJwtException_whenTokenAlreadyExpired() {
        // negative expiration => token's "expiration" timestamp is already in the past
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        User user = buildUser("kimia");
        String expiredToken = jwtService.generateToken(user);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, user));
    }

    @Test
    void validateJwtToken_returnsFalse_forMalformedToken() {
        assertFalse(jwtService.validateJwtToken("this.is.not-a-valid-jwt"));
    }

    @Test
    void validateJwtToken_returnsTrue_forWellFormedToken() {
        User user = buildUser("kimia");
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.validateJwtToken(token));
    }
}
