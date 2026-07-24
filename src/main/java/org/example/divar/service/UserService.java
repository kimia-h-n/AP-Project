package org.example.divar.service;

import org.example.divar.model.User;

/**
 * Interface for managing user authentication and profile information.
 */
public interface UserService {

    /**
     * Authenticates a user with username and password.
     */
    void login(String username, String password) throws RuntimeException;

    /**
     * Registers a new user with personal and account details.
     */
    void register(String firstname, String lastname, String username, String password, String phoneNumber, String email) throws RuntimeException;

    String getNameByUsername(String username);

    User getUserProfile(String username) throws RuntimeException;
}


