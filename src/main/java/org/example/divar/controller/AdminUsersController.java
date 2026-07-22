package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.divar.component.UserCard;
import org.example.divar.component.ReasonDialog;
import org.example.divar.model.User;
import org.example.divar.model.UserStatus;
import org.example.divar.util.AppContext;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersController {

    @FXML private FlowPane cardsContainer;
    @FXML private Label messageLabel;

    private List<User> allUsersList = new ArrayList<>();

    @FXML
    public void initialize() {
        loadUsersData();
    }

    private void loadUsersData() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);

        try {
            allUsersList = AppContext.getAdminService().getAllUsers();
            renderList(allUsersList);
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void renderList(List<User> users) {
        cardsContainer.getChildren().clear();

        if (users == null || users.isEmpty()) {
            Label noUserLabel = new Label("هیچ کاربری یافت نشد.");
            noUserLabel.getStyleClass().add("empty-user-label");
            cardsContainer.getChildren().add(noUserLabel);
            return;
        }

        for (User user : users) {
            UserCard userCard = new UserCard(
                    user,
                    () -> handleUserAction(user),
                    () -> handleDetailsClick(user),
                    () -> handleAdsClick(user)
            );
            cardsContainer.getChildren().add(userCard);
        }
    }

    private void handleUserAction(User user) {
        if (user.getStatus() == UserStatus.BANNED) {
            unblockUser(user);
        } else {
            blockUser(user);
        }
    }

    private void blockUser(User user) {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);

        String title = "دلیل مسدود کردن کاربر";
        String subtitle = "دلیل مسدود کردن «" + user.getFirstname() + " " + user.getLastname() + "» را وارد کنید.";

        String reason = ReasonDialog.show("/org/example/divar/fxml/reason_dialog.fxml", title, subtitle, true);

        if (reason == null) {
            return;
        }
        if (reason.isEmpty()) {
            showError("برای مسدود کردن کاربر باید دلیل را وارد کنید.");
            return;
        }

        try {
            AppContext.getAdminService().blockUser(user.getId(), reason);
            user.setStatus(UserStatus.BANNED);

            loadUsersData();
            showSuccess("کاربر با موفقیت مسدود شد.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void unblockUser(User user) {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);

        String title = "فعال‌سازی کاربر";
        String subtitle = "آیا از فعال‌سازی «" + user.getFirstname() + " " + user.getLastname() + "» مطمئن هستید؟";

        String result = ReasonDialog.show("/org/example/divar/fxml/reason_dialog.fxml", title, subtitle, false);

        if (result != null) {
            try {
                AppContext.getAdminService().unblockUser(user.getId());
                user.setStatus(UserStatus.ACTIVE);

                loadUsersData();
                showSuccess("کاربر با موفقیت فعال شد.");
            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        }
    }

    private void handleDetailsClick(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/user_details_dialog.fxml"));
            Parent root = loader.load();
            UserDetailsDialogController controller = loader.getController();
            controller.loadUserData(Long.parseLong(user.getId()));
            Stage stage = new Stage();
            stage.setTitle("جزئیات کاربر");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error opening user details window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAdsClick(User user) {
        try {
            User fullUser = user;
            if (user.getId() != null && !user.getId().isBlank()) {
                try {
                    long userId = Long.parseLong(user.getId());
                    fullUser = AppContext.getAdminService().getUserDetails(userId);
                } catch (Exception ignored) {
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/admin_user_ads.fxml"));
            Parent userAdsPage = loader.load();

            AdminUserAdsController controller = loader.getController();
            controller.setUserData(fullUser);

            StackPane contentArea = (StackPane) cardsContainer.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(userAdsPage);
            }
        } catch (Exception e) {
            System.err.println("Error opening user advertisements: " + e.getMessage());
            e.printStackTrace();
        }
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
}


