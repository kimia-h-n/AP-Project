package org.example.divar.util;

import javafx.scene.image.Image;
import org.example.divar.model.Advertisement;

import java.io.File;
import java.util.Objects;

public class ImageLoader {

    private static final String DEFAULT_IMAGE_PATH = "/org/example/divar/images/current.jpg";

    public static Image loadMainImage(Advertisement ad) {
        if (ad.getImagePaths() != null && !ad.getImagePaths().isEmpty()) {
            Image image = loadFromPath(ad.getImagePaths().get(0));
            if (image != null) return image;
        }
        return loadDefault();
    }

    public static Image loadFromPath(String path) {
        try {
            if (path != null) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    return new Image(file.toURI().toString());
                }
            }
        } catch (Exception e) {
            System.out.println("خطا در بارگذاری عکس: " + e.getMessage());
        }
        return null;
    }

    public static Image loadDefault() {
        try {
            return new Image(Objects.requireNonNull(
                    ImageLoader.class.getResourceAsStream(DEFAULT_IMAGE_PATH)));
        } catch (Exception e) {
            return null;
        }
    }
}