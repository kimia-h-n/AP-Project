package org.example.divar.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.divar.SwitchStage;
import org.example.divar.chat.model.Conversation;
import org.example.divar.component.ImageGallery;
import org.example.divar.component.SellerRatingDialog;
import org.example.divar.model.*;
import org.example.divar.service.AdvertisementService;
import org.example.divar.util.AppContext;
import org.example.divar.util.SessionManager;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AdvertisementDetailsController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm", Locale.ENGLISH)
                    .withZone(ZoneId.of("Asia/Tehran"));

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label categoryLabel;
    @FXML private Label conditionLabel;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label updatedAtLabel;
    @FXML private Label sellerLabel;
    @FXML private Label lblSellerRating;
    @FXML private Label addressLabel;
    @FXML private Label messageLabel;
    @FXML private ImageView mainImage;
    @FXML private HBox thumbnailBox;
    @FXML private Label counterLabel;
    @FXML private Button topChatBtn;
    @FXML private Button chatBtn;
    @FXML private Button favoriteBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button reportButton;
    @FXML private Button rateSellerBtn;

    private Advertisement currentAdvertisement;
    private boolean isFavoriteAdvertisement = false;
    private ImageGallery gallery;

    @FXML
    public void initialize() {
        gallery = new ImageGallery(mainImage, thumbnailBox, counterLabel);
    }

    public void showAdvertisement(Advertisement advertisement) {
        Advertisement freshAd;
        try {
            freshAd = AppContext.getAdvertisementService().getAdvertisementById(advertisement.getId());
        } catch (RuntimeException e) {
            showAccessDeniedState(e.getMessage());
            return;
        }

        this.currentAdvertisement = freshAd;

        if (freshAd.getTitle() != null && !freshAd.getTitle().isBlank()) {
            titleLabel.setText(freshAd.getTitle());
        } else {
            titleLabel.setText("-");
        }

        if (freshAd.getDescription() != null && !freshAd.getDescription().isBlank()) {
            descriptionLabel.setText(freshAd.getDescription());
        } else {
            descriptionLabel.setText("-");
        }

        if (freshAd.getCategory() != null) {
            categoryLabel.setText(freshAd.getCategory().toString());
        } else {
            categoryLabel.setText("-");
        }

        if (freshAd.getCondition() != null) {
            conditionLabel.setText(freshAd.getCondition().toString());
        } else {
            conditionLabel.setText("-");
        }

        if (freshAd.getPrice() > 0) {
            priceLabel.setText(String.format("%,d", freshAd.getPrice()) + " تومان");
        } else {
            priceLabel.setText("-");
        }

        if (freshAd.getCity() != null) {
            cityLabel.setText(freshAd.getCity().toString());
        } else {
            cityLabel.setText("-");
        }

        createdAtLabel.setText(formatInstant(freshAd.getCreatedAt()));
        updatedAtLabel.setText(formatInstant(freshAd.getUpdatedAt()));

        if (freshAd.getAddress() != null && !freshAd.getAddress().isBlank()) {
            addressLabel.setText(freshAd.getAddress());
        } else {
            addressLabel.setText("-");
        }

        if (freshAd.getSeller() != null && freshAd.getSeller().getFullName() != null && !freshAd.getSeller().getFullName().isBlank()) {
            sellerLabel.setText(freshAd.getSeller().getFullName());
        } else {
            sellerLabel.setText("-");
        }

        if (lblSellerRating != null) {
            double avg = freshAd.getSellerRating();
            if (avg > 0) {
                lblSellerRating.setText(String.format("%.1f از ۵", avg));
            } else {
                lblSellerRating.setText("بدون امتیاز");
            }
        }

        String currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername != null) {
            isFavoriteAdvertisement = freshAd.isFavorite();
        }
        updateFavoriteButtonUI();

        boolean isMine = checkIfAdvertisementIsMine(freshAd, currentUsername);
        updateActionButtonsVisibility(isMine);

        gallery.setImages(freshAd.getImagePaths());
    }

    private boolean checkIfAdvertisementIsMine(Advertisement freshAd, String currentUsername) {
        if (currentUsername == null) {
            return false;
        }

        if (freshAd.getSeller() != null && currentUsername.equals(freshAd.getSeller().getUsername())) {
            return true;
        }

        try {
            var myAds = AppContext.getAdvertisementService().getMyAdvertisements();
            for (Advertisement ad : myAds) {
                if (ad.getId() == freshAd.getId()) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private String formatInstant(Instant instant) {
        if (instant == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER.format(instant);
    }

    private void updateActionButtonsVisibility(boolean isMine) {
        boolean isAdmin = SessionManager.isAdmin();

        editBtn.setVisible(isMine && !isAdmin);
        editBtn.setManaged(isMine && !isAdmin);

        deleteBtn.setVisible(isMine || isAdmin);
        deleteBtn.setManaged(isMine || isAdmin);

        boolean showBuyerActions = !isMine && !isAdmin;

        if (topChatBtn != null) {
            topChatBtn.setVisible(showBuyerActions);
            topChatBtn.setManaged(showBuyerActions);
        }
        if (chatBtn != null) {
            chatBtn.setVisible(showBuyerActions);
            chatBtn.setManaged(showBuyerActions);
        }
        favoriteBtn.setVisible(showBuyerActions);
        favoriteBtn.setManaged(showBuyerActions);
        reportButton.setVisible(showBuyerActions);
        reportButton.setManaged(showBuyerActions);

        if (rateSellerBtn != null) {
            rateSellerBtn.setVisible(showBuyerActions);
            rateSellerBtn.setManaged(showBuyerActions);
        }
    }

    private void showAccessDeniedState(String message) {
        titleLabel.setText("");
        descriptionLabel.setText("");
        categoryLabel.setText("");
        conditionLabel.setText("");
        priceLabel.setText("");
        cityLabel.setText("");
        addressLabel.setText("");
        createdAtLabel.setText("");
        updatedAtLabel.setText("");
        sellerLabel.setText("");
        if (lblSellerRating != null) lblSellerRating.setText("");
        gallery.setImages(null);
        favoriteBtn.setVisible(false);
        favoriteBtn.setManaged(false);
        editBtn.setVisible(false);
        editBtn.setManaged(false);
        deleteBtn.setVisible(false);
        deleteBtn.setManaged(false);
        if (topChatBtn != null) {
            topChatBtn.setVisible(false);
            topChatBtn.setManaged(false);
        }
        if (chatBtn != null) {
            chatBtn.setVisible(false);
            chatBtn.setManaged(false);
        }
        reportButton.setVisible(false);
        reportButton.setManaged(false);

        if (rateSellerBtn != null) {
            rateSellerBtn.setVisible(false);
            rateSellerBtn.setManaged(false);
        }
        showError(message);
    }

    @FXML
    private void showNext() {
        gallery.showNext();
    }

    @FXML
    private void showPrevious() {
        gallery.showPrevious();
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
        favoriteBtn.getStyleClass().remove("admin-topbar-btn-active");
        if (isFavoriteAdvertisement) {
            favoriteBtn.setText("حذف از علاقه‌مندی");
            favoriteBtn.getStyleClass().add("admin-topbar-btn-active");
        } else {
            favoriteBtn.setText("افزودن به علاقه‌مندی");
        }
    }

    @FXML
    private void reportAdvertisement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/report.fxml"));
            Parent root = loader.load();

            ReportDialogController controller = loader.getController();
            Stage reportWindow = new Stage();
            controller.setDialogStage(reportWindow);
            controller.setAdvertisementId(currentAdvertisement.getId());

            reportWindow.initModality(Modality.APPLICATION_MODAL);
            reportWindow.initStyle(StageStyle.TRANSPARENT);

            StackPane blackBackground = new StackPane(root);
            blackBackground.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-padding: 30px;");
            blackBackground.setOnMouseClicked(e -> {
                if (e.getTarget() == blackBackground) reportWindow.close();
            });

            Scene scene = new Scene(blackBackground);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(java.util.Objects.requireNonNull(
                    getClass().getResource("/org/example/divar/css/style.css")).toExternalForm());
            reportWindow.setScene(scene);
            reportWindow.show();

        } catch (IOException e) {
            System.err.println("Could not load the FXML file: " + e.getMessage());
        }
    }

    @FXML
    private void editAdvertisement() {
        SwitchStage.switchToEditAd(currentAdvertisement);
    }

    @FXML
    private void deleteAdvertisement() {
        deleteBtn.setDisable(true);
        try {
            AppContext.getAdvertisementService().deleteAdvertisement(currentAdvertisement.getId());

            showSuccess("آگهی شما با موفقیت حذف شد.");

            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(this::goBack);
            }).start();

        } catch (RuntimeException e) {
            showError(e.getMessage());
            deleteBtn.setDisable(false);
        }
    }

    @FXML
    private void rateSeller() {
        if (currentAdvertisement == null || currentAdvertisement.getSeller() == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/seller_rating.fxml"));
            Parent root = loader.load();

            SellerRatingDialog controller = loader.getController();

            String rawId = currentAdvertisement.getSeller().getId();
            Long sellerId = (rawId != null && !rawId.isBlank()) ? Long.parseLong(rawId) : null;

            controller.setSellerData(sellerId);

            Stage ratingWindow = new Stage();
            ratingWindow.initModality(Modality.APPLICATION_MODAL);
            ratingWindow.initStyle(StageStyle.TRANSPARENT);

            StackPane blackBackground = new StackPane(root);
            blackBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

            blackBackground.setOnMouseClicked(e -> {
                if (e.getTarget() == blackBackground) {
                    ratingWindow.close();
                }
            });

            Scene scene = new Scene(blackBackground);
            scene.setFill(Color.TRANSPARENT);

            ratingWindow.setScene(scene);
            ratingWindow.sizeToScene();
            ratingWindow.showAndWait();

            showAdvertisement(currentAdvertisement);

        } catch (NumberFormatException e) {
            System.err.println("Invalid seller ID format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Could not load rating FXML file: " + e.getMessage());
        }
    }


    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("success-message");
        messageLabel.setVisible(true);
    }

    @FXML
    private void goToProfile() {
        SwitchStage.switchToProfile();
    }

    @FXML
    private void goBack() {
        SwitchStage.goBack();
    }

    @FXML
    private void goToChat() {
        SwitchStage.switchToChat();
    }

    @FXML
    private void chatWithSeller() {
        clearMessage();

        String myUsername = SessionManager.getCurrentUsername();
        if (myUsername == null || myUsername.isBlank()) {
            showError("برای گفتگو ابتدا باید وارد حساب کاربری شوید.");
            return;
        }

        if (currentAdvertisement == null) {
            showError("اطلاعات آگهی در دسترس نیست.");
            return;
        }

        if (currentAdvertisement.getSeller() == null || currentAdvertisement.getSeller().getUsername() == null) {
            showError("اطلاعات فروشنده در دسترس نیست.");
            return;
        }

        String sellerUsername = currentAdvertisement.getSeller().getUsername();

        if (myUsername.equals(sellerUsername)) {
            showError("شما نمی‌توانید برای آگهی خودتان گفت‌وگو شروع کنید.");
            return;
        }

        chatBtn.setDisable(true);

        try {
            System.out.println("Inisde chat with seller" + myUsername + " " + sellerUsername);
            Conversation conversation = AppContext.getConversationService()
                    .findOrCreateConversation(currentAdvertisement, myUsername, sellerUsername);

            if (conversation == null) {
                throw new IllegalStateException("conversation is null");
            }

            if (conversation.getBuyerId() == null || conversation.getSellerId() == null) {
                throw new IllegalStateException("شناسه‌های گفتگو معتبر نیستند.");
            }

            SwitchStage.switchToChat(conversation);
        } catch (RuntimeException exception) {
            showError(exception.getMessage() != null ? exception.getMessage() : "خطا در باز کردن گفتگو");
        } finally {
            chatBtn.setDisable(false);
        }
    }


    private void disableActions() {
        if (chatBtn != null) chatBtn.setDisable(true);
        if (favoriteBtn != null) favoriteBtn.setDisable(true);
        if (editBtn != null) editBtn.setDisable(true);
        if (deleteBtn != null) deleteBtn.setDisable(true);
        if (reportButton != null) reportButton.setDisable(true);
        if (rateSellerBtn != null) rateSellerBtn.setDisable(true);
    }

    private void clearMessage() {
        if (messageLabel != null) {
            messageLabel.setVisible(false);
            messageLabel.setText("");
        }
    }

    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setVisible(true);
            messageLabel.setText(message);
        }
    }
}



