package org.example.divar.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();

    private static HttpRequest.Builder createBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + path));

        String token = SessionManager.getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    public static JSONObject post(String path, JSONObject body) {
        HttpRequest.Builder builder = createBuilder(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()));
        return (JSONObject) sendRequest(builder, false);
    }

    public static JSONObject get(String path) {
        HttpRequest.Builder builder = createBuilder(path).GET();
        return (JSONObject) sendRequest(builder, false);
    }

    public static JSONArray getList(String path) {
        HttpRequest.Builder builder = createBuilder(path).GET();
        return (JSONArray) sendRequest(builder, true);
    }

    public static JSONObject patch(String path, JSONObject body) {
        HttpRequest.Builder builder = createBuilder(path)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()));
        return (JSONObject) sendRequest(builder, false);
    }

    public static JSONObject delete(String path) {
        HttpRequest.Builder builder = createBuilder(path).DELETE();
        return (JSONObject) sendRequest(builder, false);
    }

    private static Object sendRequest(HttpRequest.Builder builder, boolean isList) {
        try {
            HttpRequest request = builder.build();
            System.out.println("=== API REQUEST DEBUG ===");
            System.out.println("METHOD: " + request.method());
            System.out.println("URI: " + request.uri());
            System.out.println("TOKEN SENT: " + SessionManager.getToken());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("HTTP STATUS: " + response.statusCode());
            System.out.println("RESPONSE BODY: " + response.body());
            System.out.println("========================");

            return handleResponse(response, isList);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("خطا: ارتباط با سرور برقرار نشد.");
        }
    }

    private static Object handleResponse(HttpResponse<String> response, boolean isList) {
        int status = response.statusCode();
        String body = response.body();
        boolean hasBody = body != null && !body.isBlank();

        if (status >= 200 && status < 300) {
            if (isList) {
                if (hasBody) {
                    return new JSONArray(body);
                } else {
                    return new JSONArray();
                }
            } else {
                try {
                    if (hasBody) {
                        return new JSONObject(body);
                    } else {
                        return new JSONObject();
                    }
                } catch (JSONException e) {
                    return new JSONObject();
                }
            }
        }

        JSONObject errorJson = new JSONObject();
        if (hasBody) {
            try {
                errorJson = new JSONObject(body);
            } catch (JSONException ignored) {}
        }

        String errorCode = errorJson.optString("error", null);
        String message = errorJson.optString("message", null);
        throw new RuntimeException(HandleErrors.getPersianMessage(errorCode, message, status));
    }

    public static void postRaw(String path, String rawJsonBody) {
        HttpRequest.Builder builder = createBuilder(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(rawJsonBody));
        sendRequest(builder, false);
    }

    public static void postMultipartImages(String path, ArrayList<String> localImagePaths) {
        String boundary = generateBoundary();
        List<byte[]> byteArrays = new ArrayList<>();

        try {
            for (String filePath : localImagePaths) {
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }
                addFilePart(byteArrays, boundary, "files", file);
            }
            addBoundaryEnd(byteArrays, boundary);
        } catch (IOException e) {
            throw new RuntimeException("Error reading image files: " + e.getMessage());
        }

        byte[] totalBody = combineByteArrays(byteArrays);
        HttpRequest.Builder builder = createBuilder(path)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(totalBody));

        sendRequest(builder, false);
    }

    public static void putMultipartImage(String path, String localFilePath) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            throw new RuntimeException("Image file not found: " + localFilePath);
        }

        String boundary = generateBoundary();
        List<byte[]> byteArrays = new ArrayList<>();

        try {
            addFilePart(byteArrays, boundary, "file", file);
            addBoundaryEnd(byteArrays, boundary);
        } catch (IOException e) {
            throw new RuntimeException("Error reading image file: " + e.getMessage());
        }

        byte[] totalBody = combineByteArrays(byteArrays);
        HttpRequest.Builder builder = createBuilder(path)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .method("PUT", HttpRequest.BodyPublishers.ofByteArray(totalBody));

        sendRequest(builder, false);
    }

    private static String generateBoundary() {
        return "JavaBoundary" + System.currentTimeMillis();
    }

    private static void addFilePart(List<byte[]> byteArrays, String boundary, String fieldName, File file) throws IOException {
        String header = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: image/jpeg\r\n\r\n";

        byteArrays.add(header.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(file.toPath()));
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private static void addBoundaryEnd(List<byte[]> byteArrays, String boundary) {
        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] combineByteArrays(List<byte[]> byteArrays) {
        long totalLength = byteArrays.stream().mapToInt(a -> a.length).sum();
        byte[] totalBody = new byte[(int) totalLength];
        int currentIndex = 0;
        for (byte[] byteArray : byteArrays) {
            System.arraycopy(byteArray, 0, totalBody, currentIndex, byteArray.length);
            currentIndex += byteArray.length;
        }
        return totalBody;
    }

    public static byte[] getImageBytes(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        String cleanPath = path.trim();

        if (cleanPath.startsWith(ApiConfig.BASE_URL)) {
            cleanPath = cleanPath.substring(ApiConfig.BASE_URL.length());
        } else if (cleanPath.startsWith("http://localhost:8080")) {
            cleanPath = cleanPath.substring("http://localhost:8080".length());
        }

        if (!cleanPath.startsWith("/api/v1/images/")) {
            cleanPath = "/api/v1/images/" + cleanPath.replaceFirst("^/+", "");
        }

        HttpRequest.Builder builder = createBuilder(cleanPath).GET();
        try {
            HttpResponse<byte[]> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                System.err.println("Error fetching image. Status: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error in connection while fetching image: " + e.getMessage());
            return null;
        }
    }

    public static String getString(String path) {
        HttpRequest.Builder builder = createBuilder(path).GET();
        try {
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }
            return null;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching raw string response from server: " + e.getMessage());
        }
    }
}


