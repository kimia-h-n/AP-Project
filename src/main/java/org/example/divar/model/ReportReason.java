package org.example.divar.model;

public enum ReportReason {
    FRAUD("کلاهبرداری"),
    IMMORAL("غیرقانونی یا غیراخلاقی"),
    WRONG_CATEGORY("دسته‌بندی اشتباه"),
    WRONG_PRICE("قیمت اشتباه"),
    WRONG_INFORMATION("اطلاعات اشتباه"),
    DUPLICATE("تکراری یا اسپم"),
    UNAVAILABLE("ناموجود یا پاسخگو نبودن"),
    OTHERS("سایر");

    private final String label;

    ReportReason(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static ReportReason fromString(String text) {
        for (ReportReason reason : ReportReason.values()) {
            if (reason.name().equalsIgnoreCase(text) || reason.getLabel().equals(text)) {
                return reason;
            }
        }
        return OTHERS;
    }
}