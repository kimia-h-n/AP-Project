package org.example.divar.controller;

import org.example.divar.SwitchStage;
import org.example.divar.component.AdvertisementCard;
import org.example.divar.model.Advertisement;
import org.example.divar.model.City;
import org.example.divar.service.AdvertisementService;
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
    @FXML private ComboBox<String> cityFilterComboBox;
    @FXML private ComboBox<String> timeFilterComboBox;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private TextField searchField;

    private ArrayList<Advertisement> advertisements = new ArrayList<>();
    private ArrayList<Advertisement> activeAds = new ArrayList<>();

    private String selectedCategory = "همه دسته‌بندی‌ها";

    @FXML
    public void initialize() {

        cityFilterComboBox.getItems().add("همه شهرها");
        for (City city : City.values()) {
            cityFilterComboBox.getItems().add(city.getLabel());
        }
        cityFilterComboBox.setValue("همه شهرها");

        timeFilterComboBox.getItems().addAll("همه زمان‌ها", "امروز", "دیروز", "هفته گذشته");
        timeFilterComboBox.setValue("همه زمان‌ها");

        loadActiveAd();

        adsGrid.widthProperty().addListener((obs, old, newVal) -> {
            if (newVal.doubleValue() > 0) {
                showAds(newVal.doubleValue());
            }
        });
    }

    private void loadActiveAd() {
        AdvertisementService service = AppContext.getAdvertisementService();
        advertisements = service.getActiveAdvertisements();
        activeAds = new ArrayList<>(advertisements);

        System.out.println("تعداد آگهی‌ها: " + advertisements.size());
        showAds(adsGrid.getWidth() > 0 ? adsGrid.getWidth() : 900);
    }

    @FXML
    private void handleCategoryClick(ActionEvent event) {
        Hyperlink clickedLink = (Hyperlink) event.getSource();
        selectedCategory = clickedLink.getText();

        filterInputs();
    }

    @FXML
    private void filterInputs() {

        String keyword;
        if (searchField.getText() == null) {
            keyword = "";
        } else {
            keyword = searchField.getText().trim().toLowerCase();
        }

        String selectedCity = cityFilterComboBox.getValue();
        long minPrice = getPriceFromField(minPriceField, 0);
        long maxPrice = getPriceFromField(maxPriceField, Long.MAX_VALUE);

        activeAds = processFilters(keyword, selectedCity, minPrice, maxPrice);
        showAds(adsGrid.getWidth() > 0 ? adsGrid.getWidth() : 900);
    }


    private long getPriceFromField(TextField field, long defaultValue) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return defaultValue;
        }
    }

    private ArrayList<Advertisement> processFilters(String keyword, String city, long min, long max) {
        ArrayList<Advertisement> filteredList = new ArrayList<>();

        for (Advertisement advertisement : advertisements) {

            boolean hasKeyword = false;
            if (keyword.isEmpty()) {
                hasKeyword = true;
            } else if (advertisement.getTitle().toLowerCase().contains(keyword) ||
                    (advertisement.getDescription() != null && advertisement.getDescription().toLowerCase().contains(keyword))) {
                hasKeyword = true;
            }

            boolean hasCategory = false;
            if (selectedCategory.equals("همه دسته‌بندی‌ها")) {
                hasCategory = true;
            } else if (advertisement.getCategory() != null && advertisement.getCategory().getLabel().equals(selectedCategory)) {
                hasCategory = true;
            }

            boolean hasCity = false;
            if (city.equals("همه شهرها")) {
                hasCity = true;
            } else if (advertisement.getCity() != null && advertisement.getCity().getLabel().equals(city)) {
                hasCity = true;
            }

            boolean hasPrice = false;
            if (advertisement.getPrice() >= min && advertisement.getPrice() <= max){
                hasPrice = true;
            }

            if (hasKeyword && hasCity && hasPrice && hasCategory) {
                filteredList.add(advertisement);
            }
        }
        return filteredList;
    }

    private void showAds(double width) {
        adsGrid.getChildren().clear();

        if (activeAds.isEmpty()) {
            Label empty = new Label("هیچ آگهی‌ای با این فیلترها پیدا نشد.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
            adsGrid.add(empty, 0, 0);
            return;
        }

        int columns = (int) Math.max(1, width / 240);

        int row = 0, col = 0;
        for (Advertisement ad : activeAds) {
            AdvertisementCard card = new AdvertisementCard(ad);
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


