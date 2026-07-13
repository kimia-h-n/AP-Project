package org.example.divar.dto.auth;

import org.json.JSONObject;

public class AuthenticationResponseDTO {

    private final String token;

    private AuthenticationResponseDTO(String token) {
        this.token = token;
    }

    public static AuthenticationResponseDTO fromJson(JSONObject json) {
        String token = json.optString("token", null);
        return new AuthenticationResponseDTO(token);
    }

    public String getToken() {
        return token;
    }
}
