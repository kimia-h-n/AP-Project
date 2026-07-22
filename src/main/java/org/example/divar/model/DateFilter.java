package org.example.divar.model;

public enum DateFilter {
    YESTERDAY("دیروز"),
    PAST_WEEK("هفته گذشته"),
    OLDER("قدیمی‌تر");

    private final String label;

    DateFilter(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static DateFilter fromString(String text) {
        for (DateFilter df : DateFilter.values()) {
            if (df.getLabel().equals(text)) {
                return df;
            }
        }
        return null;
    }
}
