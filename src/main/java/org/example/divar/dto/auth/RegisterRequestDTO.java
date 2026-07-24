package org.example.divar.dto.auth;

import org.json.JSONObject;

/**
 * Data Transfer Object (DTO) holding user registration details for signup requests.
 */
public class RegisterRequestDTO {

    private final String username;
    private final String password;
    private final String firstname;
    private final String lastname;
    private final String phoneNumber;
    private final String email;

    public RegisterRequestDTO(String username, String password, String firstname,
                              String lastname, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        json.put("firstname", firstname);
        json.put("lastname", lastname);
        json.put("phoneNumber", phoneNumber);
        json.put("email", email);
        return json;
    }
}
