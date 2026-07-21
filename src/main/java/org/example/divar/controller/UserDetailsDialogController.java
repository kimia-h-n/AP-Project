package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.divar.model.User;
import org.example.divar.util.AppContext;

public class UserDetailsDialogController {

    @FXML private Label lblFullName;
    @FXML private Label lblUsername;
    @FXML private Label lblStatus;
    @FXML private Label lblPhone;
    @FXML private Label lblEmail;
    @FXML private Label lblRating;

    public void loadUserData(long userId) {
        new Thread(() -> {
            try {
                User user = AppContext.getAdminService().getUserDetails(userId);

                javafx.application.Platform.runLater(() -> {
                    lblFullName.setText(user.getFirstname() + " " + user.getLastname());
                    lblUsername.setText("@" + (user.getUsername() != null ? user.getUsername() : "پیدا نشد"));
                    lblPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "-");
                    lblEmail.setText(user.getEmail() != null ? user.getEmail() : "-");

                    double rating = user.getAverageRating();
                    lblRating.setText(rating > 0 ? String.format("%.1f از ۵", rating) : "بدون امتیاز");

                    if (user.getStatus() != null && user.getStatus() != org.example.divar.model.UserStatus.BANNED) {
                        lblStatus.setText("فعال");
                        lblStatus.setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-padding: 4 8; -fx-background-radius: 6;");
                    } else {
                        lblStatus.setText("مسدود شده");
                        lblStatus.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 4 8; -fx-background-radius: 6;");
                    }
                });
            } catch (Exception e) {
                System.err.println("خطا در دریافت جزئیات کاربر: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblFullName.getScene().getWindow();
        stage.close();
    }
}