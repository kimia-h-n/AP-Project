package org.example.divar.model;

public enum ProductCondition {
    NEW("نو"),
    ALMOST_NEW("در حد نو"),
    USED("کارکرده");

    private final String label;

    ProductCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static ProductCondition fromString(String text) {
        for (ProductCondition status : ProductCondition.values()) {
            if (status.getLabel().equals(text)) {
                return status;
            }
        }
        throw new RuntimeException("Condition not found!" + text);
    }
}


