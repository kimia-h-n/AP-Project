package org.example.divar.util;

public class HandleErrors {

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
                    return "شما اجازه‌ی حذف این آگهی را ندارید.";
                case "AD_NOT_REMOVABLE":
                    return "این آگهی قبلاً حذف شده است.";

            }
        }

        if (message != null && !message.isBlank()) {
            return message;
        }

        return   status  + "Error: " ;
    }
}

