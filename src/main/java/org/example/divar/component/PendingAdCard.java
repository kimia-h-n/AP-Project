package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;

import java.util.Optional;

public class PendingAdCard extends VBox {

    @FXML private Label titleLabel;
    @FXML private Label infoLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label messageLabel;
    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;

    public PendingAdCard(Advertisement advertisement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/pending_ad_card.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            titleLabel.setText(advertisement.getTitle());

            String priceText = advertisement.getPrice() > 0 ? String.format("%,d", advertisement.getPrice()) + " تومان" : "توافقی";
            infoLabel.setText(
                    (advertisement.getCategory() != null ? advertisement.getCategory().toString() : "") + " | " +
                            (advertisement.getCity() != null ? advertisement.getCity().toString() : "") + " | " + priceText +
                            " | فروشنده: " + (advertisement.getSeller() != null ? advertisement.getSeller().getUsername() : "نامشخص")
            );

            descriptionLabel.setText(advertisement.getDescription() != null ? advertisement.getDescription() : "");

            approveBtn.setOnAction(e -> approve(advertisement));
            rejectBtn.setOnAction(e -> reject(advertisement));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void approve(Advertisement advertisement) {
        try {
            AppContext.getAdminService().approveAdvertisement(advertisement.getId());
            removeCardFromList();
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void reject(Advertisement advertisement) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("رد کردن آگهی");
        dialog.setHeaderText("دلیل رد آگهی" + advertisement.getTitle() + "» را وارد کنید");
        dialog.setContentText("دلیل:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                showError("برای رد کردن آگهی باید دلیل را وارد کنید.");
                return;
            }
            try {
                AppContext.getAdminService().rejectAdvertisement(advertisement.getId(), reason.trim());
                removeCardFromList();
            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        });
    }

    private void removeCardFromList() {
        if (getParent() != null && getParent() instanceof VBox) {
            ((VBox) getParent()).getChildren().remove(this);
        }
    }

    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setVisible(true);
        } else {
            System.out.println(message);
        }
    }
}
