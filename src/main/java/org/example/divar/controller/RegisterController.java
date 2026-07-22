package org.example.divar.controller;

import org.example.divar.util.AppContext;
import org.example.divar.SwitchStage;
import javafx.fxml.FXML;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    @FXML
    private void register() {
        String firstname = firstnameField.getText().trim();
        String lastname = lastnameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();

        messageLabel.setVisible(false);
        messageLabel.setText("");

        try {

            AppContext.getUserValidation().registerValidation(firstname, lastname, username, password, phoneNumber, email);
            AppContext.getUserService().register(firstname, lastname, username, password, phoneNumber, email);

            showSuccess("ثبت‌نام با موفقیت انجام شد!");

            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            // Wait 3 seconds then switch to login screen
            delay.setOnFinished(e -> SwitchStage.showLogin());
            delay.play();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        SwitchStage.showLogin();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("error-message");
        messageLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("success-message");
        messageLabel.setVisible(true);
    }
}



