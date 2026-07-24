package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.divar.model.AdminReport;
import org.example.divar.model.ReportResolutionAction;
import org.example.divar.util.AppContext;

/**
 * Controller class for managing the admin report resolution dialog, allowing admins to ban users or delete reported ads.
 */
public class HandleReportDialogController {

    @FXML private RadioButton deleteAdRadio;
    @FXML private RadioButton banUserRadio;
    @FXML private TextArea reasonTextArea;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    private ToggleGroup actionGroup;
    private Stage stage;
    private AdminReport report;
    private Runnable onSuccessCallback;

    @FXML
    public void initialize() {
        actionGroup = new ToggleGroup();
        deleteAdRadio.setToggleGroup(actionGroup);
        banUserRadio.setToggleGroup(actionGroup);
    }

    public void initData(Stage stage, AdminReport report, Runnable onSuccessCallback) {
        this.stage = stage;
        this.report = report;
        this.onSuccessCallback = onSuccessCallback;
    }

    /**
     * Handles the submission of the resolution action, either banning the seller or deleting the reported advertisement.
     */
    @FXML
    private void handleSubmit() {
        String reason = reasonTextArea != null && reasonTextArea.getText() != null ? reasonTextArea.getText().trim() : "";

        try {
            if (banUserRadio != null && banUserRadio.isSelected()) {
                var ad = AppContext.getAdvertisementService().getAdvertisementById(report.getAdId());

                if (ad != null) {
                    if (ad.getSeller() != null) {
                        String sellerId = String.valueOf(ad.getSeller().getId());
                        AppContext.getAdminService().blockUser(sellerId, reason);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "خطا", "اطلاعات فروشنده برای این آگهی یافت نشد.");
                        return;
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "خطا", "آگهی مورد نظر یافت نشد.");
                    return;
                }
            }

            if (deleteAdRadio != null && deleteAdRadio.isSelected()) {
                AppContext.getAdvertisementService().deleteAdvertisement(report.getAdId());
            }

            if (stage != null) {
                stage.close();
            }

            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در انجام عملیات: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(javafx.stage.StageStyle.UNDECORATED);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);

        try {
            dialogPane.getStylesheets().add(getClass().getResource("/org/example/divar/css/style.css").toExternalForm());
        } catch (Exception ignored) {
        }

        alert.showAndWait();
    }
}