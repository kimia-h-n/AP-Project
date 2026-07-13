package org.example.divar.controller;

import org.example.divar.SwitchStage;
import org.example.divar.model.*;
import org.example.divar.service.AdvertisementService;
import org.example.divar.util.AppContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import org.example.divar.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AdvertisementDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label addressLabel;
    @FXML private Label statusLabel;
    @FXML private Label priceLabel;
    @FXML private Label categoryLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label messageLabel;
    @FXML private ImageView mainImage;
    @FXML private HBox imageContainer;
    @FXML private Button favoriteBtn;

    private javafx.stage.Stage reportWindow;
    private javafx.stage.Stage successWindow;
    private Advertisement currentAdvertisement;
    private boolean isFavoriteAdvertisement = false;

    public void showAdvertisement(Advertisement advertisement) {
        Advertisement freshAd;
        try {
            freshAd = AppContext.getAdvertisementService().getAdvertisementById(advertisement.getId());
        } catch (RuntimeException e) {
            showAccessDeniedState(e.getMessage());
            return;
        }

        this.currentAdvertisement = freshAd;

        titleLabel.setText(freshAd.getTitle());
        addressLabel.setText(freshAd.getAddress());
        statusLabel.setText(freshAd.getStatus().toString());
        categoryLabel.setText(freshAd.getCategory().toString());
        if (freshAd.getDescription() != null) {
            descriptionLabel.setText(freshAd.getDescription());
        } else {
            descriptionLabel.setText("");
        }
        if (freshAd.getPrice() > 0) {
            priceLabel.setText(String.format("%,d", freshAd.getPrice()) + " تومان");
        }

        String currentUsername = SessionManager.getCurrentUsername();

        if (currentUsername != null) {
            isFavoriteAdvertisement = freshAd.isFavorite();
        }

        if (isFavoriteAdvertisement) {
            favoriteBtn.setText("★ نشان شده");
            favoriteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a62626; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            favoriteBtn.setText("★ نشان کردن");
            favoriteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px;");
        }

        imageContainer.getChildren().clear();
        if (freshAd.getImagePaths() != null && !freshAd.getImagePaths().isEmpty()) {
            setMainImage(freshAd.getImagePaths().get(0));
            for (String path : freshAd.getImagePaths()) {
                ImageView view = createOtherImages(path);
                if (view != null) {
                    imageContainer.getChildren().add(view);
                }
            }
        } else {
            setMainImage(null);
        }
    }

    private void showAccessDeniedState(String message) {
        titleLabel.setText("");
        addressLabel.setText("");
        statusLabel.setText("");
        categoryLabel.setText("");
        descriptionLabel.setText("");
        priceLabel.setText("");
        imageContainer.getChildren().clear();
        favoriteBtn.setVisible(false);
        favoriteBtn.setManaged(false);
        showError(message);
    }

    private ImageView createOtherImages(String path) {
        try {
            Image image = loadImages(path);
            if (image == null) {
                return null;
            }

            ImageView view = new ImageView(image);
            view.setFitWidth(65.0);
            view.setFitHeight(65.0);
            view.setPreserveRatio(true);
            view.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 4px; -fx-cursor: hand;");
            view.setOnMouseClicked(e -> setMainImage(path));

            return view;
        } catch (Exception e) {
            System.out.println("خطا در بارگزای تصاویر زیر تصویر اصلی" + e.getMessage());
            return null;
        }
    }

    private Image loadImages(String imagePath) {
        if (imagePath != null) {
            try {
                File file = new File(imagePath);
                if (file.exists() && file.isFile()) {
                    return new Image(file.toURI().toString(), true);
                }
            } catch (Exception e) {
                System.out.println("خطا در لود کردن عکس" + e.getMessage());
            }
        }
        return loadDefaultImage();
    }

    private Image loadDefaultImage() {
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/divar/images/current.jpg")));
        } catch (Exception e) {
            System.out.println("عکس پیش فرض پیدا نشد");
            return null;
        }
    }

    private void setMainImage(String path) {
        try {
            if (path != null) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    mainImage.setImage(new Image(file.toURI().toString(), true));
                    return;
                }
            }
            mainImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/divar/images/current.jpg"))));
        } catch (Exception e) {
            System.out.println("خطا در بارگذاری عکس اصلی" + e.getMessage());
        }
    }

    @FXML
    private void chatWithSeller() {
        if (messageLabel != null) {
            messageLabel.setVisible(false);
            messageLabel.setText("");
        }

        String myUsername = SessionManager.getCurrentUsername();
        if (myUsername == null) {
            showError("برای گفتگو ابتدا باید وارد حساب کاربری شوید.");
            return;
        }

        String sellerUsername = currentAdvertisement.getSeller().getUsername();

        if (currentAdvertisement == null || sellerUsername == null) {
            return;
        }

        if (myUsername.equals(sellerUsername)) {
            showError("شما نمی‌توانید برای آگهی خودتان گفت‌وگو شروع کنید.");
            return;
        }

        Conversation conversation = AppContext.getConversationService()
                .findOrCreateConversation(currentAdvertisement, myUsername, sellerUsername);

        SwitchStage.switchToChat(conversation);
    }


    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: #a62626; -fx-font-weight: bold; -fx-font-size: 14px;");
            messageLabel.setVisible(true);
        } else {
            System.out.println(message);
        }
    }

    @FXML
    private void manageFavoriteAdvertisement() {
        String currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername == null) {
            showError("لطفاً ابتدا وارد حساب کاربری خود شوید.");
            return;
        }

        favoriteBtn.setDisable(true);

        AdvertisementService adService = AppContext.getAdvertisementService();

        try {
            if (isFavoriteAdvertisement) {
                adService.removeFromFavorites(currentAdvertisement.getId());
                isFavoriteAdvertisement = false;
            } else {
                adService.addToFavorites(currentAdvertisement.getId());
                isFavoriteAdvertisement = true;
            }
            updateFavoriteButtonUI();

        } catch (RuntimeException e) {
            showError(e.getMessage());
        } finally {
            favoriteBtn.setDisable(false);
        }
    }

    private void updateFavoriteButtonUI() {
        if (isFavoriteAdvertisement) {
            favoriteBtn.setText("★ نشان شده");
            favoriteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a62626; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            favoriteBtn.setText("★ نشان کردن");
            favoriteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px;");
        }
    }


    @FXML
    private void reportAdvertisement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/report.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            reportWindow = new javafx.stage.Stage();
            reportWindow.initModality(Modality.APPLICATION_MODAL);
            reportWindow.initStyle(StageStyle.TRANSPARENT);

            StackPane blackBackground = new StackPane(root);
            blackBackground.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-padding: 30px;");
            blackBackground.setOnMouseClicked(e -> {
                if (e.getTarget() == blackBackground) closeReportWindow();
            });

            Scene scene = new Scene(blackBackground);
            scene.setFill(Color.TRANSPARENT);
            reportWindow.setScene(scene);
            reportWindow.show();

        } catch (IOException e) {
            System.err.println("Could not load the FXML file." + e.getMessage());
        }
    }

    @FXML
    private void closeReportWindow() {
        if (reportWindow != null) {
            reportWindow.close();
        }
    }

    @FXML
    public void sendReport() {
        closeReportWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/success_report.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            successWindow = new javafx.stage.Stage();
            successWindow.initModality(Modality.APPLICATION_MODAL);
            successWindow.initStyle(StageStyle.TRANSPARENT);

            StackPane shadowBackground = new StackPane(root);
            shadowBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 20px;");

            Scene scene = new Scene(shadowBackground);
            scene.setFill(Color.TRANSPARENT);
            successWindow.setScene(scene);
            successWindow.showAndWait();

        } catch (IOException e) {
            System.err.println("خطا در باز کردن فایل fxml موفقیت" + e.getMessage());
        }
    }


    @FXML private void goToProfile() {
        SwitchStage.switchToProfile(); }

    @FXML private void goBack() {
        SwitchStage.goBack(); }

    @FXML private void goToChat() {
        SwitchStage.switchToChat(); }
}

