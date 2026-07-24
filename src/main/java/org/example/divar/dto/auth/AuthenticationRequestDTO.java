package org.example.divar.dto.auth;

import org.json.JSONObject;

/**
 * Data Transfer Object (DTO) holding user credentials for authentication requests.
 */
public class AuthenticationRequestDTO {

    private final String username;
    private final String password;

    public AuthenticationRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        return json;
    }
}
