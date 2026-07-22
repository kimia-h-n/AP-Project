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
            Alert alert = new Alert(Alert.AlertType.WARNING, "لطفاً علت اقدام را وارد کنید.");
            alert.showAndWait();
            return;
        }

        ReportResolutionAction action = banUserRadio.isSelected()
                ? ReportResolutionAction.BLOCK_USER
                : ReportResolutionAction.DELETE_AD;

        try {
            AppContext.getAdminService().resolveReport(report.getId(), action, reason);

            if (stage != null) stage.close();
            if (onSuccessCallback != null) onSuccessCallback.run();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "خطا در ثبت اقدام: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        if (stage != null) stage.close();
    }
}