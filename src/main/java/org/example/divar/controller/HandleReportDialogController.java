package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.divar.model.AdminReport;
import org.example.divar.model.ReportResolutionAction;
import org.example.divar.util.AppContext;

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

    @FXML
    private void handleSubmit() {
        String reason = reasonTextArea.getText().trim();
        if (reason.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "توجه", "لطفاً علت اقدام را وارد کنید.");
        } else {
            ReportResolutionAction action;
            if (banUserRadio.isSelected()) {
                action = ReportResolutionAction.BLOCK_USER;
            } else {
                action = ReportResolutionAction.DELETE_AD;
            }

            try {
                AppContext.getAdminService().resolveReport(report.getId(), action, reason);

                if (stage != null) {
                    stage.close();
                }

                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "خطا", "خطا در ثبت اقدام: " + e.getMessage());
            }
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