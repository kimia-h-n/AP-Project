package org.example.divar.model;

public enum AdvertisementStatus {
    PENDING("در انتظار بررسی"),
    REJECTED("رد شده"),
    REMOVED("حذف شده"),
    APPROVED("تایید شده"),
    SOLD("فروخته شده");

    private final String label;

    AdvertisementStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static AdvertisementStatus fromString(String text) {
        for (AdvertisementStatus status : AdvertisementStatus.values()) {
            if (status.getLabel().equals(text)) {
                return status;
            }
        }
        throw new RuntimeException("Advertisement status not found: " + text);
    }
}


