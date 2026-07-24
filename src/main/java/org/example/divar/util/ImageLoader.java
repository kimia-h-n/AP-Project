package org.example.divar.util;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Objects;

/**
 * Utility class for loading images from URLs or local file paths.
 */
public class ImageLoader {

    private static final String DEFAULT_IMAGE_PATH = "/org/example/divar/images/current.jpg";

    /**
     * Loads an image from the given URL.
     *
     * @param imageUrl URL of the image
     * @return Image object, or default image if loading fails
     */
    public static Image loadMainImageFromUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                byte[] imageBytes = ApiClient.getImageBytes(imageUrl);

                if (imageBytes != null && imageBytes.length > 0) {
                    return new Image(new ByteArrayInputStream(imageBytes));
                }
            } catch (Exception e) {
                System.err.println("Error converting image bytes: " + e.getMessage());
            }
        }
        return loadDefault();
    }

    /**
     * Loads the default fallback image.
     *
     * @return default Image object, or null if not found
     */
    public static Image loadDefault() {
        try {
            return new Image(Objects.requireNonNull(
                    ImageLoader.class.getResourceAsStream(DEFAULT_IMAGE_PATH)));
        } catch (Exception e) {
            System.err.println("Default image not found.");
            return null;
        }
    }

    /**
     * Loads an image from a local file path.
     *
     * @param path local file path
     * @return Image object, or null if file doesn't exist or can't be loaded
     */
    public static Image loadFromPath(String path) {
        try {
            if (path != null) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    return new Image(file.toURI().toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        return null;
    }
}

