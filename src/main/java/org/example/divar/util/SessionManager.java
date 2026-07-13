package org.example.divar.util;

import java.io.*;
import java.util.Scanner;

public class SessionManager {
    private static String currentUsername;
    private static final String SESSIONS_DIR = "tokens";

    private static String getTokenFileName(String username) {
        File directory = new File(SESSIONS_DIR);
        if (!directory.exists()) directory.mkdir();
        return SESSIONS_DIR + File.separator + username + "_token.txt";
    }

    public static void login(String username, String token) {
        currentUsername = username;
        setToken(username, token);
    }

    public static void logout() {
        if (currentUsername != null) {
            new File(getTokenFileName(currentUsername)).delete();
            currentUsername = null;
        }
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static boolean isLoggedIn() {
        return currentUsername != null && getToken() != null;
    }

    public static String getToken() {
        if (currentUsername == null) return null;
        File file = new File(getTokenFileName(currentUsername));
        if (!file.exists()) return null;
        try (Scanner scanner = new Scanner(file)) {
            return scanner.hasNextLine() ? scanner.nextLine() : null;
        } catch (IOException e) { return null; }
    }

    private static void setToken(String username, String token) {
        try (FileWriter writer = new FileWriter(getTokenFileName(username))) {
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace(); }
    }
}



