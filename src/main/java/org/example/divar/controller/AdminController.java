package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.divar.SwitchStage;
import org.example.divar.component.PendingAdCard;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;

import java.util.ArrayList;

public class AdminController {

    @FXML private VBox pendingAdsBox;
    @FXML private Label emptyLabel;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        loadPendingAds();
    }

    private void loadPendingAds() {
        pendingAdsBox.getChildren().clear();

        if (messageLabel != null) {
            messageLabel.setVisible(false);
            messageLabel.setText("");
        }

        try {
            ArrayList<Advertisement> pendingAds = AppContext.getAdminService().getPendingAdvertisements();

            if (pendingAds.isEmpty()) {
                emptyLabel.setText("در حال حاضر آگهی‌ای در انتظار بررسی نیست.");
                emptyLabel.setVisible(true);
                return;
            }
            emptyLabel.setVisible(false);

            for (Advertisement ad : pendingAds) {
                pendingAdsBox.getChildren().add(new PendingAdCard(ad));
            }

        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void refresh() {
        loadPendingAds();
    }

    @FXML
    private void goBack() {
        SwitchStage.switchToProfile();
    }

    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            if (!messageLabel.getStyleClass().contains("error-message")) {
                messageLabel.getStyleClass().add("error-message");
            }

            messageLabel.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }

}

