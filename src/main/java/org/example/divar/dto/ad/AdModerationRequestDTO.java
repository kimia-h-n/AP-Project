package org.example.divar.dto.ad;

import org.example.divar.model.AdModerationChoice;
import org.json.JSONObject;

public class AdModerationRequestDTO {

    private final AdModerationChoice choice;
    private final String rejectReason;

    public AdModerationRequestDTO(AdModerationChoice choice, String rejectReason) {
        this.choice = choice;
        this.rejectReason = rejectReason;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("choice", choice.name());
        if (rejectReason != null && !rejectReason.trim().isEmpty()) {
            json.put("rejectReason", rejectReason.trim());
        }
        return json;
    }
}
