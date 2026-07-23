package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.divar.SwitchStage;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;
import org.example.divar.util.ImageLoader;

public class AdSummaryCard extends VBox {

    @FXML private ImageView cardImage;
    @FXML private Label cardTitle;
    @FXML private Label cardPrice;
    @FXML private Label cardLocation;

    public AdSummaryCard(Advertisement advertisement, boolean isAdminContext) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/ad_summary_card.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            String rawUrl = advertisement.getPrimaryImageUrl();
            if (rawUrl != null && !rawUrl.isBlank()) {
                cardImage.setImage(ImageLoader.loadMainImageFromUrl(rawUrl));
            } else {
                try {
                    cardImage.setImage(new Image(getClass().getResourceAsStream("/assets/placeholder.png")));
                } catch (Exception e) {
                    cardImage.setImage(ImageLoader.loadDefault());
                }
            }

            cardTitle.setText(advertisement.getTitle());
            cardPrice.setText(String.format("%,d", advertisement.getPrice()) + " تومان");

            String city = "نامشخص";
            if (advertisement.getCity() != null) {
                city = advertisement.getCity().toString();
            }
            cardLocation.setText(city);

            this.setOnMouseClicked(e -> {
                if (isAdminContext) {
                    Advertisement full = AppContext.getAdvertisementService().getAdvertisementById(advertisement.getId());
                    SwitchStage.switchToAdminAdDetails(full);
                } else {
                    SwitchStage.switchToAdDetails(advertisement);
                }
            });

        } catch (Exception e) {
            System.err.println("Error creating ad summary card: " + e.getMessage());
            e.printStackTrace();
        }
    }
}