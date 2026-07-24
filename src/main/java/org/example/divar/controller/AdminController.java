package org.example.divar.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.example.divar.SwitchStage;
import org.example.divar.util.SessionManager;
import java.util.List;

public class AdminController {

    @FXML private StackPane contentArea;

    @FXML private Button dashboardBtn;
    @FXML private Button adsBtn;
    @FXML private Button reportsBtn;
    @FXML private Button usersBtn;
    @FXML private Button statsDashboardBtn;

    private Button activeButton;
    private List<Button> menuButtons;

    private static String lastTab = "dashboard";

    @FXML
    public void initialize() {
        if (!SessionManager.isAdmin()) {
            SwitchStage.switchToHome();
            return;
        }

        menuButtons = List.of(dashboardBtn, adsBtn, reportsBtn, usersBtn, statsDashboardBtn);

        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("button");
            if (!btn.getStyleClass().contains("admin-nav-btn")) {
                btn.getStyleClass().add("admin-nav-btn");
            }
        }

        showTab(lastTab);
    }

    private void showTab(String tab) {
        if (tab.equals("ads")) {
            showAds();
        } else if (tab.equals("reports")) {
            showReports();
        } else if (tab.equals("users")) {
            showUsers();
        } else if (tab.equals("stats")) {
            showStatsDashboard();
        } else {
            showDashboard();
        }
    }

    @FXML
    private void showDashboard() {
        lastTab = "dashboard";
        loadPage("/org/example/divar/fxml/admin_dashboard.fxml", dashboardBtn);
    }

    @FXML
    private void showAds() {
        lastTab = "ads";
        loadPage("/org/example/divar/fxml/admin_ads.fxml", adsBtn);
    }

    @FXML
    private void showReports() {
        lastTab = "reports";
        loadPage("/org/example/divar/fxml/admin_reports.fxml", reportsBtn);
    }

    @FXML
    private void showUsers() {
        lastTab = "users";
        loadPage("/org/example/divar/fxml/admin_users.fxml", usersBtn);
    }

    @FXML
    private void showStatsDashboard() {
        lastTab = "stats";
        loadPage("/org/example/divar/fxml/admin_stats_dashboard.fxml", statsDashboardBtn);
    }

    private void loadPage(String fxmlPath, Button clickedButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();
            contentArea.getChildren().setAll(page);
            setActiveButton(clickedButton);
        } catch (Exception e) {
            System.err.println("Error loading page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button clicked) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("admin-nav-btn-active");
            if (!activeButton.getStyleClass().contains("admin-nav-btn")) {
                activeButton.getStyleClass().add("admin-nav-btn");
            }
        }
        clicked.getStyleClass().remove("admin-nav-btn");
        clicked.getStyleClass().add("admin-nav-btn-active");
        activeButton = clicked;
    }

    @FXML
    private void logout() {
        SessionManager.logout();
        SwitchStage.showLogin();
    }
}





