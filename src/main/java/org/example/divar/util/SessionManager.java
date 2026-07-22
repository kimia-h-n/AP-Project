package org.example.divar.util;

import org.example.divar.model.UserRole;

import java.io.*;
import java.util.Scanner;

public class SessionManager {
    private static String currentUsername;
    private static UserRole currentRole;
    private static final String TOKENS_DIRECTORY = "tokens";

    private static String getTokenFileName(String username) {
        File directory = new File(TOKENS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return TOKENS_DIRECTORY + File.separator + username + "_token.txt";
    }

    public static void login(String username, String token) {
        currentUsername = username;
        setToken(username, token);
    }

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

    private static void setToken(String username, String token) {
        try (FileWriter writer = new FileWriter(getTokenFileName(username))) {
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}











