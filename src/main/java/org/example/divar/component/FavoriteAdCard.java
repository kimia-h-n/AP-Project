package org.example.divar.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.divar.SwitchStage;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;
import org.example.divar.util.ImageLoader;

public class FavoriteAdCard extends VBox {

    @FXML private ImageView adImage;
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label errorLabel;
    @FXML private Button removeBtn;

    public FavoriteAdCard(Advertisement advertisement, Runnable onRemoved) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/favorite_ad_card.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            if (advertisement.getTitle() != null && !advertisement.getTitle().trim().isEmpty()) {
                titleLabel.setText(advertisement.getTitle());
            } else {
                titleLabel.setText("بدون عنوان");
            }

            priceLabel.setText("0");
            if (advertisement.getPrice() > 0) {
                priceLabel.setText(String.format("%,d", advertisement.getPrice()) + " تومان");
            }

            if (advertisement.getCity() != null && advertisement.getCity().getName() != null) {
                cityLabel.setText(advertisement.getCity().getName());
            } else {
                cityLabel.setText("نامشخص");
            }

            System.out.println(
                    "[FAVORITE] adId=" + advertisement.getId()
                            + ", primaryImageUrl=" + advertisement.getPrimaryImageUrl()
                            + ", imagePaths=" + advertisement.getImagePaths()
                            + ", imageIds=" + advertisement.getImageIds()
            );


            String rawUrl = advertisement.getPrimaryImageUrl();
            if (rawUrl != null && !rawUrl.isBlank()) {
                adImage.setImage(ImageLoader.loadMainImageFromUrl(rawUrl));
            } else {
                try {
                    adImage.setImage(new Image(getClass().getResourceAsStream("/assets/placeholder.png")));
                } catch (Exception e) {
                    adImage.setImage(ImageLoader.loadDefault());
                }
            }
            this.setOnMouseClicked(e -> SwitchStage.switchToAdDetails(advertisement));

            removeBtn.setOnMouseClicked(Event::consume);
            removeBtn.setOnAction(e -> {
                try {
                    AppContext.getAdvertisementService().removeFromFavorites(advertisement.getId());
                    if (onRemoved != null) {
                        onRemoved.run();
                    }
                } catch (RuntimeException ex) {
                    showError(ex.getMessage());
                    removeBtn.setDisable(false);
                }
            });

        } catch (Exception e) {
            System.err.println("Error creating favorite ad card: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }
}