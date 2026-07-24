package com.example.sales.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
/**
 * Main Spring Security configuration for the application.
 * <p>
 * Configures request authorization rules, session management, CORS,
 * and the JWT authentication filter.
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Builds the HTTP security filter chain.
     *
     * @param http HTTP security builder
     * @return configured security filter chain
     * @throws Exception if the security configuration cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ───────────────────── Public Endpoints ─────────────────────
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/province").permitAll()

                        // ───────────────────── Public GET Endpoints ─────────────────
                        .requestMatchers(HttpMethod.GET, "/api/v1/ads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/filter").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/ads/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/ads/sortBy").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/rating/avg/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()

                        // ───────────────────── Swagger / Docs ───────────────────────
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // ───────────────────── Authenticated Endpoints ──────────────
                        .requestMatchers("/api/v1/favorites/**").authenticated()

                        // ───────────────────── Image Mutations ──────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/v1/ads/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/images/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/images/**").authenticated()

                        // ───────────────────── Admin ───────────────────────────────
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // ───────────────────── Chat ───────────────────────────────
                        .requestMatchers("/chat-native").permitAll()
                        .requestMatchers("/chat/**").permitAll()

                        // ───────────────────── Everything else ─────────────────────
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configures Cross-Origin Resource Sharing for the application.
     *
     * @return CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
