package com.example.sales.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for creating, parsing, and validating JWT tokens.
 * <p>
 * Uses application properties for the signing key and token expiration time.
 * </p>
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extracts the username from a JWT token.
     *
     * @param jwtToken JWT token
     * @return username encoded in the token subject
     */
    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    /**
     * Generates a JWT token with custom claims for the specified user.
     *
     * @param customClaims additional claims to include in the token
     * @param userDetails authenticated user details
     * @return signed JWT token
     */
    public String generateToken(Map<String, Object> customClaims,
                                UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(customClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token for the given user without additional claims.
     *
     * @param userDetails authenticated user details
     * @return signed JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Parses and returns all claims from the given token.
     *
     * @param token JWT token
     * @return parsed claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates that the token belongs to the given user and is not expired.
     *
     * @param token JWT token
     * @param userDetails user details to compare against
     * @return true if the token is valid, otherwise false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }

    /**
     * Checks whether the token has expired.
     *
     * @param token JWT token
     * @return true if the token is expired, otherwise false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token JWT token
     * @return token expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Builds the signing key used to verify and sign JWT tokens.
     *
     * @return HMAC signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Performs a lightweight token validation by parsing its claims.
     *
     * @param token JWT token
     * @return true if token parsing succeeds, otherwise false
     */
    public boolean validateJwtToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Extracts a custom value from token claims.
     *
     * @param token JWT token
     * @param claimsResolver function used to resolve a value from claims
     * @param <T> resolved value type
     * @return extracted value
     */
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}
