package com.example.sales.admin;

import com.example.sales.user.UserRepository;
import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for creating the default administrator account
 * during application startup.
 * <p>
 * If a user with the configured admin username already exists, no new admin
 * account is created.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    /**
     * Creates a startup task that inserts the default admin user if it does not
     * already exist in the system.
     *
     * @return command line runner responsible for admin account initialization
     */
    @Bean
    public CommandLineRunner createAdmin() {
        return args -> {
            if (userRepository.existsByUsername(adminUsername)) {
                return;
            }

            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(null)
                    .phoneNumber(null)
                    .firstname(null)
                    .lastname(null)
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
        };
    }
}
