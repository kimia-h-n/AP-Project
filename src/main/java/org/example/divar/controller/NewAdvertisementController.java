package org.example.divar.controller;

import org.example.divar.model.*;
import org.example.divar.model.City;
import org.example.divar.util.SessionManager;
import org.example.divar.util.AppContext;
import org.example.divar.util.ImageLoader;
import org.example.divar.util.CityFormatter; // <--- ایمپورت کلاس CityFormatter
import org.example.divar.validation.AdvertisementValidation;
import org.example.divar.SwitchStage;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewAdvertisementController {

    @FXML private Label pageTitleLabel;
    @FXML private Button submitButton;
    @FXML private ComboBox<Category> category;
    @FXML private ComboBox<ProductCondition> condition;
    @FXML private ComboBox<City> city;
    @FXML private TextField titleField;
    @FXML private TextField priceField;
    @FXML private TextField addressField;
    @FXML private TextArea descriptionArea;
    @FXML private HBox images;
    @FXML private Label imagePathLabel;
    @FXML private Label messageLabel;

    private static class ExistingImageRef {
        final String id;
        final String url;
        ExistingImageRef(String id, String url) {
            this.id = id;
            this.url = url;
        }
    }

    private final List<ExistingImageRef> existingImages = new ArrayList<>();
    private final List<String> imagesToDelete = new ArrayList<>();
    private final ArrayList<String> newLocalFiles = new ArrayList<>();

    private Long editingAdId = null;

    @FXML
    public void initialize() {
        category.getItems().setAll(Category.values());
        condition.getItems().setAll(ProductCondition.values());

        try {
            ArrayList<City> serverCities = AppContext.getAdvertisementService().getAllProvinces();
            city.getItems().setAll(serverCities);

            if (!serverCities.isEmpty()) {
                city.setValue(serverCities.get(0));
            }
        } catch (Exception e) {
            showError("امکان دریافت لیست شهرها از سرور وجود ندارد.");
            System.err.println("Error loading cities: " + e.getMessage());
        }

        try {
            ArrayList<City> serverCities = AppContext.getAdvertisementService().getAllProvinces();
            city.getItems().setAll(serverCities);
        } catch (Exception e) {
            showError("امکان دریافت لیست شهرها از سرور وجود ندارد.");
            System.err.println("Error loading cities: " + e.getMessage());
        }
    }

    public void initializeForEdit(Advertisement advertisement) {
        this.editingAdId = advertisement.getId();

        pageTitleLabel.setText("ویرایش آگهی");
        submitButton.setText("ذخیره تغییرات");

        titleField.setText(advertisement.getTitle());
        descriptionArea.setText(advertisement.getDescription());
        addressField.setText(advertisement.getAddress());
        priceField.setText(String.valueOf(advertisement.getPrice()));
        category.setValue(advertisement.getCategory());
        condition.setValue(advertisement.getCondition());
        city.setValue(advertisement.getCity());

        existingImages.clear();
        imagesToDelete.clear();
        newLocalFiles.clear();
        images.getChildren().clear();

        ArrayList<String> urls = advertisement.getImagePaths();
        ArrayList<String> ids = advertisement.getImageIds();
        if (urls != null) {
            for (int i = 0; i < urls.size(); i++) {
                String id = (ids != null && i < ids.size()) ? ids.get(i) : null;
                ExistingImageRef ref = new ExistingImageRef(id, urls.get(i));
                existingImages.add(ref);
                addExistingImageThumbnail(ref);
            }
        }
        updateImagePathLabel();
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب عکس آگهی");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                String path = file.getAbsolutePath();
                newLocalFiles.add(path);
                addNewLocalThumbnail(path);
            }
        }
    }

    private void addExistingImageThumbnail(ExistingImageRef ref) {
        Image image = ImageLoader.loadMainImageFromUrl(ref.url);
        if (image == null) {
            image = ImageLoader.loadDefault();
        }
        VBox imageRow = buildImageRow(image, () -> {
            existingImages.remove(ref);
            if (ref.id != null) {
                imagesToDelete.add(ref.id);
            }
        });
        images.getChildren().add(imageRow);
        updateImagePathLabel();
    }

    private void addNewLocalThumbnail(String path) {
        Image image = ImageLoader.loadFromPath(path);
        if (image == null) {
            image = ImageLoader.loadDefault();
        }
        VBox imageRow = buildImageRow(image, () -> newLocalFiles.remove(path));
        images.getChildren().add(imageRow);
        updateImagePathLabel();
    }

    private VBox buildImageRow(Image image, Runnable onDelete) {
        ImageView thumb = new ImageView(image);
        thumb.setFitWidth(100.0);
        thumb.setFitHeight(100.0);
        thumb.setPreserveRatio(true);

        Button deleteBtn = new Button("حذف عکس");
        deleteBtn.getStyleClass().add("delete-image-btn");

        VBox imageRow = new VBox(5);
        imageRow.setAlignment(Pos.CENTER);
        imageRow.getChildren().addAll(thumb, deleteBtn);

        deleteBtn.setOnAction(e -> {
            onDelete.run();
            images.getChildren().remove(imageRow);
            updateImagePathLabel();
        });

        return imageRow;
    }

    private void updateImagePathLabel() {
        int total = existingImages.size() + newLocalFiles.size();
        if (total == 0) {
            imagePathLabel.setText("عکسی انتخاب نشده");
        } else {
            imagePathLabel.setText(total + " عکس انتخاب شد");
        }
    }

    @FXML
    private void createAdvertisement() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String address = addressField.getText();
        String price = priceField.getText();

        if (messageLabel != null) {
            messageLabel.setVisible(false);
            messageLabel.setText("");
        }

        Category selectedCategory = category.getValue();
        ProductCondition selectedCondition = condition.getValue();
        City selectedCity = city.getValue();

        if (selectedCategory == null) {
            showError("لطفاً یک دسته‌بندی برای آگهی خود انتخاب کنید.");
            return;
        }

        if (selectedCondition == null) {
            showError("لطفاً وضعیت کالا را انتخاب کنید.");
            return;
        }

        if (selectedCity == null) {
            showError("لطفاً شهر خود را انتخاب کنید.");
            return;
        }

        try {
            AdvertisementValidation validation = AppContext.getAdvertisementValidation();
            validation.advertisementValidation(title, price, selectedCategory, selectedCity, selectedCondition, address);

            long lPrice = Long.parseLong(price.trim());

            if (editingAdId == null) {
                submitNewAdvertisement(title, description, address, lPrice, selectedCategory, selectedCondition, selectedCity);
            } else {
                submitEditedAdvertisement(title, description, address, lPrice, selectedCategory, selectedCondition, selectedCity);
            }

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void submitNewAdvertisement(String title, String description, String address, long price,
                                        Category category, ProductCondition condition, City city) {
        User currentUser = new User();
        currentUser.setUsername(SessionManager.getCurrentUsername());

        try {
            long newAdId = AppContext.getAdvertisementService().createAdvertisement(
                    title, description, address, price, category, condition, city, currentUser);

            if (!newLocalFiles.isEmpty()) {
                AppContext.getAdvertisementService().uploadAdvertisementImages(newAdId, newLocalFiles);
            }

            showSuccess("آگهی شما با موفقیت ثبت شد و در انتظار بررسی مدیر است.");
            goBackAfterDelay();

        } catch (Exception e) {
            showError("خطا در ثبت آگهی یا آپلود تصاویر: " + e.getMessage());
        }
    }

    private void submitEditedAdvertisement(String title, String description, String address, long price,
                                           Category category, ProductCondition condition, City city) {
        try {
            AppContext.getAdvertisementService().updateAdvertisement(
                    editingAdId, title, description, address, price, category, condition, city, null);

            applyImageChanges();

            showSuccess("آگهی شما با موفقیت ویرایش شد.");
            goBackAfterDelay();

        } catch (Exception e) {
            showError("خطا در ثبت تغییرات آگهی: " + e.getMessage());
        }
    }

    private void applyImageChanges() {
        var adService = AppContext.getAdvertisementService();

        int localIndex = 0;
        int deleteIndex = 0;

        while (deleteIndex < imagesToDelete.size() && localIndex < newLocalFiles.size()) {
            String imageIdToReplace = imagesToDelete.get(deleteIndex);
            String newFilePath = newLocalFiles.get(localIndex);
            try {
                adService.replaceAdvertisementImage(imageIdToReplace, newFilePath);
            } catch (RuntimeException e) {
                System.err.println("Error REPLACE: " + e.getMessage());
            }
            deleteIndex++;
            localIndex++;
        }

        while (deleteIndex < imagesToDelete.size()) {
            String imageIdToDelete = imagesToDelete.get(deleteIndex);
            System.out.println("Executing DELETE for imageId: " + imageIdToDelete);
            try {
                adService.deleteAdvertisementImage(imageIdToDelete);
            } catch (RuntimeException e) {
                System.err.println("Error DELETE: " + e.getMessage());
            }
            deleteIndex++;
        }

        if (localIndex < newLocalFiles.size()) {
            ArrayList<String> remainingFiles = new ArrayList<>(newLocalFiles.subList(localIndex, newLocalFiles.size()));
            try {
                adService.uploadAdvertisementImages(editingAdId, remainingFiles);
            } catch (RuntimeException e) {
                System.err.println("Error POST Upload: " + e.getMessage());
            }
        }
    }

    private void goBackAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                goBack();
            });
        }).start();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("error-message");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("success-message");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    @FXML
    private void goBack() {
        SwitchStage.goBack();
    }

    @FXML
    private void goToProfile() {
        SwitchStage.switchToProfile();
    }

    @FXML
    private void goToChat() {
        SwitchStage.switchToChat();
    }
}




