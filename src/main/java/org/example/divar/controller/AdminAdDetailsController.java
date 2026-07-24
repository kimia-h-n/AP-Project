package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.example.divar.SwitchStage;
import org.example.divar.component.ImageGallery;
import org.example.divar.component.ReasonDialog;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller class for managing the admin view of advertisement details, image gallery, and administrative actions.
 */
public class AdminAdDetailsController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm", Locale.ENGLISH)
                    .withZone(ZoneId.of("Asia/Tehran"));

    @FXML private ImageView mainImage;
    @FXML private HBox thumbnailBox;
    @FXML private Label counterLabel;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label categoryLabel;
    @FXML private Label conditionLabel;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label updatedAtLabel;
    @FXML private Label sellerLabel;
    @FXML private Label messageLabel;

    private Advertisement advertisement;
    private ImageGallery gallery;

    @FXML
    public void initialize() {

        gallery = new ImageGallery(mainImage, thumbnailBox, counterLabel);
    }

    /**
     * Loads and populates the view components with the given advertisement details.
     *
     * @param advertisement the advertisement to display in the admin details view
     */
    public void showAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;

        if (advertisement.getTitle() != null) {
            titleLabel.setText(advertisement.getTitle());
        } else {
            titleLabel.setText(null);
        }

        if (advertisement.getDescription() != null) {
            descriptionLabel.setText(advertisement.getDescription());
        } else {
            descriptionLabel.setText("-");
        }

        if (advertisement.getCity() != null) {
            cityLabel.setText(advertisement.getCity().toString());
        } else {
            cityLabel.setText("-");
        }

        createdAtLabel.setText(formatInstant(advertisement.getCreatedAt()));

        updatedAtLabel.setText(formatInstant(advertisement.getUpdatedAt()));

        if (advertisement.getSeller() != null) {
            sellerLabel.setText(advertisement.getSeller().getFullName());
        } else {
            sellerLabel.setText("نامشخص");
        }
        if (advertisement.getCategory() != null) {
            categoryLabel.setText(advertisement.getCategory().toString());
        } else {
            categoryLabel.setText("-");
        }

        if (advertisement.getCondition() != null) {
            conditionLabel.setText(advertisement.getCondition().toString());
        } else {
            conditionLabel.setText("-");
        }


        String priceText = String.format("%,d", advertisement.getPrice()) + " تومان";
        priceLabel.setText(priceText);

        gallery.setImages(advertisement.getImagePaths(), advertisement.getImageIds());
    }

    private String formatInstant(Instant instant) {
        if (instant == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER.format(instant);
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
    private void deleteAdvertisement() {
        String fxmlPath = "/org/example/divar/fxml/delete_ad_dialog.fxml";
        String title = "حذف آگهی";
        String subtitle = "لطفاً دلیل حذف آگهی «" + advertisement.getTitle() + "» را وارد کنید.";

        String reason = ReasonDialog.show(fxmlPath, title, subtitle, true);

        if (reason == null || reason.isEmpty()) {
            showError("برای حذف آگهی باید دلیل را وارد کنید.");
            return;
        }

        try {
            AppContext.getAdvertisementService().deleteAdvertisement(advertisement.getId());
            SwitchStage.switchToAdminPanel();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void openChat() {
        SwitchStage.switchToChat();
    }

    @FXML
    private void goBack() {
        SwitchStage.goBack();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("error-message");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}





