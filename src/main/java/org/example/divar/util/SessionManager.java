package org.example.divar.util;

import org.example.divar.model.UserRole;

import java.io.*;
import java.util.Scanner;

/**
 * Manages the user session state on the client side, including the currently logged-in user,
 * their role, and the secure authentication token stored locally in text files.
 *
 * @author Fatemeh Salehi Mobin
 * @version 1.0
 */
public class SessionManager {
    private static String currentUsername;
    private static UserRole currentRole;
    private static final String TOKENS_DIRECTORY = "tokens";

    /**
     * Generates the file path for a specific user's authentication token.
     * Automatically creates the tokens directory if it does not exist.
     *
     * @param username the username of the user
     * @return the string path to the user's token file
     */
    private static String getTokenFileName(String username) {
        File directory = new File(TOKENS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return TOKENS_DIRECTORY + File.separator + username + "_token.txt";
    }

    /**
     * Logs in the user by setting the active username and saving their authentication token.
     *
     * @param username the username logging in
     * @param token    the JWT or authentication token provided by the server
     */
    public static void login(String username, String token) {
        currentUsername = username;
        setToken(username, token);
    }

    /**
     * Logs out the current user by deleting their token file and clearing session variables.
     */
    public static void logout() {
        if (currentUsername != null) {
            new File(getTokenFileName(currentUsername)).delete();
            currentUsername = null;
            currentRole = null;
        }
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setRole(UserRole role) {
        currentRole = role;
    }

    public static UserRole getRole() {
        return currentRole;
    }

    public static boolean isAdmin() {
        if (currentRole == UserRole.ADMIN) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves the authentication token for the currently active user from their local token file.
     *
     * @return the token string, or null if not found or no user is logged in
     */
    public static String getToken() {
        if (currentUsername == null) {
            return null;
        }
        File file = new File(getTokenFileName(currentUsername));
        if (!file.exists()) {
            return null;
        }
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Saves the user's authentication token into a local text file.
     *
     * @param username the username associated with the token
     * @param token    the token string to save
     */
    private static void setToken(String username, String token) {
        try (FileWriter writer = new FileWriter(getTokenFileName(username))) {
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}











