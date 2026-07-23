package org.example.divar.component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.divar.util.AppContext;

public class SellerRatingDialog {

    @FXML private Label lblAvgRating;
    @FXML private Label lblMessage;
    @FXML private Button btnStar1, btnStar2, btnStar3, btnStar4, btnStar5;
    @FXML private Button btnSubmit;

    private Button[] stars;
    private int selectedRating = 0;
    private Long sellerId;

    @FXML
    public void initialize() {
        stars = new Button[]{btnStar1, btnStar2, btnStar3, btnStar4, btnStar5};
        btnSubmit.setDisable(true);
    }

    public void setSellerData(Long sellerId) {
        this.sellerId = sellerId;
        loadAverageRating();
    }

    private void loadAverageRating() {
        if (sellerId == null) {
            return;
        }

        try {
            double avg = AppContext.getRatingService().getAverageRating(sellerId);
            if (avg > 0) {
                if (lblAvgRating != null) {
                    lblAvgRating.setText(String.format("میانگین امتیاز: %.1f از ۵", avg));
                }
            } else {
                if (lblAvgRating != null) {
                    lblAvgRating.setText("هنوز امتیازی ثبت نشده");
                }
            }
        } catch (Exception e) {
            if (lblAvgRating != null) {
                lblAvgRating.setText("میانگین امتیاز: -");
            }
            System.err.println("Error fetching average rating: " + e.getMessage());
        }
    }

    @FXML
    private void handleStarClick(ActionEvent event) {
        Button clickedBtn = (Button) event.getSource();
        for (int i = 0; i < stars.length; i++) {
            if (stars[i] == clickedBtn) {
                selectedRating = i + 1;
                break;
            }
        }
        updateStarUI();
        btnSubmit.setDisable(false);
    }

    private void updateStarUI() {
        for (int i = 0; i < stars.length; i++) {
            if (i < selectedRating) {
                stars[i].getStyleClass().removeAll("star-button-inactive");
                if (!stars[i].getStyleClass().contains("star-button-active")) {
                    stars[i].getStyleClass().add("star-button-active");
                }
            } else {
                stars[i].getStyleClass().removeAll("star-button-active");
                if (!stars[i].getStyleClass().contains("star-button-inactive")) {
                    stars[i].getStyleClass().add("star-button-inactive");
                }
            }
        }
    }

    @FXML
    private void handleSubmit() {
        if (selectedRating > 0 && sellerId != null) {
            try {
                btnSubmit.setDisable(true);
                AppContext.getRatingService().submitRating(sellerId, selectedRating);
                closeDialog();
            } catch (RuntimeException e) {
                btnSubmit.setDisable(false);
                showError(e.getMessage());
            }
        }
    }

    private void showError(String message) {
        if (lblMessage != null) {
            lblMessage.setText(message);
            lblMessage.getStyleClass().add("rating-error-label");
            lblMessage.setVisible(true);
            lblMessage.setManaged(true);
        } else {
            System.err.println("Rating Error: " + message);
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }
}