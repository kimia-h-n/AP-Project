package org.example.divar.controller;

import org.example.divar.SwitchStage;
import org.example.divar.model.*;
import org.example.divar.util.AppContext;
import org.example.divar.component.AdSummaryCard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import org.example.divar.util.SessionManager;
import org.example.divar.component.FavoriteAdCard;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    @FXML private VBox menu;
    @FXML private Hyperlink favoriteLink;
    @FXML private Hyperlink advertisementLink;
    @FXML private Label emptyLabel;
    @FXML private Region activeIndicator;

    private GridPane adsGrid;
    private List<Advertisement> currentAds = new ArrayList<>();
    private String currentMode = "";

    private static String lastProfileTab = "myAdvertisements";

    @FXML
    public void initialize() {
        adsGrid = new GridPane();
        adsGrid.setHgap(15.0);
        adsGrid.setVgap(15.0);
        adsGrid.setVisible(false);
        adsGrid.setManaged(false);

        menu.getChildren().add(adsGrid);

        menu.widthProperty().addListener((obs, old, newVal) -> {
            if (newVal.doubleValue() > 0) {
                if (adsGrid.isVisible()) {
                    renderGrid(newVal.doubleValue());
                }
            }
        });

        if ("favorites".equals(lastProfileTab)) {
            showFavorites();
        } else {
            showMyAdvertisements();
        }
    }

    private List<Advertisement> getFavorites() {
        String currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername == null) {
            return new ArrayList<>();
        } else {
            return AppContext.getAdvertisementService().getFavoriteAdvertisements(currentUsername);
        }
    }

    @FXML
    private void showFavorites() {
        lastProfileTab = "favorites";
        setActiveMenu(favoriteLink, 0);
        currentMode = "favorites";
        currentAds = getFavorites();

        adsGrid.setVisible(true);
        adsGrid.setManaged(true);

        double targetWidth;
        if (menu.getWidth() > 0) {
            targetWidth = menu.getWidth();
        } else {
            targetWidth = 900;
        }
        renderGrid(targetWidth);
    }

    private List<Advertisement> getMyAdvertisements() {
        return AppContext.getAdvertisementService().getMyAdvertisements();
    }

    @FXML
    private void showMyAdvertisements() {
        lastProfileTab = "myAdvertisements";
        setActiveMenu(advertisementLink, 42);
        currentMode = "myAdvertisements";
        currentAds = getMyAdvertisements();

        adsGrid.setVisible(true);
        adsGrid.setManaged(true);

        double targetWidth;
        if (menu.getWidth() > 0) {
            targetWidth = menu.getWidth();
        } else {
            targetWidth = 900;
        }
        renderGrid(targetWidth);
    }

    private void renderGrid(double width) {
        adsGrid.getChildren().clear();

        if (currentAds.isEmpty()) {
            emptyLabel.setText("موردی برای نمایش وجود ندارد.");
            emptyLabel.setVisible(true);
            return;
        }
        emptyLabel.setVisible(false);

        double calcColumns = width / 240;
        int columns;
        if (calcColumns > 1) {
            columns = (int) calcColumns;
        } else {
            columns = 1;
        }

        int row = 0;
        int col = 0;

        for (Advertisement ad : currentAds) {
            if (currentMode.equals("favorites")) {
                FavoriteAdCard card = new FavoriteAdCard(ad, () -> {
                    currentAds.remove(ad);
                    double currentWidth;
                    if (menu.getWidth() > 0) {
                        currentWidth = menu.getWidth();
                    } else {
                        currentWidth = 900;
                    }
                    renderGrid(currentWidth);
                });
                adsGrid.add(card, col, row);
            } else {
                AdSummaryCard card = new AdSummaryCard(ad, false);
                adsGrid.add(card, col, row);
            }

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private void setActiveMenu(Hyperlink selectedMenu, double linePosition) {
        Hyperlink[] allMenus = {favoriteLink, advertisementLink};

        for (Hyperlink menuLink : allMenus) {
            if (menuLink != null) {
                menuLink.getStyleClass().remove("menu-link-active");
                menuLink.getStyleClass().add("menu-link-normal");
            }
        }

        if (selectedMenu != null) {
            selectedMenu.getStyleClass().remove("menu-link-normal");
            selectedMenu.getStyleClass().add("menu-link-active");

            activeIndicator.setVisible(true);
            VBox.setMargin(activeIndicator, new Insets(linePosition, 0, 0, 0));
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
    private void goToChat() {
        SwitchStage.switchToChat();
    }
}








