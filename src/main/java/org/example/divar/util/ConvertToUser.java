package org.example.divar.util;

import org.example.divar.model.User;
import org.example.divar.model.UserStatus;
import org.json.JSONObject;

public class ConvertToUser {

    public static User convertToUser(JSONObject json) {
        User user = new User();
        user.setId(json.optString("id", null));
        user.setUsername(json.optString("username", null));
        user.setFirstname(json.optString("firstname", null));
        user.setLastname(json.optString("lastname", null));
        user.setPhoneNumber(json.optString("phoneNumber", null));
        user.setEmail(json.optString("email", null));

        if (json.has("avgRating") && !json.isNull("avgRating")) {
            user.setAverageRating(json.optDouble("avgRating", 0.0));
        }

        boolean isUserEnabled = true;
        if (json.has("enabled")) {
            isUserEnabled = json.optBoolean("enabled", true);
        } else {
            isUserEnabled = json.optBoolean("enable", true);
        }

        if (isUserEnabled) {
            user.setStatus(UserStatus.ACTIVE);
        } else {
            user.setStatus(UserStatus.BANNED);
        }

        return user;
    }

    public static User convertToUserDetails(JSONObject json) {
        return convertToUser(json);
    }
}

