package org.example.divar.controller;

import org.example.divar.SwitchStage;
import org.example.divar.component.AdSummaryCard;
import org.example.divar.model.Advertisement;
import org.example.divar.model.Category;
import org.example.divar.model.City;
import org.example.divar.model.DateFilter;
import org.example.divar.util.AppContext;
import org.example.divar.util.CityFormatter;
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
        cityFilterComboBox.setConverter(CityFormatter.createStringConverter());

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
            double width = adsGrid.getWidth();
            if (width > 0) {
                showAds(width);
            } else {
                showAds(900);
            }
        } catch (Exception e) {
            System.err.println("Error loading initial advertisements: " + e.getMessage());
        }
    }

    @FXML
    private void handleCategoryClick(ActionEvent event) {
        Hyperlink clickedLink = (Hyperlink) event.getSource();

        if (lastClickedCategoryLink != null) {
            lastClickedCategoryLink.getStyleClass().remove("category-link-selected");
            if (!lastClickedCategoryLink.getStyleClass().contains("category-link")) {
                lastClickedCategoryLink.getStyleClass().add("category-link");
            }
        }

        clickedLink.getStyleClass().remove("category-link");
        if (!clickedLink.getStyleClass().contains("category-link-selected")) {
            clickedLink.getStyleClass().add("category-link-selected");
        }

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

            double widthToUse;
            if (adsGrid.getWidth() > 0) {
                widthToUse = adsGrid.getWidth();
            } else {
                widthToUse = 900;
            }
            showAds(widthToUse);
        } catch (Exception e) {
            System.err.println("Error filtering by category: " + e.getMessage());
        }
    }

    @FXML
    private void filterInputs() {
        City selectedCity = cityFilterComboBox.getValue();
        Long cityId;
        if (selectedCity != null) {
            cityId = selectedCity.getId();
        } else {
            cityId = null;
        }

        Long minPrice = getPriceFromField(minPriceField);
        Long maxPrice = getPriceFromField(maxPriceField);

        String selectedTimeLabel = timeFilterComboBox.getValue();
        DateFilter dateFilter = DateFilter.fromString(selectedTimeLabel);

        try {
            activeAds = AppContext.getAdvertisementService().filterAdvertisements(
                    minPrice, maxPrice, selectedCategoryEnum, cityId, dateFilter);

            double widthToUse;
            if (adsGrid.getWidth() > 0) {
                widthToUse = adsGrid.getWidth();
            } else {
                widthToUse = 900;
            }
            showAds(widthToUse);
        } catch (Exception e) {
            System.err.println("Error applying filters: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword;
        if (searchField.getText() != null) {
            keyword = searchField.getText().trim();
        } else {
            keyword = "";
        }

        try {
            if (keyword.isEmpty()) {
                activeAds = AppContext.getAdvertisementService().getActiveAdvertisements();
            } else {
                activeAds = AppContext.getAdvertisementService().searchAdvertisements(keyword);
            }

            double currentWidth;
            if (adsGrid.getWidth() > 0) {
                currentWidth = adsGrid.getWidth();
            } else {
                currentWidth = 900;
            }
            showAds(currentWidth);

        } catch (Exception e) {
            System.err.println("Error searching advertisements: " + e.getMessage());
        }
    }

    private Long getPriceFromField(TextField field) {
        if (field == null) {
            return null;
        }
        if (field.getText() == null) {
            return null;
        }

        String text = field.getText().trim();
        if (text.isEmpty()) {
            return null;
        }

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
            empty.getStyleClass().add("empty-ads-label");
            adsGrid.add(empty, 0, 0);
            return;
        }

        int columns;
        if (width / 240 > 1) {
            columns = (int) (width / 240);
        } else {
            columns = 1;
        }

        int row = 0;
        int col = 0;

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

    @FXML private void goToNewAd() {
        SwitchStage.switchToNewAd(); }

    @FXML private void goToChat() {
        SwitchStage.switchToChat(); }

    @FXML private void goToProfile() {
        SwitchStage.switchToProfile(); }
}




