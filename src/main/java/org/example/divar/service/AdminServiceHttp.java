package org.example.divar.service;

import org.example.divar.dto.admin.AdminReportResponseDTO;
import org.example.divar.model.*;
import org.example.divar.dto.ad.AdModerationRequestDTO;
import org.example.divar.dto.ad.AdResponseDTO;
import org.example.divar.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

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
                AdminReportResponseDTO dto = AdminReportResponseDTO.fromJson(obj);

                AdminReport report = ConvertToAdminReport.convertToAdminReport(dto);
                if (report != null) {
                    reports.add(report);
                }
            }
        }

        return reports;
    }

    @Override
    public void resolveReport(long reportId, ReportResolutionAction action, String note) {
        String endpoint = "/api/v1/admin/reported-ads/" + reportId + "/resolve";

        JSONObject body = new JSONObject();
        body.put("action", action.name());
        if (note != null && !note.isBlank()) {
            body.put("note", note);
        }
        ApiClient.post(endpoint, body);
    }

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
}


