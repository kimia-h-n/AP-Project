package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.divar.controller.HandleReportDialogController;
import org.example.divar.model.AdminReport;
import org.example.divar.model.ReportReason;
import org.example.divar.util.ImageLoader;

public class ReportCard extends VBox {

    @FXML private ImageView cardImage;
    @FXML private Label cardTitle;
    @FXML private Label cardLocation;
    @FXML private Label reasonLabel;
    @FXML private Button handleBtn;

    public ReportCard(AdminReport report, Runnable onResolved) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/report_card.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            String rawUrl = report.getImageUrl();
            if (rawUrl != null && !rawUrl.isBlank()) {
                cardImage.setImage(ImageLoader.loadMainImageFromUrl(rawUrl));
            } else {
                cardImage.setImage(ImageLoader.loadDefault());
            }

            if (cardTitle != null) {
                cardTitle.setText(report.getAdTitle());
            }

            if (cardLocation != null) {
                cardLocation.setText("فروشنده: " + report.getSellerFullName());
            }

            if (reasonLabel != null) {
                ReportReason reasonEnum = ReportReason.fromString(report.getReason());
                reasonLabel.setText("علت گزارش: " + reasonEnum.getLabel());
                reasonLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #b71c1c; -fx-font-size: 11px;");
            }

            if (handleBtn != null) {
                handleBtn.setOnAction(e -> openHandleDialog(report, onResolved));
            }

        } catch (Exception e) {
            System.err.println("Error creating report card: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openHandleDialog(AdminReport report, Runnable onResolved) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/handle_report_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();

            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            HandleReportDialogController controller = loader.getController();
            controller.initData(dialogStage, report, onResolved);

            Scene scene = new Scene(root);
            scene.setFill(null);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error opening handle report dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}