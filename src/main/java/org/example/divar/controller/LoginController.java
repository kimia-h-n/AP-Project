package org.example.divar.controller;

import org.example.divar.util.AppContext;
import org.example.divar.SwitchStage;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (messageLabel != null) {
            messageLabel.setVisible(false);
            messageLabel.setText("");
        }

        try {
            AppContext.getUserValidation().loginValidation(username, password);
            AppContext.getUserService().login(username, password);

            SwitchStage.switchToHome();

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
        if (messageLabel != null) {
            messageLabel.setText(message);
            if (!messageLabel.getStyleClass().contains("error-message")) {
                messageLabel.getStyleClass().add("error-message");
            }
            messageLabel.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }
}


