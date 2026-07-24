package org.example.divar.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.example.divar.component.AdSummaryCard;
import org.example.divar.model.Advertisement;
import org.example.divar.model.User;
import org.example.divar.service.AdminService;
import org.example.divar.service.AdminServiceHttp;
import java.util.ArrayList;

/**
 * Controller class for managing and displaying a specific user's advertisements in the admin panel.
 */
public class AdminUserAdsController {

    @FXML private FlowPane adsContainer;

    private User targetUser;
    private final AdminService adminService = new AdminServiceHttp();

    /**
     * Sets the target user and triggers loading of their advertisements.
     *
     * @param user the user whose advertisements are to be displayed
     */
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
            ArrayList<Advertisement> ads = adminService.getUserAdvertisements(Long.parseLong(targetUser.getId()));
            Platform.runLater(() -> renderUserAds(ads));

        } catch (Exception e) {
            System.err.println("Error fetching user advertisements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderUserAds(ArrayList<Advertisement> ads) {
        if (adsContainer == null) return;

        adsContainer.getChildren().clear();

        if (ads == null || ads.isEmpty()) {
            showEmptyAdsState();
        } else {
            populateAdCards(ads);
        }
    }

    private void showEmptyAdsState() {
        Label noAdLabel = new Label("هیچ آگهی برای این کاربر یافت نشد.");
        noAdLabel.getStyleClass().add("empty-ad-label");
        adsContainer.getChildren().add(noAdLabel);
    }

    private void populateAdCards(ArrayList<Advertisement> ads) {
        for (Advertisement ad : ads) {
            try {
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