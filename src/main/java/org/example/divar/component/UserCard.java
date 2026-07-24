package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.divar.model.User;
import org.example.divar.model.UserStatus;

/**
 * Custom UI component representing a user summary card in the admin panel
 * with controls for viewing user details, advertisements, and toggling block/unblock status.
 */
public class UserCard extends HBox {

    @FXML private Label nameLabel;
    @FXML private Label statusLabel;
    @FXML private Label phoneLabel;
    @FXML private Button detailsBtn;
    @FXML private Button adsBtn;
    @FXML private Button actionBtn;

    /**
     * Initializes and populates the user summary card component with user details, status badges, and action listeners.
     *
     * @param user           the user data object to display
     * @param onActionClick  callback runnable executed when the block/unblock action button is clicked
     * @param onDetailsClick callback runnable executed when the details view button is clicked
     * @param onAdsClick     callback runnable executed when the user advertisements button is clicked
     */
    public UserCard(User user, Runnable onActionClick, Runnable onDetailsClick, Runnable onAdsClick) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/user_summary_card.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            boolean isBanned = (user.getStatus() != null && user.getStatus() == UserStatus.BANNED);

            if (isBanned) {
                statusLabel.setText("مسدود شده");
                statusLabel.getStyleClass().removeAll("status-pill-active", "status-pill-blocked");
                statusLabel.getStyleClass().add("status-pill-blocked");

                actionBtn.setText("فعال‌سازی");
                actionBtn.getStyleClass().removeAll("btn-reject", "btn-approve");
                actionBtn.getStyleClass().add("btn-approve");
            } else {
                statusLabel.setText("فعال");
                statusLabel.getStyleClass().removeAll("status-pill-active", "status-pill-blocked");
                statusLabel.getStyleClass().add("status-pill-active");

                actionBtn.setText("مسدود کردن");
                actionBtn.getStyleClass().removeAll("btn-reject", "btn-approve");
                actionBtn.getStyleClass().add("btn-reject");
            }

            nameLabel.setText(user.getFullName());

            String phoneNumber = (user.getPhoneNumber() != null) ? user.getPhoneNumber() : "-";
            phoneLabel.setText("شماره تماس: " + phoneNumber);

            actionBtn.setOnAction(e -> {
                if (onActionClick != null) {
                    onActionClick.run();
                }
            });

            detailsBtn.setOnAction(e -> {
                if (onDetailsClick != null) {
                    onDetailsClick.run();
                }
            });

            adsBtn.setOnAction(e -> {
                if (onAdsClick != null) {
                    onAdsClick.run();
                }
            });

        } catch (Exception e) {
            System.err.println("Error creating user grid card: " + e.getMessage());
            e.printStackTrace();
        }
    }
}