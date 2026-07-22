package org.example.divar.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.divar.SwitchStage;
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

    @FXML private RadioButton reasonFraud, reasonImmoral, reasonWrongCategory, reasonWrongPrice,
            reasonWrongInfo, reasonDuplicate, reasonUnavailable, reasonOthers;

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
    @FXML private Button chatBtn;
    @FXML private Button favoriteBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button reportButton;
    @FXML private Button rateSellerBtn;

    private Stage reportWindow;
    private Stage successWindow;
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

        titleLabel.setText(safe(freshAd.getTitle()));
        descriptionLabel.setText(safe(freshAd.getDescription()));
        categoryLabel.setText(freshAd.getCategory() != null ? freshAd.getCategory().toString() : "-");
        conditionLabel.setText(freshAd.getCondition() != null ? freshAd.getCondition().toString() : "-");
        priceLabel.setText(freshAd.getPrice() > 0 ? String.format("%,d", freshAd.getPrice()) + " تومان" : "-");
        cityLabel.setText(freshAd.getCity() != null ? freshAd.getCity().toString() : "-");
        createdAtLabel.setText(formatInstant(freshAd.getCreatedAt()));
        updatedAtLabel.setText(formatInstant(freshAd.getUpdatedAt()));
        addressLabel.setText(freshAd.getAddress() != null && !freshAd.getAddress().isBlank() ? freshAd.getAddress() : "-");
        sellerLabel.setText(freshAd.getSeller() != null ? safe(freshAd.getSeller().getFullName()) : "-");

        // ست کردن مستقیم امتیاز از دیتای آگهی (freshAd)
        if (lblSellerRating != null) {
            double avg = freshAd.getSellerRating(); // یا freshAd.getAverageRating() بسته به اسم فیلد توی Advertisement
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

        boolean isMine = false;
        if (currentUsername != null) {
            if (freshAd.getSeller() != null && currentUsername.equals(freshAd.getSeller().getUsername())) {
                isMine = true;
            } else {
                try {
                    var myAds = AppContext.getAdvertisementService().getMyAdvertisements();
                    isMine = myAds.stream().anyMatch(ad -> ad.getId() == freshAd.getId());
                } catch (Exception ignored) {}
            }
        }

        updateActionButtonsVisibility(isMine);

        gallery.setImages(freshAd.getImagePaths());
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    private String formatInstant(Instant instant) {
        if (instant == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER.format(instant);
    }

    private void updateActionButtonsVisibility(boolean isMine) {
        editBtn.setVisible(isMine);
        editBtn.setManaged(isMine);
        deleteBtn.setVisible(isMine);
        deleteBtn.setManaged(isMine);

        boolean showBuyerActions = !isMine;
        chatBtn.setVisible(showBuyerActions);
        chatBtn.setManaged(showBuyerActions);
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
        chatBtn.setVisible(false);
        chatBtn.setManaged(false);
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

        if (currentAdvertisement == null || currentAdvertisement.getSeller() == null) {
            return;
        }

        String sellerUsername = currentAdvertisement.getSeller().getUsername();

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
            messageLabel.getStyleClass().remove("success-message");
            if (!messageLabel.getStyleClass().contains("error-message")) {
                messageLabel.getStyleClass().add("error-message");
            }
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
        } else {
            System.err.println("Error: " + message);
        }
    }

    private void showSuccess(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.getStyleClass().remove("error-message");
            if (!messageLabel.getStyleClass().contains("success-message")) {
                messageLabel.getStyleClass().add("success-message");
            }
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
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
            loader.setController(this);
            Parent root = loader.load();

            reportWindow = new Stage();
            reportWindow.initModality(Modality.APPLICATION_MODAL);
            reportWindow.initStyle(StageStyle.TRANSPARENT);

            StackPane blackBackground = new StackPane(root);
            blackBackground.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-padding: 30px;");
            blackBackground.setOnMouseClicked(e -> {
                if (e.getTarget() == blackBackground) closeReportWindow();
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
    private void closeReportWindow() {
        if (reportWindow != null) {
            reportWindow.close();
        }
    }

    @FXML
    public void showSuccessReportPopup() {
        closeReportWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/success_report.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            successWindow = new Stage();
            successWindow.initModality(Modality.APPLICATION_MODAL);
            successWindow.initStyle(StageStyle.TRANSPARENT);

            StackPane shadowBackground = new StackPane(root);
            shadowBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 20px;");

            Scene scene = new Scene(shadowBackground);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(java.util.Objects.requireNonNull(
                    getClass().getResource("/org/example/divar/css/style.css")).toExternalForm());
            successWindow.setScene(scene);
            successWindow.showAndWait();

        } catch (IOException e) {
            System.err.println("خطا در باز کردن فایل FXML موفقیت: " + e.getMessage());
        }
    }

    @FXML
    public void sendReport() {
        ReportReason selectedReason = getSelectedReportReason();
        closeReportWindow();

        try {
            AppContext.getAdvertisementService().reportAdvertisement(currentAdvertisement.getId(), selectedReason);
            showSuccessReportPopup();
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private ReportReason getSelectedReportReason() {
        if (reasonFraud.isSelected()) return ReportReason.FRAUD;
        if (reasonImmoral.isSelected()) return ReportReason.IMMORAL;
        if (reasonWrongCategory.isSelected()) return ReportReason.WRONG_CATEGORY;
        if (reasonWrongPrice.isSelected()) return ReportReason.WRONG_PRICE;
        if (reasonWrongInfo.isSelected()) return ReportReason.WRONG_INFORMATION;
        if (reasonDuplicate.isSelected()) return ReportReason.DUPLICATE;
        if (reasonUnavailable.isSelected()) return ReportReason.UNAVAILABLE;
        return ReportReason.OTHERS;
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
            Long sellerId = Long.parseLong(currentAdvertisement.getSeller().getId());
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

            // رلوود کردن مجدد آگهی جهت آپدیت امتیاز
            showAdvertisement(currentAdvertisement);

        } catch (NumberFormatException e) {
            System.err.println("شناسه فروشنده معتبر نیست: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("خطا در بارگذاری FXML امتیازدهی: " + e.getMessage());
        }
    }

    @FXML private void goToProfile() {
        SwitchStage.switchToProfile();
    }

    @FXML private void goBack() {
        SwitchStage.goBack();
    }

    @FXML private void goToChat() {
        SwitchStage.switchToChat();
    }

    @FXML
    private void closeSuccessWindow() {
        if (successWindow != null) {
            successWindow.close();
        }
    }
}







