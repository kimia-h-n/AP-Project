package com.example.sales.admin;


import com.example.sales.repository.UserRepository;
import com.example.sales.user.Role;
import com.example.sales.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner createAdmin() {
        return args -> {
            if (userRepository.existsByUsername(adminUsername))
                return;

            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(null)
                    .phoneNumber(null)
                    .firstname(null)
                    .lastname(null)
                    .role(Role.ADMIN)
                    .enable(true)
                    .build();
            userRepository.save(admin);
        };
    }
}
