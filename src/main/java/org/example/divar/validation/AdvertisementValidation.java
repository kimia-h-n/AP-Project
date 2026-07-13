package org.example.divar.validation;

import org.example.divar.model.Category;
import org.example.divar.model.City;
import org.example.divar.model.ProductCondition;

public class AdvertisementValidation {

    public void advertisementValidation(String title, String price, Category category, City city,
                                        ProductCondition condition, String address) throws IllegalArgumentException {

        if (category == null) {
            throw new IllegalArgumentException("لطفا یک دسته‌بندی برای آگهی خود انتخاب کنید.");
        }
        if (condition == null) {
            throw new IllegalArgumentException("لطفا یک وضعیت برای آگهی خود انتخاب کنید.");
        }
        if (city == null) {
            throw new IllegalArgumentException("لطفا یک شهر برای آگهی خود انتخاب کنید.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("فیلد عنوان آگهی نمی‌تواند خالی باشد.");
        }
        if (price == null || price.trim().isEmpty()) {
            throw new IllegalArgumentException("قیمت وارد نشده است.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("فیلد آدرس آگهی الزامی است.");
        }
        try {
            long lPrice = Long.parseLong(price.trim());
            if (lPrice < 0) {
                throw new IllegalArgumentException("قیمت آگهی نا معتبر است.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("قیمت باید به صورت عددی وارد شود (از حروف استفاده نکنید).");
        }
    }
}
