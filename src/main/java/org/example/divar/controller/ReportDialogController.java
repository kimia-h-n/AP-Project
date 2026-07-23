package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.divar.model.ReportReason;
import org.example.divar.util.AppContext;
import org.example.divar.util.HandleErrors;

import java.io.IOException;

public class ReportDialogController {

    @FXML private RadioButton reasonFraud, reasonImmoral, reasonWrongCategory, reasonWrongPrice,
            reasonWrongInfo, reasonDuplicate, reasonUnavailable, reasonOthers;

    private Stage dialogStage;
    private long advertisementId;
    private AdvertisementDetailsController detailsController;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAdvertisementId(long advertisementId) {
        this.advertisementId = advertisementId;
    }

    public void setDetailsController(AdvertisementDetailsController detailsController) {
        this.detailsController = detailsController;
    }

    @FXML
    private void sendReport() {
        ReportReason selectedReason = getSelectedReportReason();
        try {
            AppContext.getAdvertisementService().reportAdvertisement(advertisementId, selectedReason);
            closeReportWindow();
            showSuccessReportPopup();
        } catch (RuntimeException e) {
            String persianMessage = HandleErrors.getPersianMessage(e.getMessage(), e.getMessage(), 400);

            closeReportWindow();

            if (detailsController != null) {
                detailsController.showError(persianMessage);
            }
        }
    }

    @FXML
    private void closeReportWindow() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void showSuccessReportPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/success_report.fxml"));
            Parent root = loader.load();

            ReportDialogController controller = loader.getController();
            Stage successWindow = new Stage();
            controller.setDialogStage(successWindow);

            successWindow.initModality(Modality.APPLICATION_MODAL);
            successWindow.initStyle(StageStyle.TRANSPARENT);

            javafx.scene.layout.StackPane shadowBackground = new javafx.scene.layout.StackPane(root);
            shadowBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 15px;");
            shadowBackground.setOnMouseClicked(e -> {
                if (e.getTarget() == shadowBackground) successWindow.close();
            });

            Scene scene = new Scene(shadowBackground);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(java.util.Objects.requireNonNull(
                    getClass().getResource("/org/example/divar/css/style.css")).toExternalForm());
            successWindow.setScene(scene);
            successWindow.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading success report FXML: " + e.getMessage());
        }
    }

    private ReportReason getSelectedReportReason() {
        if (reasonFraud.isSelected()) return ReportReason.FRAUD;
        if (reasonImmoral.isSelected()) return ReportReason.IMMORAL;
        if (reasonWrongCategory.isSelected()) return ReportReason.WRONG_CATEGORY;
        if (reasonWrongPrice.isSelected()) return ReportReason.WRONG_PRICE;
        if (reasonWrongInfo.isSelected()) return ReportReason.WRONG_INFORMATION;
        if (reasonDuplicate.isSelected()) return ReportReason.DUPLICATE;
        if (reasonUnavailable.isSelected()) return ReportReason.UNAVAILABLE;
        return ReportReason.OTHERS;
    }

    @FXML
    private void closeSuccessWindow() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}