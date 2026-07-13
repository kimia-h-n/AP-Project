package org.example.divar.service;

import org.example.divar.model.AdModerationChoice;
import org.example.divar.model.Advertisement;
import org.example.divar.dto.ad.AdModerationRequestDTO;
import org.example.divar.dto.ad.AdResponseDTO;
import org.example.divar.util.ApiClient;
import org.example.divar.util.ConvertToAdvertisement;
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
}
