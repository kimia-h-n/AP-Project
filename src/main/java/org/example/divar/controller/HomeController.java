package org.example.divar.controller;

import org.example.divar.SwitchStage;
import org.example.divar.component.AdSummaryCard;
import org.example.divar.model.*;
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

/**
 * Controller class for the main Home view, managing advertisement display, filtering, sorting, and navigation.
 */
public class HomeController {

    @FXML private GridPane adsGrid;
    @FXML private ComboBox<City> cityFilterComboBox;
    @FXML private ComboBox<String> timeFilterComboBox;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private TextField searchField;
    @FXML private Hyperlink allCategoriesLink, realEstateLink, vehiclesLink, digitalLink,
            entertainmentLink, jobsLink, homeKitchenLink, industrialLink,
            socialLink, servicesLink, personalGoodsLink;

    @FXML private Hyperlink sortNewestLink, sortCheapestLink, sortExpensiveLink, sortRatingLink;

    private AdSortChoice selectedSortChoice = null;
    private Hyperlink lastClickedSortLink = null;
    private ArrayList<Advertisement> activeAds = new ArrayList<>();
    private Category selectedCategoryEnum = null;
    private Hyperlink lastClickedCategoryLink = null;

    @FXML
    public void initialize() {
        cityFilterComboBox.setConverter(CityFormatter.createStringConverter());

        lastClickedCategoryLink = allCategoriesLink;
        if (allCategoriesLink != null) {
            allCategoriesLink.getStyleClass().add("category-link-selected");
        }

        try {
            ArrayList<City> serverCities = AppContext.getAdvertisementService().getAllProvinces();
            City allCitiesPlaceholder = new City(null, "همه شهرها");
            cityFilterComboBox.getItems().add(allCitiesPlaceholder);
            cityFilterComboBox.getItems().addAll(serverCities);
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

    /**
     * Handles category selection clicks to filter advertisements by category and update UI styles.
     *
     * @param event the ActionEvent triggered by clicking a category hyperlink
     */
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

    /**
     * Handles sorting option clicks to reorder displayed advertisements.
     */
    @FXML
    private void handleSortClick(ActionEvent event) {
        Hyperlink clickedLink = (Hyperlink) event.getSource();

        if (lastClickedSortLink != null) {
            lastClickedSortLink.getStyleClass().remove("category-link-selected");
            if (!lastClickedSortLink.getStyleClass().contains("category-link")) {
                lastClickedSortLink.getStyleClass().add("category-link");
            }
        }

        clickedLink.getStyleClass().remove("category-link");
        if (!clickedLink.getStyleClass().contains("category-link-selected")) {
            clickedLink.getStyleClass().add("category-link-selected");
        }

        lastClickedSortLink = clickedLink;
        String sortText = clickedLink.getText();

        try {
            selectedSortChoice = AdSortChoice.fromString(sortText);
            activeAds = AppContext.getAdvertisementService().getSortedAds(selectedSortChoice);

            double widthToUse = adsGrid.getWidth() > 0 ? adsGrid.getWidth() : 900;
            showAds(widthToUse);
        } catch (Exception e) {
            System.err.println("Error sorting advertisements: " + e.getMessage());
        }
    }

    /**
     * Applies filters such as price range, city, and date to the advertisement list.
     */
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

    /**
     * Searches advertisements based on the keyword entered in the search field.
     */
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

    /**
     * Dynamically renders advertisement summary cards in a responsive grid layout
     * based on the current width of the container.
     *
     * @param width the current width of the advertisements grid
     */
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




