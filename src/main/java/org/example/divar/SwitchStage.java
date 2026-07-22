package org.example.divar;

import javafx.scene.Parent;
import org.example.divar.controller.*;
import org.example.divar.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import java.util.Objects;
import java.util.Stack;

public class SwitchStage {

    private static javafx.stage.Stage stage;
    private static final Stack<Runnable> history = new Stack<>();
    private static Runnable currentScreen = null;

    public static void setStage(javafx.stage.Stage stage) {
        SwitchStage.stage = stage;
    }

    private static void navigate(Runnable screenLoader) {
        if (currentScreen != null) {
            history.push(currentScreen);
        }
        currentScreen = screenLoader;
        screenLoader.run();
    }

    public static void goBack() {
        if (!history.isEmpty()) {
            Runnable previous = history.pop();
            currentScreen = previous;
            previous.run();
        } else {
            switchToHome();
        }
    }

    public static void showLogin() {
        history.clear();
        currentScreen = null;
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/login.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(SwitchStage.class.getResource("/org/example/divar/css/style.css").toExternalForm());
                stage.setScene(scene);
                stage.setResizable(true);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToHome() {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/home.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(SwitchStage.class.getResource("/org/example/divar/css/style.css").toExternalForm());
                stage.setScene(scene);
                stage.setResizable(true);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToNewAd() {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/newAdvertisement.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                stage.setScene(scene);
                stage.setResizable(true);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToEditAd(Advertisement ad) {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/newAdvertisement.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                NewAdvertisementController controller = fxmlLoader.getController();
                controller.initializeForEdit(ad);
                stage.setScene(scene);
                stage.setResizable(true);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToChat() {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/chat.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToChat(Conversation conversation) {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/chat.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                ChatController controller = fxmlLoader.getController();
                controller.openConversation(conversation);
                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToAdDetails(Advertisement ad) {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/advertisement_details.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                AdvertisementDetailsController controller = fxmlLoader.getController();
                controller.showAdvertisement(ad);
                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToRegister() {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/register.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                stage.setScene(scene);
                stage.setResizable(true);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToProfile() {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/profile.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToAdminAdDetails(Advertisement ad) {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/admin_ad_details.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                AdminAdDetailsController controller = fxmlLoader.getController();
                controller.showAdvertisement(ad);
                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void switchToAdminPanel() {
        navigate(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/admin_panel.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
                stage.setScene(scene);
                stage.setResizable(true);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}











