package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Objects;

public class ReasonDialog {

    @FXML private Label dialogTitle;
    @FXML private Label dialogSubtitle;
    @FXML private Button closeBtn;
    @FXML private Button cancelBtn;
    @FXML private Button submitBtn;
    @FXML private TextArea reasonArea;

    private Stage dialogStage;
    private String reason = null;

    @FXML
    public void initialize() {
        closeBtn.setOnAction(e -> dialogStage.close());
        cancelBtn.setOnAction(e -> dialogStage.close());
        submitBtn.setOnAction(e -> {
            reason = reasonArea.getText().trim();
            dialogStage.close();
        });
    }

    public static String show(String fxmlPath, String title, String subtitle, boolean showTextArea) {
        try {
            FXMLLoader loader = new FXMLLoader(ReasonDialog.class.getResource(fxmlPath));
            VBox root = loader.load();

            ReasonDialog controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);

            controller.dialogStage = stage;
            controller.dialogTitle.setText(title);
            controller.dialogSubtitle.setText(subtitle);

            stage.setHeight(320);
            if (!showTextArea) {
                controller.reasonArea.setVisible(false);
                controller.reasonArea.setManaged(false);
                stage.setHeight(200);
            }

            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.15));
            shadow.setRadius(20);
            root.setEffect(shadow);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(Objects.requireNonNull(
                    ReasonDialog.class.getResource("/org/example/divar/css/style.css")).toExternalForm());

            stage.setScene(scene);
            stage.setWidth(400);

            stage.showAndWait();
            return controller.reason;

        } catch (IOException e) {
            System.err.println("Error showing reason dialog: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
