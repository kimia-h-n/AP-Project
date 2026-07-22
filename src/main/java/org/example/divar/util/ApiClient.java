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
        HttpRequest.Builder builder = createBuilder(path)
                .DELETE();
        return (JSONObject) sendRequest(builder, false);
    }

    private static Object sendRequest(HttpRequest.Builder builder, boolean isList) {
        try {
            HttpRequest request = builder.build();

            // --- لاگ پیشرفته برای عیب‌یابی دقیق ---
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

    public static void postMultipartImages(String path, java.util.ArrayList<String> localImagePaths) {
        String boundary = "JavaBoundary" + System.currentTimeMillis();

        java.util.List<byte[]> byteArrays = new java.util.ArrayList<>();
        try {
            for (String filePath : localImagePaths) {
                java.io.File file = new java.io.File(filePath);
                if (!file.exists()) continue;

                String header = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"files\"; filename=\"" + file.getName() + "\"\r\n" +
                        "Content-Type: image/jpeg\r\n\r\n";

                byteArrays.add(header.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                byteArrays.add(java.nio.file.Files.readAllBytes(file.toPath()));
                byteArrays.add("\r\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            byteArrays.add(("--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("خطا در خواندن فایل‌های تصویر: " + e.getMessage());
        }

        long totalLength = byteArrays.stream().mapToInt(a -> a.length).sum();

        byte[] totalBody = new byte[(int) totalLength];
        int currentIndex = 0;
        for (byte[] byteArray : byteArrays) {
            System.arraycopy(byteArray, 0, totalBody, currentIndex, byteArray.length);
            currentIndex += byteArray.length;
        }

        HttpRequest.Builder builder = createBuilder(path)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(totalBody));

        sendRequest(builder, false);
    }

    public static void putMultipartImage(String path, String localFilePath) {
        String boundary = "JavaBoundary" + System.currentTimeMillis();
        java.io.File file = new java.io.File(localFilePath);

        if (!file.exists()) {
            throw new RuntimeException("فایل عکس پیدا نشد: " + localFilePath);
        }

        java.util.List<byte[]> byteArrays = new java.util.ArrayList<>();
        try {
            String header = "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type: image/jpeg\r\n\r\n";

            byteArrays.add(header.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byteArrays.add(java.nio.file.Files.readAllBytes(file.toPath()));
            byteArrays.add("\r\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byteArrays.add(("--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("خطا در خواندن فایل عکس: " + e.getMessage());
        }

        long totalLength = byteArrays.stream().mapToInt(a -> a.length).sum();
        byte[] totalBody = new byte[(int) totalLength];
        int currentIndex = 0;
        for (byte[] byteArray : byteArrays) {
            System.arraycopy(byteArray, 0, totalBody, currentIndex, byteArray.length);
            currentIndex += byteArray.length;
        }

        HttpRequest.Builder builder = createBuilder(path)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .method("PUT", HttpRequest.BodyPublishers.ofByteArray(totalBody));

        sendRequest(builder, false);
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
                System.err.println("خطا در دریافت عکس. وضعیت: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("خطا در ارتباط برای دریافت عکس: " + e.getMessage());
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



