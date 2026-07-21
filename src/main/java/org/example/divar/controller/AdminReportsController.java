package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.example.divar.component.ReportCard;
import org.example.divar.model.AdminReport;
import org.example.divar.util.AppContext;

import java.util.ArrayList;
import java.util.List;

public class AdminReportsController {

    @FXML private FlowPane reportsFlowPane;
    @FXML private Label messageLabel;
    @FXML private Label emptyLabel;

    @FXML
    public void initialize() {
        loadReports();
    }

    public void loadReports() {
        clearMessage();

        List<AdminReport> reports;
        try {
            reports = AppContext.getAdminService().getReports();
        } catch (RuntimeException e) {
            showError(e.getMessage());
            reports = new ArrayList<>();
        }

        reportsFlowPane.getChildren().clear();

        if (reports.isEmpty()) {
            emptyLabel.setVisible(true);
            emptyLabel.setManaged(true);
            return;
        }

        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        for (AdminReport report : reports) {
            ReportCard card = new ReportCard(report, this::loadReports);
            reportsFlowPane.getChildren().add(card);
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("error-message");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void clearMessage() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}
