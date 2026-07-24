package org.example.divar.service;

import org.example.divar.dto.admin.AdminReportResponseDTO;
import org.example.divar.model.*;
import org.example.divar.dto.ad.AdModerationRequestDTO;
import org.example.divar.dto.ad.AdResponseDTO;
import org.example.divar.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * HTTP implementation of the {@link AdminService} interface responsible for communicating
 * with administrative backend endpoints. This class handles HTTP requests via {@link ApiClient},
 * data serialization/deserialization using JSON objects and DTOs, and model conversions
 * for advertisements, user management, moderation reports, and system dashboard statistics.
 *
 * @author Fatemeh Salehi Mobin
 * @version 1.0
 * @since 2026-07
 */
public class AdminServiceHttp implements AdminService {

    /**
     * Retrieves a list of all advertisements currently pending moderation approval from the server.
     *
     * @return an {@link ArrayList} of pending {@link Advertisement} objects ready for review
     * @throws RuntimeException if the network request fails or JSON parsing encounters an error
     */
    @Override
    public ArrayList<Advertisement> getPendingAdvertisements() {
        JSONArray responseArray = ApiClient.getList("/api/v1/admin/ads/moderation");
        ArrayList<Advertisement> result = new ArrayList<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject adJson = responseArray.getJSONObject(i);
            AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
            result.add(ConvertToAdvertisement.convertToAdvertisement(dto));
        }
        return result;
    }

    /**
     * Approves a specific advertisement by its unique identifier, making it active on the platform.
     *
     * @param adId the unique identifier of the advertisement to approve
     * @throws RuntimeException if the network request fails or the ad ID is invalid
     */
    @Override
    public void approveAdvertisement(long adId) {
        AdModerationRequestDTO request = new AdModerationRequestDTO(AdModerationChoice.APPROVE, null);
        ApiClient.post("/api/v1/admin/ads/" + adId + "/moderation", request.toJson());
    }

    /**
     * Rejects a specific advertisement by its unique identifier along with a mandatory rejection reason.
     *
     * @param adId         the unique identifier of the advertisement to reject
     * @param rejectReason the detailed reason explaining why the advertisement was rejected
     * @throws RuntimeException if the network request fails or the rejection reason is missing
     */
    @Override
    public void rejectAdvertisement(long adId, String rejectReason) {
        AdModerationRequestDTO request = new AdModerationRequestDTO(AdModerationChoice.REJECT, rejectReason);
        ApiClient.post("/api/v1/admin/ads/" + adId + "/moderation", request.toJson());
    }

    /**
     * Fetches a comprehensive list of all registered users in the system for administrative oversight.
     *
     * @return an {@link ArrayList} containing all registered {@link User} profiles
     * @throws RuntimeException if the server request fails
     */
    @Override
    public ArrayList<User> getAllUsers() {
        JSONArray responseArray = ApiClient.getList("/api/v1/admin/users");
        ArrayList<User> result = new ArrayList<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject userJson = responseArray.getJSONObject(i);
            result.add(ConvertToUser.convertToUser(userJson));
        }
        return result;
    }

    /**
     * Fetches detailed information for a specific user identified by their unique user ID.
     *
     * @param userId the unique numeric identifier of the user
     * @return a fully populated {@link User} object containing detailed profile attributes
     * @throws RuntimeException if the user does not exist or the request fails
     */
    @Override
    public User getUserDetails(long userId) {
        JSONObject userJson = ApiClient.get("/api/v1/admin/users/" + userId);
        return ConvertToUser.convertToUserDetails(userJson);
    }

    /**
     * Unblocks a previously suspended or banned user, restoring their account access.
     *
     * @param id the unique string identifier of the user account to unblock
     * @throws RuntimeException if the network operation fails
     */
    @Override
    public void unblockUser(String id) {
        ApiClient.post("/api/v1/admin/users/unblock/" + id, new JSONObject());
    }

    /**
     * Blocks a user account with a specified reason for disciplinary action.
     *
     * @param id     the unique string identifier of the user account to block
     * @param reason the justification or reason for blocking the user
     * @throws RuntimeException if the network request fails
     */
    @Override
    public void blockUser(String id, String reason) {
        String endpoint = "/api/v1/admin/users/block/" + id;

        JSONObject body = new JSONObject();
        if (reason != null && !reason.isBlank()) {
            body.put("reason", reason);
        }

        ApiClient.post(endpoint, body);
    }

    /**
     * Retrieves all active user reports submitted against problematic advertisements.
     *
     * @return an {@link ArrayList} of {@link AdminReport} objects detailing the reported ads and reasons
     * @throws RuntimeException if fetching reports from the server fails
     */
    @Override
    public ArrayList<AdminReport> getReports() {
        JSONArray jsonArray = ApiClient.getList("/api/v1/admin/reported-ads");
        ArrayList<AdminReport> reports = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                AdminReportResponseDTO dto = AdminReportResponseDTO.fromJson(obj);

                AdminReport report = ConvertToAdminReport.convertToAdminReport(dto);
                if (report != null) {
                    reports.add(report);
                }
            }
        }

        return reports;
    }

    /**
     * Fetches all advertisements published by a specific user targeted in the admin panel.
     *
     * @param userId the unique numeric identifier of the user whose ads are being queried
     * @return an {@link ArrayList} of {@link Advertisement} items posted by the user
     * @throws RuntimeException if the network request encounters an error
     */
    @Override
    public ArrayList<Advertisement> getUserAdvertisements(long userId) {
        String endpoint = "/api/v1/admin/users/" + userId + "/ads";
        JSONArray jsonArray = ApiClient.getList(endpoint);
        ArrayList<Advertisement> result = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject adJson = jsonArray.getJSONObject(i);
                    AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
                    result.add(ConvertToAdvertisement.convertToAdvertisement(dto));
                } catch (Exception ex) {
                    System.err.println("Error creating advertisement card: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Fetches general statistical metrics for the admin main dashboard (e.g., active users, total ads, pending counts).
     *
     * @return a {@link DashboardStatistics} object containing current system metrics
     * @throws RuntimeException if fetching dashboard statistics fails
     */
    @Override
    public DashboardStatistics getDashboardStats() {
        JSONObject json = ApiClient.get("/api/v1/admin/dashboard/stats");
        return ConvertToDashboardStatistics.convert(json);
    }
}


