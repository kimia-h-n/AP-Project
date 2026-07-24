package com.example.sales.user;


import com.example.sales.user.dto.UserInfoResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user-related operations.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Returns user details by username.
     *
     * @param username username to search for
     * @return detailed user response
     */
    @GetMapping("/username/{username}")
    public UserInfoResponse getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }
}

