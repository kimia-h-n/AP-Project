package org.example.divar.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.example.divar.SwitchStage;
import org.example.divar.component.ReasonDialog;
import org.example.divar.model.Advertisement;
import org.example.divar.util.AppContext;
import org.example.divar.util.ImageLoader;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class AdminDashboardController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm", Locale.ENGLISH)
                    .withZone(ZoneId.of("Asia/Tehran"));

    @FXML private TableView<Advertisement> pendingTable;
    @FXML private TableColumn<Advertisement, Advertisement> imageColumn;
    @FXML private TableColumn<Advertisement, String> titleColumn;
    @FXML private TableColumn<Advertisement, String> sellerColumn;
    @FXML private TableColumn<Advertisement, String> cityColumn;
    @FXML private TableColumn<Advertisement, String> createdAtColumn;
    @FXML private TableColumn<Advertisement, Advertisement> actionsColumn;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        setupColumns();
        loadPendingAds();
        pendingTable.setSelectionModel(null);
    }

    private void setupColumns() {

        imageColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        imageColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imgView = new ImageView();
            {
                imgView.setFitWidth(55);
                imgView.setFitHeight(45);
                imgView.setPreserveRatio(true);
                setAlignment(javafx.geometry.Pos.CENTER);
            }
            @Override
            protected void updateItem(Advertisement ad, boolean empty) {
                super.updateItem(ad, empty);
                if (empty || ad == null) {
                    setGraphic(null);
                } else {
                    imgView.setImage(ImageLoader.loadMainImageFromUrl(ad.getPrimaryImageUrl()));
                    setGraphic(imgView);
                }
            }
        });

        titleColumn.setCellValueFactory(data -> {
            Advertisement ad = data.getValue();
            String category = "";
            if (ad.getCategory() != null) {
                category = ad.getCategory().toString();
            }
            return new ReadOnlyObjectWrapper<>(ad.getTitle() + "\n" + category);
        });

        titleColumn.setCellFactory(col -> new TableCell<>() {
            { setAlignment(javafx.geometry.Pos.CENTER); }
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setWrapText(true);
                    setText(value);
                }
            }
        });

        sellerColumn.setCellValueFactory(data -> {
            Advertisement ad = data.getValue();
            String seller;
            if (ad.getSeller() != null) {
                seller = ad.getSeller().getUsername();
            } else {
                seller = "نامشخص";
            }
            return new ReadOnlyObjectWrapper<>(seller);
        });

        sellerColumn.setCellFactory(col -> new TableCell<>() {
            { setAlignment(javafx.geometry.Pos.CENTER); }
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value);
                }
            }
        });

        cityColumn.setCellValueFactory(data -> {
            Advertisement ad = data.getValue();
            String city;
            if (ad.getCity() != null && ad.getCity().getName() != null) {
                city = ad.getCity().getName();
            } else {
                city = "-";
            }
            return new ReadOnlyObjectWrapper<>(city);
        });

        cityColumn.setCellFactory(col -> new TableCell<>() {
            { setAlignment(javafx.geometry.Pos.CENTER); }
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value);
                }
            }
        });

        createdAtColumn.setCellValueFactory(data -> {
            Advertisement ad = data.getValue();
            String dateText = "-";
            if (ad.getCreatedAt() != null) {
                dateText = DATE_TIME_FORMATTER.format(ad.getCreatedAt());
            }
            return new ReadOnlyObjectWrapper<>(dateText);
        });
        createdAtColumn.setCellFactory(col -> new TableCell<>() {
            { setAlignment(javafx.geometry.Pos.CENTER); }
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value);
                }
            }
        });

        actionsColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("مشاهده");
            private final Button approveBtn = new Button("تایید");
            private final Button rejectBtn = new Button("رد");
            private final HBox box = new HBox(6.0, approveBtn, rejectBtn, viewBtn);
            {
                viewBtn.getStyleClass().add("btn-view");
                approveBtn.getStyleClass().add("btn-approve");
                rejectBtn.getStyleClass().add("btn-reject");
                box.setAlignment(javafx.geometry.Pos.CENTER);
                setAlignment(javafx.geometry.Pos.CENTER);
            }
            @Override
            protected void updateItem(Advertisement ad, boolean empty) {
                super.updateItem(ad, empty);
                if (empty || ad == null) {
                    setGraphic(null);
                } else {
                    viewBtn.setOnAction(e -> {
                        try {
                            Advertisement freshAd = AppContext.getAdvertisementService().getAdvertisementById(ad.getId());
                            SwitchStage.switchToAdminAdDetails(freshAd);
                        } catch (RuntimeException ex) {
                            showError("خطا در بارگذاری جزئیات کامل آگهی: " + ex.getMessage());
                        }
                    });
                    approveBtn.setOnAction(e -> approve(ad));
                    rejectBtn.setOnAction(e -> reject(ad));
                    setGraphic(box);
                }
            }
        });
    }

    private void loadPendingAds() {
        clearMessage();
        try {
            ArrayList<Advertisement> pendingAds = AppContext.getAdminService().getPendingAdvertisements();
            ObservableList<Advertisement> items = FXCollections.observableArrayList(pendingAds);
            pendingTable.setItems(items);
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void approve(Advertisement ad) {
        try {
            AppContext.getAdminService().approveAdvertisement(ad.getId());
            pendingTable.getItems().remove(ad);
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void reject(Advertisement ad) {
        String fxmlPath = "/org/example/divar/fxml/reason_dialog.fxml";
        String title = "رد آگهی";
        String subtitle = "لطفاً دلیل رد آگهی «" + ad.getTitle() + "» را وارد کنید.";

        String reason = ReasonDialog.show(fxmlPath, title, subtitle, true);

        if (reason == null || reason.isEmpty()) {
            showError("برای رد کردن آگهی باید دلیل را وارد کنید.");
            return;
        }

        try {
            AppContext.getAdminService().rejectAdvertisement(ad.getId(), reason);
            pendingTable.getItems().remove(ad);
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void refresh() {
        loadPendingAds();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("error-message");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void clearMessage() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}



