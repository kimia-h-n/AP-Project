package org.example.divar.controller;

import org.example.divar.model.*;
import org.example.divar.util.SessionManager;
import org.example.divar.util.AppContext;
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

    private final ArrayList<String> imagePaths = new ArrayList<>();

    @FXML
    public void initialize() {
        category.getItems().setAll(Category.values());
        condition.getItems().setAll(ProductCondition.values());
        city.getItems().setAll(City.values());
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
                String absolutePath = file.getAbsolutePath();
                imagePaths.add(absolutePath);

                Image image = new Image(file.toURI().toString());
                ImageView thumb = new ImageView(image);
                thumb.setFitWidth(100.0);
                thumb.setFitHeight(100.0);
                thumb.setPreserveRatio(true);

                Button deleteBtn = new Button("حذف عکس");
                deleteBtn.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

                VBox imageRow = new VBox(5);
                imageRow.setAlignment(Pos.CENTER);
                imageRow.getChildren().addAll(thumb, deleteBtn);

                deleteBtn.setOnAction(e -> {
                    imagePaths.remove(absolutePath);
                    images.getChildren().remove(imageRow);

                    if (imagePaths.isEmpty()) {
                        imagePathLabel.setText("عکسی انتخاب نشده");
                    } else {
                        imagePathLabel.setText(imagePaths.size() + " عکس");
                    }
                });

                images.getChildren().add(imageRow);
            }

            imagePathLabel.setText(imagePaths.size() + " عکس انتخاب شد");
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

        Object categoryValue = category.getValue();
        Object conditionValue = condition.getValue();
        Object cityValue = city.getValue();

        if (categoryValue == null) {
            showError("لطفاً یک دسته‌بندی برای آگهی خود انتخاب کنید.");
            return;
        }

        if (conditionValue == null) {
            showError("لطفاً وضعیت کالا را انتخاب کنید.");
            return;
        }

        if (cityValue == null) {
            showError("لطفاً شهر خود را انتخاب کنید.");
            return;
        }

        try {
            Category selectedCategory = Category.fromString(categoryValue.toString());
            ProductCondition selectedCondition = ProductCondition.fromString(conditionValue.toString());
            City selectedCity = City.fromString(cityValue.toString());

            AdvertisementValidation validation = AppContext.getAdvertisementValidation();
            validation.advertisementValidation(title, price, selectedCategory, selectedCity, selectedCondition, address); //

            long lPrice = Long.parseLong(price.trim());

            User currentUser = new User();
            currentUser.setUsername(SessionManager.getCurrentUsername());

            AppContext.getAdvertisementService().createAdvertisement(
                    title, description, address, lPrice,
                    selectedCategory, selectedCondition, selectedCity,
                    imagePaths, currentUser
            );

            showSuccess("آگهی شما با موفقیت ثبت شد و در انتظار بررسی مدیر است.");

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

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
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

    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.getStyleClass().remove("success-message");
            if (!messageLabel.getStyleClass().contains("error-message")) {
                messageLabel.getStyleClass().add("error-message");
            }
            messageLabel.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }

    private void showSuccess(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.getStyleClass().remove("error-message");
            if (!messageLabel.getStyleClass().contains("success-message")) {
                messageLabel.getStyleClass().add("success-message");
            }
            messageLabel.setVisible(true);
        } else {
            System.out.println(message);
        }
    }
}
