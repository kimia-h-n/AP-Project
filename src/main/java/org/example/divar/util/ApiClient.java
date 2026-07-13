package org.example.divar.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static JSONObject post(String path, JSONObject body) {
        return sendRequest(HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString())));
    }

    public static JSONObject get(String path) {
        return sendRequest(HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + path))
                .GET());
    }

    public static JSONObject delete(String path) {
        return sendRequest(HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + path))
                .DELETE());
    }

    private static JSONObject sendRequest(HttpRequest.Builder builder) {
        try {
            String token = SessionManager.getToken();
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return handleResponse(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error: Connection failed. Check your internet or server address.");
        }
    }

    private static JSONObject handleResponse(HttpResponse<String> response) {
        int status = response.statusCode();
        String body = response.body();

        JSONObject bodyJson = new JSONObject();
        try {
            if (body != null && !body.isBlank()) {
                bodyJson = new JSONObject(body);
            }
        } catch (JSONException e) {
            return bodyJson;
        }

        if (status >= 200 && status < 300) {
            return bodyJson;
        }

        String errorCode = bodyJson.optString("error", null);
        String message = bodyJson.optString("message", null);

        throw new RuntimeException(HandleErrors.getPersianMessage(errorCode, message, status));
    }

    public static JSONArray getList(String path) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + path))
                    .GET();

            String token = SessionManager.getToken();
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String body = response.body();

            if (status >= 200 && status < 300) {
                if (body == null || body.isBlank()) {
                    return new JSONArray();
                }
                return new JSONArray(body);
            }

            JSONObject errorJson = new JSONObject();
            try {
                if (body != null && !body.isBlank()) {
                    errorJson = new JSONObject(body);
                }
            } catch (JSONException ignored) {
            }
            String errorCode = errorJson.optString("error", null);
            String message = errorJson.optString("message", null);
            throw new RuntimeException(HandleErrors.getPersianMessage(errorCode, message, status));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error: Connection failed. Check your internet or server address.");
        }
    }
}
