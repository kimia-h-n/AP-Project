package org.example.divar.service;

import org.example.divar.model.*;
import org.example.divar.dto.ad.AdModerationRequestDTO;
import org.example.divar.dto.ad.AdResponseDTO;
import org.example.divar.util.ApiClient;
import org.example.divar.util.ConvertToAdvertisement;
import org.example.divar.util.ConvertToUser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminServiceHttp implements AdminService {

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

    @Override
    public void approveAdvertisement(long adId) {
        AdModerationRequestDTO request = new AdModerationRequestDTO(AdModerationChoice.APPROVE, null);
        ApiClient.post("/api/v1/admin/ads/" + adId + "/moderation", request.toJson());
    }

    @Override
    public void rejectAdvertisement(long adId, String rejectReason) {
        AdModerationRequestDTO request = new AdModerationRequestDTO(AdModerationChoice.REJECT, rejectReason);
        ApiClient.post("/api/v1/admin/ads/" + adId + "/moderation", request.toJson());
    }

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

    @Override
    public User getUserDetails(long userId) {
        JSONObject userJson = ApiClient.get("/api/v1/admin/users/" + userId);
        return ConvertToUser.convertToUserDetails(userJson);
    }

    @Override
    public void unblockUser(String id) {
        ApiClient.post("/api/v1/admin/users/unblock/" + id, new JSONObject());
    }

    @Override
    public void blockUser(String id, String reason) {
        String endpoint = "/api/v1/admin/users/block/" + id;

        System.out.println("\n========== [REQUEST DEBUG] ==========");
        System.out.println("Action: Block User");
        System.out.println("Target ID in URL: " + id);
        System.out.println("Full Endpoint: " + endpoint);
        System.out.println("Reason: " + reason);
        System.out.println("=====================================\n");

        JSONObject body = new JSONObject();
        if (reason != null && !reason.isBlank()) {
            body.put("reason", reason);
        }

        ApiClient.post(endpoint, body);
    }

    @Override
    public ArrayList<AdminReport> getReports() {
        JSONArray jsonArray = ApiClient.getList("/api/v1/admin/reported-ads");
        ArrayList<AdminReport> reports = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                AdminReport report = new AdminReport();

                report.setId(obj.optLong("adReportId", obj.optLong("id", 0)));
                report.setAdId(obj.optLong("adId", 0));
                report.setAdTitle(obj.optString("adTitle", ""));

                String firstName = obj.optString("sellerFirstName", "");
                String lastName = obj.optString("sellerLastName", "");
                report.setSellerFullName((firstName + " " + lastName).trim());

                report.setReason(obj.optString("reportReason", ""));
                report.setImageUrl(obj.optString("primaryImageUrl", ""));

                reports.add(report);
            }
        }

        return reports;
    }

    @Override
    public void resolveReport(long reportId, ReportResolutionAction action, String note) {
    }
}


