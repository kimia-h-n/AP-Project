package org.example.divar.util;

public class HandleErrors {

    /**
     * Returns a Persian error message based on error code, message, or HTTP status.
     *
     * @param error   error code (e.g., "USER_NOT_FOUND")
     * @param message error message
     * @param status  HTTP status code
     * @return Persian translated error message
     */
    public static String getPersianMessage(String error, String message, int status) {

        if (error != null) {
            switch (error) {
                case "INVALID_PASSWORD_OR_USERNAME":
                    return "نام کاربری یا رمز عبور اشتباه است.";
                case "USER_NOT_FOUND":
                    return "کاربر یافت نشد.";
                case "AD_NOT_FOUND":
                    return "آگهی مورد نظر پیدا نشد.";
                case "USERNAME_ALREADY_EXISTS":
                    return "این نام کاربری قبلاً ثبت شده است.";
                case "EMAIL_ALREADY_EXISTS":
                    return "این ایمیل قبلاً ثبت شده است.";
                case "PHONE_NUMBER_ALREADY_EXISTS":
                    return "این شماره تماس قبلاً ثبت شده است.";
                case "AD_ALREADY_FAVORITE":
                    return "این آگهی قبلاً به علاقه‌مندی‌ها اضافه شده است.";
                case "AD_NOT_FAVORITE":
                    return "این آگهی در لیست علاقه‌مندی‌های شما نیست.";
                case "NOT_ALLOWED_AD_VIEW":
                    return "این آگهی هنوز تایید نشده و قابل مشاهده نیست.";
                case "OPERATION_NOT_ALLOWED":
                    return "شما اجازه انجام این عملیات را ندارید.";
                case "AD_NOT_REMOVABLE":
                    return "این آگهی قبلاً حذف شده است.";
                case "USER_ALREADY_BLOCKED":
                    return "این کاربر از قبل مسدود شده است.";
                case "USER_ALREADY_ENABLED":
                    return "این کاربر از قبل فعال است.";
                case "INTERNAL_SERVER_ERROR":
                    return "خطای داخلی سرور رخ داده است.";
                case "CANNOT_REPORT_OWN_AD":
                    return "شما نمی‌توانید آگهی خودتان را گزارش کنید.";
                case "AD_ALREADY_SPAMMED":
                    return "این آگهی قبلاً گزارش شده و در دست بررسی است.";
                case "INVALID_REPORT_REASON":
                    return "دلیل گزارش انتخاب شده معتبر نیست.";
                case "INVALID_IMAGE_FORMAT":
                    return "فرمت فایل انتخابی باید تصویر (png, jpg, jpeg) باشد.";
                case "ALREADY_VOTED":
                case "USER_ALREADY_VOTED":
                    return "شما قبلاً به این فروشنده امتیاز داده‌اید.";
                case "USER_IS_BLOCKRD_FROM_LOGIN":
                    return "شما مسدود شده اید.";
            }
        }

        if (message != null && !message.isBlank()) {
            if (message.equalsIgnoreCase("User already voted!")) {
                return "شما قبلاً به این فروشنده امتیاز داده‌اید.";
            }
            if (message.equalsIgnoreCase("You cannot rate yourself!")) {
                return "شما نمی‌توانید به خودتان امتیاز دهید.";
            }
            return message;
        }

        if (status == 403) {
            return "دسترسی غیرمجاز یا عملیات نامعتبر است.";
        }

        return "خطای ناشناخته (کد: " + status + ")";
    }
}

