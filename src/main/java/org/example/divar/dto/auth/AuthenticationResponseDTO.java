package org.example.divar.dto.auth;

import org.example.divar.model.UserRole;
import org.json.JSONObject;

/**
 * Data Transfer Object (DTO) for parsing authentication responses from the server.
 */
public class AuthenticationResponseDTO {

    private final String token;
    private final UserRole role;

    private AuthenticationResponseDTO(String token, UserRole role) {
        this.token = token;
        this.role = role;
    }

    public static AuthenticationResponseDTO fromJson(JSONObject json) {
        String token = json.optString("token", null);
        UserRole role = null;
        String roleText = json.optString("role", null);

        if (roleText != null && !roleText.isBlank()) {
            role = UserRole.valueOf(roleText);
        }

        return new AuthenticationResponseDTO(token, role);
    }

    public String getToken() {
        return token;
    }

    public UserRole getRole() {
        return role;
    }
}


