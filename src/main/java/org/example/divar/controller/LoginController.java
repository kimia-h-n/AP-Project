package org.example.divar.controller;

import org.example.divar.model.User;
import org.example.divar.model.UserRole;
import org.example.divar.util.AppContext;
import org.example.divar.util.SessionManager;
import org.example.divar.SwitchStage;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        messageLabel.setVisible(false);
        messageLabel.setText("");

        try {

            AppContext.getUserValidation().loginValidation(username, password);
            AppContext.getUserService().login(username, password);

            UserRole role = SessionManager.getRole();
            if (role == null) {
                User currentUser = AppContext.getUserService().getUserProfile(username);
                role = currentUser.getRole();
                SessionManager.setRole(role);
            }

            if (role == UserRole.ADMIN) {
                SwitchStage.switchToAdminPanel();
            } else {
                SwitchStage.switchToHome();
            }

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        SwitchStage.switchToRegister();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("error-message");
        messageLabel.setVisible(true);
    }
}









