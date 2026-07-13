package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.divar.SwitchStage;
import org.example.divar.model.Advertisement;
import javafx.scene.image.Image;
import java.io.File;
import java.util.Objects;

public class AdvertisementCard extends VBox {

    @FXML private ImageView advertisementImage;
    @FXML private Label advertisementTitle;
    @FXML private Label advertisementPrice;

    public AdvertisementCard(Advertisement advertisement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/advertisement_card.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            if (advertisementTitle != null) {
                advertisementTitle.setText(advertisement.getTitle());
            }
            if (advertisementPrice != null) {
                String priceText = String.format("%,d", advertisement.getPrice()) + "تومان";
                advertisementPrice.setText(priceText);
            }
            if (advertisementImage != null) {
                advertisementImage.setImage(loadImage(advertisement));
            }

            this.setOnMouseClicked(e -> SwitchStage.switchToAdDetails(advertisement));

        } catch (Exception e) {
            System.err.println("خطا در ساخت کامپوننت کارت آگهی: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private Image loadImage(Advertisement ad) {
        if (ad.getImagePaths() != null && !ad.getImagePaths().isEmpty()) {
            try {
                File file = new File(ad.getImagePaths().get(0));
                if (file.exists()) return new Image(file.toURI().toString());
            } catch (Exception e) {
                System.out.println("خطا در بارگذاری عکس اصلی: " + e.getMessage());
            }
        }
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/divar/images/current.jpg")));
        } catch (Exception e) {
            return null;
        }
    }
}


