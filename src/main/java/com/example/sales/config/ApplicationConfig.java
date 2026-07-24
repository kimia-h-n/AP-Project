package com.example.sales.config;


import com.example.sales.exception.UserNotFoundException;
import com.example.sales.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * Application-level security configuration.
 * <p>
 * Defines core authentication beans such as the {@link UserDetailsService},
 * {@link AuthenticationProvider}, {@link PasswordEncoder}, and
 * {@link AuthenticationManager}.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class
ApplicationConfig {

    private final UserRepository repository;

    /**
     * Provides a user details service that loads users by username.
     *
     * @return user details service backed by the user repository
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * Configures the authentication provider for username/password login.
     *
     * @return configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    /**
     * Provides the password encoder used for storing and verifying passwords.
     *
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the application authentication manager.
     *
     * @param config authentication configuration
     * @return authentication manager
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
