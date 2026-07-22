package org.example.divar.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.example.divar.component.AdSummaryCard;
import org.example.divar.dto.ad.AdResponseDTO;
import org.example.divar.model.Advertisement;
import org.example.divar.model.User;
import org.example.divar.util.ApiClient;
import org.example.divar.util.ConvertToAdvertisement;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminUserAdsController {

    @FXML private FlowPane adsContainer;

    private User targetUser;

    public void setUserData(User user) {
        this.targetUser = user;
        loadUserAds();
    }

    private void loadUserAds() {
        if (targetUser == null || targetUser.getId() == null) {
            return;
        }

        new Thread(() -> fetchAndDisplayUserAds()).start();
    }

    private void fetchAndDisplayUserAds() {
        try {
            String endpoint = "/api/v1/admin/users/" + targetUser.getId() + "/ads";
            JSONArray jsonArray = ApiClient.getList(endpoint);

            Platform.runLater(() -> renderUserAds(jsonArray));

        } catch (Exception e) {
            System.err.println("Error fetching user advertisements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderUserAds(JSONArray jsonArray) {
        if (adsContainer == null) return;

        adsContainer.getChildren().clear();

        if (jsonArray == null || jsonArray.isEmpty()) {
            showEmptyAdsState();
        } else {
            populateAdCards(jsonArray);
        }
    }

    private void showEmptyAdsState() {
        Label noAdLabel = new Label("هیچ آگهی برای این کاربر یافت نشد.");
        noAdLabel.getStyleClass().add("empty-ad-label");
        adsContainer.getChildren().add(noAdLabel);
    }

    private void populateAdCards(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject adJson = jsonArray.getJSONObject(i);
                AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
                Advertisement ad = ConvertToAdvertisement.convertToAdvertisement(dto);

                AdSummaryCard card = new AdSummaryCard(ad, true);
                adsContainer.getChildren().add(card);

            } catch (Exception ex) {
                System.err.println("Error creating advertisement card: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/admin_users.fxml"));
            Parent usersPage = loader.load();

            StackPane contentArea = (StackPane) adsContainer.getScene().lookup("#contentArea");

            if (contentArea != null) {
                contentArea.getChildren().setAll(usersPage);
            }
        } catch (Exception e) {
            System.err.println("Error returning to users page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}