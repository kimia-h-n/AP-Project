package org.example.divar.model;

public enum AdSortChoice {
    NEWEST("جدیدترین"),
    CHEAPEST("ارزان‌ترین"),
    MOST_EXPENSIVE("گران‌ترین"),
    SELLER_RATING("امتیاز فروشنده");

    private final String label;

    AdSortChoice(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static AdSortChoice fromString(String text) {
        for (AdSortChoice choice : AdSortChoice.values()) {
            if (choice.getLabel().equals(text)) {
                return choice;
            }
        }
        throw new RuntimeException("Sort choice not found: " + text);
    }
}