package org.example.divar.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.divar.SwitchStage;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;
import org.example.divar.util.ImageLoader;

public class FavoriteAdCard extends VBox {

    @FXML private ImageView adImage;
    @FXML private Label titleLabel;
    @FXML private Label infoLabel;
    @FXML private Label errorLabel;
    @FXML private Button removeBtn;

    public interface OnRemoved {
        void onRemoved(Advertisement ad);
    }

    public FavoriteAdCard(Advertisement advertisement, OnRemoved onRemoved) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/favorite_ad_card.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            titleLabel.setText(advertisement.getTitle());

            String priceText = advertisement.getPrice() > 0
                    ? String.format("%,d", advertisement.getPrice()) + " تومان" : "توافقی";
            infoLabel.setText(
                    (advertisement.getCity() != null ? advertisement.getCity().toString() : "") + " | " + priceText
            );

            adImage.setImage(ImageLoader.loadMainImage(advertisement));

            this.setOnMouseClicked(e -> SwitchStage.switchToAdDetails(advertisement));

            removeBtn.setOnMouseClicked(Event::consume);
            removeBtn.setOnAction(e -> {
                try {
                    AppContext.getAdvertisementService().removeFromFavorites(advertisement.getId());
                    if (onRemoved != null) {
                        onRemoved.onRemoved(advertisement);
                    }
                } catch (RuntimeException ex) {
                    showError(ex.getMessage());
                    removeBtn.setDisable(false);
                }
            });

        } catch (Exception e) {
            System.err.println("خطا در ساخت کارت علاقه‌مندی: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            System.out.println(message);
        }
    }
}