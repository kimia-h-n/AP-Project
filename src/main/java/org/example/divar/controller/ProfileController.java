package org.example.divar.controller;

import org.example.divar.SwitchStage;
import org.example.divar.model.*;
import org.example.divar.util.AppContext;
import org.example.divar.component.AdvertisementCard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.divar.util.SessionManager;
import org.example.divar.component.FavoriteAdCard;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    @FXML private VBox menu;
    @FXML private Button favoriteBtn;
    @FXML private Button AdvertisementBtn;
    @FXML private Button backBtn;
    @FXML private Label emptyLabel;
    @FXML private Button adminPanelBtn;

    private GridPane adsGrid;
    private VBox favoritesBox;
    private List<Advertisement> currentAds = new ArrayList<>();

    @FXML
    public void initialize() {
        String currentUsername = SessionManager.getCurrentUsername();
        boolean isAdmin = "admin".equals(currentUsername);

        if (!isAdmin) {
            adminPanelBtn.setVisible(false);
            adminPanelBtn.setManaged(false);
        }

        adsGrid = new GridPane();
        adsGrid.setHgap(15.0);
        adsGrid.setVgap(15.0);
        adsGrid.setVisible(false);
        adsGrid.setManaged(false);

        favoritesBox = new VBox(12.0);
        favoritesBox.setMaxWidth(700.0);
        favoritesBox.setVisible(false);
        favoritesBox.setManaged(false);

        menu.getChildren().addAll(adsGrid, favoritesBox);

        menu.widthProperty().addListener((obs, old, newVal) -> {
            if (newVal.doubleValue() > 0 && adsGrid.isVisible()) {
                renderMyAds(newVal.doubleValue());
            }
        });
    }

    private List<Advertisement> getFavorites() {
        String currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername == null) {
            return new ArrayList<>();
        }
        return AppContext.getAdvertisementService().getFavoriteAdvertisements(currentUsername);
    }

    @FXML
    private void showFavorites() {
        setStyleForButton(favoriteBtn);
        currentAds = getFavorites();

        adsGrid.setVisible(false);
        adsGrid.setManaged(false);
        favoritesBox.setVisible(true);
        favoritesBox.setManaged(true);

        renderFavorites();
    }

    private List<Advertisement> getMyAdvertisements() {
        String currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername == null) {
            return new ArrayList<>();
        }
        return AppContext.getAdvertisementService().getAdvertisementsByUser(currentUsername);
    }

    @FXML
    private void showMyAdvertisements() {
        setStyleForButton(AdvertisementBtn);
        currentAds = getMyAdvertisements();

        favoritesBox.setVisible(false);
        favoritesBox.setManaged(false);
        adsGrid.setVisible(true);
        adsGrid.setManaged(true);

        renderMyAds(menu.getWidth() > 0 ? menu.getWidth() : 900);
    }

    private void renderFavorites() {
        favoritesBox.getChildren().clear();

        if (currentAds.isEmpty()) {
            emptyLabel.setText("موردی برای نمایش وجود ندارد.");
            emptyLabel.setVisible(true);
            return;
        }
        emptyLabel.setVisible(false);

        for (Advertisement ad : currentAds) {
            FavoriteAdCard card = new FavoriteAdCard(ad, removedAd -> {
                currentAds.remove(removedAd);
                renderFavorites();
            });
            favoritesBox.getChildren().add(card);
        }
    }

    private void renderMyAds(double width) {
        adsGrid.getChildren().clear();

        if (currentAds.isEmpty()) {
            emptyLabel.setText("موردی برای نمایش وجود ندارد.");
            emptyLabel.setVisible(true);
            return;
        }
        emptyLabel.setVisible(false);

        int columns = (int) Math.max(1, width / 240);
        int row = 0, col = 0;
        for (Advertisement ad : currentAds) {
            AdvertisementCard card = new AdvertisementCard(ad);
            adsGrid.add(card, col, row);
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private void setStyleForButton(Button activeButton) {
        Button[] buttons = {favoriteBtn, AdvertisementBtn, adminPanelBtn};

        for (Button btn : buttons) {
            if (btn != null) {
                btn.getStyleClass().remove("menu-btn-active");
                btn.getStyleClass().add("menu-btn-normal");
            }
        }

        if (activeButton != null) {
            activeButton.getStyleClass().remove("menu-btn-normal");
            activeButton.getStyleClass().add("menu-btn-active");
        }
    }

    @FXML
    private void logout() {
        SessionManager.logout();
        SwitchStage.showLogin();
    }

    @FXML
    private void goBack() {
        SwitchStage.goBack();
    }

    @FXML
    private void goToAdminPanel() {
        SwitchStage.switchToAdminPanel();
    }
}




