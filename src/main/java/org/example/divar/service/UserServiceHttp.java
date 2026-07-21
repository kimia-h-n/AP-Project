package org.example.divar.service;

import org.example.divar.dto.auth.AuthenticationRequestDTO;
import org.example.divar.dto.auth.AuthenticationResponseDTO;
import org.example.divar.dto.auth.RegisterRequestDTO;
import org.example.divar.model.User;
import org.example.divar.util.ApiClient;
import org.example.divar.util.ConvertToUser;
import org.example.divar.util.SessionManager;
import org.json.JSONObject;

public class UserServiceHttp implements UserService {

    @Override
    public void login(String username, String password) {
        AuthenticationRequestDTO request = new AuthenticationRequestDTO(username, password);
        JSONObject responseJson = ApiClient.post("/api/v1/auth/authenticate", request.toJson());
        AuthenticationResponseDTO responseDTO = AuthenticationResponseDTO.fromJson(responseJson);

        SessionManager.login(username, responseDTO.getToken());

        if (responseDTO.getRole() != null) {
            SessionManager.setRole(responseDTO.getRole());
        }
    }

    @Override
    public void register(String firstname, String lastname, String username, String password,
                         String phoneNumber, String email) {

        RegisterRequestDTO requestDTO = new RegisterRequestDTO(username, password, firstname, lastname, phoneNumber, email);
        JSONObject responseJson = ApiClient.post("/api/v1/auth/register", requestDTO.toJson());
        AuthenticationResponseDTO responseDTO = AuthenticationResponseDTO.fromJson(responseJson);

        if (responseDTO.getToken() != null) {
            SessionManager.login(username, responseDTO.getToken());
        }
    }

    @Override
    public String getNameByUsername(String username) {
        JSONObject userJson = ApiClient.get("/api/v1/users/" + username);

        String firstName = userJson.optString("firstname", "");
        String lastName = userJson.optString("lastname", "");

        return firstName + " " + lastName;
    }

    @Override
    public User getUserProfile(String username) {
        JSONObject userJson = ApiClient.get("/api/v1/users/" + username);
        return ConvertToUser.convertToUser(userJson);
    }
}














