package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.divar.model.User;
import org.example.divar.util.AppContext;

/**
 * Controller class for managing the user details dialog in the admin panel.
 */
public class UserDetailsDialogController {

    @FXML private Label lblFullName;
    @FXML private Label lblUsername;
    @FXML private Label lblStatus;
    @FXML private Label lblPhone;
    @FXML private Label lblEmail;
    @FXML private Label lblRating;

    /**
     * Fetches user details from the server asynchronously by user ID and populates the UI labels.
     *
     * @param userId the ID of the user whose details are to be loaded
     */
    public void loadUserData(long userId) {
        new Thread(() -> {
            try {
                User user = AppContext.getAdminService().getUserDetails(userId);

                javafx.application.Platform.runLater(() -> {
                    lblFullName.setText(user.getFirstname() + " " + user.getLastname());

                    if (user.getUsername() != null) {
                        lblUsername.setText("@" + user.getUsername());
                    } else {
                        lblUsername.setText("@پیدا نشد");
                    }

                    if (user.getPhoneNumber() != null) {
                        lblPhone.setText(user.getPhoneNumber());
                    } else {
                        lblPhone.setText("-");
                    }

                    if (user.getEmail() != null) {
                        lblEmail.setText(user.getEmail());
                    } else {
                        lblEmail.setText("-");
                    }

                    double rating = user.getAverageRating();
                    if (rating > 0) {
                        lblRating.setText(String.format("%.1f از ۵", rating));
                    } else {
                        lblRating.setText("بدون امتیاز");
                    }

                    if (user.getStatus() != null && user.getStatus() != org.example.divar.model.UserStatus.BANNED) {
                        lblStatus.setText("فعال");
                        lblStatus.getStyleClass().removeAll("status-banned");
                        if (!lblStatus.getStyleClass().contains("status-active")) {
                            lblStatus.getStyleClass().add("status-active");
                        }
                    } else {
                        lblStatus.setText("مسدود شده");
                        lblStatus.getStyleClass().removeAll("status-active");
                        if (!lblStatus.getStyleClass().contains("status-banned")) {
                            lblStatus.getStyleClass().add("status-banned");
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Error fetching user details: " + e.getMessage());
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