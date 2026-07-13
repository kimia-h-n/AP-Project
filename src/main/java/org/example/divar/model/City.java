package org.example.divar.model;

public enum City {
    TEHRAN("تهران"),
    KARAJ("کرج"),
    MASHHAD("مشهد"),
    ISFAHAN("اصفهان"),
    TABRIZ("تبریز");

    private final String label;

    City(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static City fromString(String text) {
        for (City city : City.values()) {
            if (city.getLabel().equals(text)) {
                return city;
            }
        }
        throw new RuntimeException("City not found!" + text);
    }
}
