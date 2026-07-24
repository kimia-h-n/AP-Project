package com.example.sales.user.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * JPA entity representing an application user.
 * <p>
 * The entity also implements {@link UserDetails} so it can be used directly by
 * Spring Security during authentication and authorization.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "phone_number"),
        @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {

    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Unique username used for login.
     */
    private String username;

    /**
     * User first name.
     */
    private String firstname;

    /**
     * User last name.
     */
    private String lastname;

    /**
     * Unique email address.
     */
    private String email;

    /**
     * Unique phone number.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Hashed password.
     */
    private String password;

    /**
     * Role of the user.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Indicates whether the account is enabled.
     */
    private boolean enabled;

    /*
      Spring:
      Role: USER
      Authority: ROLE_USER
      then for checking, .hasRole("ADMIN")
      or hasAuthority("ROLE_ADMIN")
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Returns the username used by Spring Security.
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Indicates whether the account is non-expired.
     *
     * @return always true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is non-locked.
     *
     * @return always true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the credentials are non-expired.
     *
     * @return always true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is enabled.
     *
     * @return true if enabled, otherwise false
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Returns the encoded password used by Spring Security.
     *
     * @return password hash
     */
    @Override
    public String getPassword() {
        return password;
    }
}

