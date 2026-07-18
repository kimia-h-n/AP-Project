package com.example.sales.repository;

import com.example.sales.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    @Query("""
       SELECT u FROM User u
       WHERE LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<User> searchUsers(@Param("keyword") String keyword);
}
