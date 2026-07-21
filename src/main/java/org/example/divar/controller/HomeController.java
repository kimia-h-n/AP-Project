package org.example.divar.controller;

import org.example.divar.SwitchStage;
import org.example.divar.component.AdSummaryCard;
import org.example.divar.model.Advertisement;
import org.example.divar.model.Category;
import org.example.divar.model.City;
import org.example.divar.model.DateFilter;
import org.example.divar.util.AppContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class HomeController {

    @FXML private GridPane adsGrid;
    @FXML private ComboBox<City> cityFilterComboBox;
    @FXML private ComboBox<String> timeFilterComboBox;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private TextField searchField;

    private ArrayList<Advertisement> activeAds = new ArrayList<>();
    private Category selectedCategoryEnum = null;
    private Hyperlink lastClickedCategoryLink = null;

    @FXML
    public void initialize() {
        cityFilterComboBox.setConverter(new javafx.util.StringConverter<City>() {
            @Override
            public String toString(City cityObject) {
                return cityObject == null ? "" : cityObject.getName();
            }

            @Override
            public City fromString(String string) {
                return null;
            }
        });

        try {
            ArrayList<City> serverCities = AppContext.getAdvertisementService().getAllProvinces();

            City allCitiesPlaceholder = new City(null, "همه شهرها");
            cityFilterComboBox.getItems().add(allCitiesPlaceholder);
            cityFilterComboBox.getItems().addAll(serverCities);
            cityFilterComboBox.setValue(allCitiesPlaceholder);
        } catch (Exception e) {
            System.err.println("Error loading cities in home: " + e.getMessage());
        }

        timeFilterComboBox.getItems().clear();
        timeFilterComboBox.getItems().add("همه زمان‌ها");
        for (DateFilter df : DateFilter.values()) {
            timeFilterComboBox.getItems().add(df.getLabel());
        }
        timeFilterComboBox.setValue("همه زمان‌ها");
        loadActiveAd();

        adsGrid.widthProperty().addListener((obs, old, newVal) -> {
            if (newVal.doubleValue() > 0) {
                showAds(newVal.doubleValue());
            }
        });
    }

    private void loadActiveAd() {
        try {
            activeAds = AppContext.getAdvertisementService().getActiveAdvertisements();
            showAds(adsGrid.getWidth() > 0 ? adsGrid.getWidth() : 900);
        } catch (Exception e) {
            System.err.println("خطا در لود اولیه آگهی‌ها: " + e.getMessage());
        }
    }

    @FXML
    private void handleCategoryClick(ActionEvent event) {
        Hyperlink clickedLink = (Hyperlink) event.getSource();

        if (lastClickedCategoryLink != null) {
            lastClickedCategoryLink.setStyle("-fx-text-fill: #303030; -fx-font-weight: normal;");
        }
        clickedLink.setStyle("-fx-text-fill: #A62626; -fx-font-weight: bold;");
        lastClickedCategoryLink = clickedLink;

        String categoryText = clickedLink.getText();

        if ("همه دسته‌بندی‌ها".equals(categoryText)) {
            selectedCategoryEnum = null;
            loadActiveAd();
            return;
        }

        try {
            selectedCategoryEnum = Category.fromString(categoryText);
            activeAds = AppContext.getAdvertisementService().filterAdvertisements(null, null, selectedCategoryEnum, null, null);
            showAds(adsGrid.getWidth() > 0 ? adsGrid.getWidth() : 900);
        } catch (Exception e) {
            System.err.println("خطا در فیلتر دسته‌بندی: " + e.getMessage());
        }
    }

    @FXML
    private void filterInputs() {
        City selectedCity = cityFilterComboBox.getValue();
        Long cityId = (selectedCity != null && selectedCity.getId() != null) ? selectedCity.getId() : null;

        Long minPrice = getPriceFromField(minPriceField);
        Long maxPrice = getPriceFromField(maxPriceField);

        String selectedTimeLabel = timeFilterComboBox.getValue();
        DateFilter dateFilter = DateFilter.fromString(selectedTimeLabel);

        try {
            activeAds = AppContext.getAdvertisementService().filterAdvertisements(
                    minPrice, maxPrice, selectedCategoryEnum, cityId, dateFilter
            );
            showAds(adsGrid.getWidth() > 0 ? adsGrid.getWidth() : 900);
        } catch (Exception e) {
            System.err.println("خطا در اعمال فیلترها: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = (searchField.getText() != null) ? searchField.getText().trim() : "";

        try {
            if (keyword.isEmpty()) {
                activeAds = AppContext.getAdvertisementService().getActiveAdvertisements();
            } else {
                activeAds = AppContext.getAdvertisementService().searchAdvertisements(keyword);
            }

            double currentWidth = adsGrid.getWidth() > 0 ? adsGrid.getWidth() : 900;
            showAds(currentWidth);

        } catch (Exception e) {
            System.err.println("خطا در سرچ آگهی: " + e.getMessage());
        }
    }

    private Long getPriceFromField(TextField field) {
        if (field == null || field.getText() == null) return null;
        String text = field.getText().trim();
        if (text.isEmpty()) return null;
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showAds(double width) {
        adsGrid.getChildren().clear();

        if (activeAds == null || activeAds.isEmpty()) {
            Label empty = new Label("هیچ آگهی‌ای با این فیلترها پیدا نشد.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
            adsGrid.add(empty, 0, 0);
            return;
        }

        int columns = (int) Math.max(1, width / 240);
        int row = 0, col = 0;

        for (Advertisement ad : activeAds) {
            AdSummaryCard card = new AdSummaryCard(ad, false);
            adsGrid.add(card, col, row);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    @FXML private void goToNewAd() { SwitchStage.switchToNewAd(); }
    @FXML private void goToChat() { SwitchStage.switchToChat(); }
    @FXML private void goToProfile() { SwitchStage.switchToProfile(); }
}




