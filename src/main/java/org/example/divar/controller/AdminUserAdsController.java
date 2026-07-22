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

import java.util.ArrayList;

public class AdminUserAdsController {

    @FXML private FlowPane adsContainer;

    private User targetUser;

    public void setUserData(User user) {
        this.targetUser = user;
        loadUserAds();
    }

    private void loadUserAds() {
        if (targetUser == null || targetUser.getId() == null) return;

        new Thread(() -> {
            try {
                String endpoint = "/api/v1/admin/users/" + targetUser.getId() + "/ads";
                JSONArray jsonArray = ApiClient.getList(endpoint);

                Platform.runLater(() -> {
                    if (adsContainer != null) {
                        adsContainer.getChildren().clear();

                        if (jsonArray == null || jsonArray.isEmpty()) {
                            Label noAdLabel = new Label("هیچ آگهی برای این کاربر یافت نشد.");
                            noAdLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 14px;");
                            adsContainer.getChildren().add(noAdLabel);
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject adJson = jsonArray.getJSONObject(i);

                                    AdResponseDTO dto = AdResponseDTO.fromJson(adJson);
                                    Advertisement ad = ConvertToAdvertisement.convertToAdvertisement(dto);

                                    AdSummaryCard card = new AdSummaryCard(ad, true);

                                    adsContainer.getChildren().add(card);

                                } catch (Exception ex) {
                                    System.err.println("خطا در ساخت کارت آگهی: " + ex.getMessage());
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });

            } catch (Exception e) {
                System.err.println("خطا در دریافت آگهی‌های کاربر: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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
            System.err.println("خطا در بازگشت به صفحه کاربران: " + e.getMessage());
            e.printStackTrace();
        }
    }
}