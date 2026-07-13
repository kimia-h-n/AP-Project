package org.example.divar;

import org.example.divar.controller.*;
import org.example.divar.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.FileNotFoundException;


import java.io.IOException;
import java.util.Objects;

public class SwitchStage {

    private static javafx.stage.Stage stage;

    public static void setStage(javafx.stage.Stage stage) {
        SwitchStage.stage = stage;
    }

    public static void goBack() {
        switchToHome();
    }

    public static void showLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(SwitchStage.class.getResource("/org/example/divar/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (FileNotFoundException e) {
            System.out.println("FXML file not found!");
            e.printStackTrace();

        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Check your resources");
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/home.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(SwitchStage.class.getResource("/org/example/divar/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Check your resources");
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToNewAd() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/newAdvertisement.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Check your resources");
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToChat() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/chat.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToAdDetails(Advertisement ad) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/advertisement_details.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            AdvertisementDetailsController controller = fxmlLoader.getController();
            controller.showAdvertisement(ad);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToRegister() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/register.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToProfile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/profile.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToChat(Conversation conversation) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/chat.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
            ChatController controller = fxmlLoader.getController();
            controller.openConversation(conversation);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchToAdminPanel() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SwitchStage.class.getResource("/org/example/divar/fxml/admin_panel.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(SwitchStage.class.getResource("/org/example/divar/css/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Could not load the FXML file.");
            e.printStackTrace();

        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



