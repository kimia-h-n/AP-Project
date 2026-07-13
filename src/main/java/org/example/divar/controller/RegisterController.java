package org.example.divar.controller;

import org.example.divar.util.AppContext;
import org.example.divar.SwitchStage;
import javafx.fxml.FXML;
import javafx.application.Platform;
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
        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();

        if (messageLabel != null) {
            messageLabel.setVisible(false);
            messageLabel.setText("");
        }

        try {

            AppContext.getUserValidation().registerValidation(firstname, lastname, username, password, phoneNumber, email);
            AppContext.getUserService().register(firstname, lastname, username, password, phoneNumber, email);

            showSuccess("ثبت‌نام با موفقیت انجام شد!");

            // در واقع در javaFX نباید ترد های فرعی پردازش کارهای گرافیکی رو انجام بدن
            // به خطر همین برای لود شدن صفحه لاگین باید از خود ترد javaFx استفاده کنیم
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    SwitchStage.showLogin();
                });
            }).start();

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



