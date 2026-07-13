package org.example.divar.model;

public enum Category {
    PROPERTY("املاک"),
    VEHICLE("وسایل نقلیه"),
    ELECTRONIC("کالای دیجیتال"),
    HOME("خانه و آشپزخانه"),
    SERVICE("خدمات"),
    PERSONAL("وسایل شخصی"),
    HOBBIT("سرگرمی و فراغت"),
    SOCIAL("اجتماعی"),
    INDUSTRIAL("تجهیزات و صنعتی"),
    JOB("استخدام و کاریابی");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.getLabel().equals(text)) {
                return category;
            }
        }
        throw new RuntimeException("Category not found!" + text);
    }
}

