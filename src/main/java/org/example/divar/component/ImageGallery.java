package org.example.divar.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.example.divar.util.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGallery {

    public interface ImageActionListener {
        void onDeleteRequested(String imageId, int index);
        void onAddRequested();
    }

    private final ImageView mainImage;
    private final HBox thumbnailBox;
    private final Label counterLabel;

    private final List<String> imageUrls = new ArrayList<>();
    private final List<String> imageIds = new ArrayList<>();

    private int currentIndex = 0;
    private boolean editable = false;
    private ImageActionListener listener;

    public ImageGallery(ImageView mainImage, HBox thumbnailBox, Label counterLabel) {
        this.mainImage = mainImage;
        this.thumbnailBox = thumbnailBox;
        this.counterLabel = counterLabel;
    }

    public void setEditable(boolean editable, ImageActionListener listener) {
        this.editable = editable;
        this.listener = listener;
        render();
    }

    public void setImages(List<String> urls) {
        setImages(urls, null);
    }

    public void setImages(List<String> urls, List<String> ids) {
        imageUrls.clear();
        imageIds.clear();

        if (urls != null) {
            imageUrls.addAll(urls);
        }

        for (int i = 0; i < imageUrls.size(); i++) {
            String id;
            if (ids != null && i < ids.size()) {
                id = ids.get(i);
            } else {
                id = null;
            }
            imageIds.add(id);
        }

        currentIndex = 0;
        render();
    }

    public void showNext() {
        if (imageUrls.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex + 1) % imageUrls.size();
        render();
    }

    public void showPrevious() {
        if (imageUrls.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex - 1 + imageUrls.size()) % imageUrls.size();
        render();
    }

    private void render() {
        thumbnailBox.getChildren().clear();

        if (imageUrls.isEmpty()) {
            mainImage.setImage(ImageLoader.loadDefault());

            if (counterLabel != null) {
                counterLabel.setText("");
            }

            if (editable) {
                thumbnailBox.getChildren().add(createAddButton());
            }

            return;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        if (currentIndex >= imageUrls.size()) {
            currentIndex = imageUrls.size() - 1;
        }

        String currentSource = imageUrls.get(currentIndex);
        mainImage.setImage(loadImage(currentSource));

        if (counterLabel != null) {
            counterLabel.setText((currentIndex + 1) + " / " + imageUrls.size());
        }

        for (int i = 0; i < imageUrls.size(); i++) {
            thumbnailBox.getChildren().add(createThumbnail(i));
        }

        if (editable) {
            thumbnailBox.getChildren().add(createAddButton());
        }
    }

    private StackPane createThumbnail(int index) {
        ImageView thumb = new ImageView();
        thumb.setFitWidth(60);
        thumb.setFitHeight(45);
        thumb.setPreserveRatio(true);

        String imageSource = imageUrls.get(index);
        thumb.setImage(loadImage(imageSource));

        if (index == currentIndex) {
            thumb.getStyleClass().add("thumbnail-selected");
        }

        thumb.setStyle("-fx-cursor: hand;");
        thumb.setOnMouseClicked(event -> {
            currentIndex = index;
            render();
        });

        StackPane wrapper = new StackPane(thumb);
        StackPane.setAlignment(thumb, Pos.CENTER);

        if (editable) {
            Button deleteBtn = new Button("×");
            deleteBtn.getStyleClass().add("image-gallery-delete-btn");

            deleteBtn.setOnAction(event -> {
                event.consume();
                if (listener != null) {
                    listener.onDeleteRequested(imageIds.get(index), index);
                }
            });

            StackPane.setAlignment(deleteBtn, Pos.TOP_RIGHT);
            wrapper.getChildren().add(deleteBtn);
        }

        return wrapper;
    }

    private Button createAddButton() {
        Button addBtn = new Button("+");
        addBtn.setPrefSize(60, 45);
        addBtn.getStyleClass().add("image-gallery-add-btn");

        addBtn.setOnAction(event -> {
            if (listener != null) {
                listener.onAddRequested();
            }
        });

        return addBtn;
    }

    private Image loadImage(String source) {
        if (source == null || source.isBlank()) {
            return ImageLoader.loadDefault();
        }

        try {
            File localFile = new File(source);

            if (localFile.exists() && localFile.isFile()) {
                Image localImage = ImageLoader.loadFromPath(source);
                if (localImage != null) {
                    return localImage;
                } else {
                    return ImageLoader.loadDefault();
                }
            } else {
                Image serverImage = ImageLoader.loadMainImageFromUrl(source);
                if (serverImage != null) {
                    return serverImage;
                } else {
                    return ImageLoader.loadDefault();
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading image " + source + ": " + e.getMessage());
            return ImageLoader.loadDefault();
        }
    }
}

