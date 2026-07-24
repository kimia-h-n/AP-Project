package com.example.sales.user;

import com.example.sales.user.model.Role;
import com.example.sales.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing and querying {@link User} entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param username username to search for
     * @return matching user if present
     */
    Optional<User> findByUsername(String username);

    /**
     * Counts enabled users.
     *
     * @return number of enabled users
     */
    long countByEnabledTrue();

    /**
     * Counts disabled users.
     *
     * @return number of disabled users
     */
    long countByEnabledFalse();

    /**
     * Checks whether a user with the given username exists.
     *
     * @param username username to check
     * @return true if a user exists, otherwise false
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether a user with the given phone number exists.
     *
     * @param phoneNumber phone number to check
     * @return true if a user exists, otherwise false
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Checks whether a user with the given email exists.
     *
     * @param email email to check
     * @return true if a user exists, otherwise false
     */
    boolean existsByEmail(String email);

    /**
     * Searches users by first name, last name, or username using a case-insensitive match.
     *
     * @param keyword search term
     * @return list of matching users
     */
    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<User> searchUsers(@Param("keyword") String keyword);

    /**
     * Finds all users with the specified role.
     *
     * @param role user role
     * @return list of users with the given role
     */
    List<User> findAllByRole(Role role);
}
