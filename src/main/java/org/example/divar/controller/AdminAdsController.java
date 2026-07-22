package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.example.divar.component.AdSummaryCard;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;

import java.util.ArrayList;

public class AdminAdsController {

    @FXML private FlowPane adsFlowPane;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        loadAds();
    }

    private void loadAds() {
        try {
            ArrayList<Advertisement> ads = AppContext.getAdvertisementService().getActiveAdvertisements();
            adsFlowPane.getChildren().clear();
            for (Advertisement ad : ads) {
                adsFlowPane.getChildren().add(new AdSummaryCard(ad, true));
            }
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("error-message");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}







