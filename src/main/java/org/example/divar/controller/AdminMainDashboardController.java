package org.example.divar.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.divar.model.DashboardStatistics;
import org.example.divar.util.AppContext;

public class AdminMainDashboardController {

    @FXML private Label lblActiveUsers;
    @FXML private Label lblBlockedUsers;
    @FXML private Label lblTotalAds;
    @FXML private Label lblPendingAds;
    @FXML private Label lblReports;

    @FXML
    public void initialize() {
        loadStats();
    }

    private void loadStats() {
        new Thread(() -> {
            try {
                DashboardStatistics stats = AppContext.getAdminService().getDashboardStats();
                if (stats != null) {
                    Platform.runLater(() -> {
                        if (lblActiveUsers != null) {
                            if (stats.getNumActiveUsers() != null) {
                                lblActiveUsers.setText(String.valueOf(stats.getNumActiveUsers()));
                            } else {
                                lblActiveUsers.setText("0");
                            }
                        }

                        if (lblBlockedUsers != null) {
                            if (stats.getNumBlockedUsers() != null) {
                                lblBlockedUsers.setText(String.valueOf(stats.getNumBlockedUsers()));
                            } else {
                                lblBlockedUsers.setText("0");
                            }
                        }

                        if (lblTotalAds != null) {
                            if (stats.getNumAds() != null) {
                                lblTotalAds.setText(String.valueOf(stats.getNumAds()));
                            } else {
                                lblTotalAds.setText("0");
                            }
                        }

                        if (lblPendingAds != null) {
                            if (stats.getNumPendingAds() != null) {
                                lblPendingAds.setText(String.valueOf(stats.getNumPendingAds()));
                            } else {
                                lblPendingAds.setText("0");
                            }
                        }

                        if (lblReports != null) {
                            if (stats.getNumReports() != null) {
                                lblReports.setText(String.valueOf(stats.getNumReports()));
                            } else {
                                lblReports.setText("0");
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Error loading dashboard stats: " + e.getMessage());
            }
        }).start();
    }
}