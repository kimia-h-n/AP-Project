package org.example.divar.validation;

import org.example.divar.model.*;

public class UserValidation {

    public void loginValidation(String username, String password) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("لطفاً نام کاربری خود را وارد کنید.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("لطفاً رمز عبور خود را وارد کنید.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("رمز عبور نمی‌تواند کمتر از 6 کاراکتر باشد.");
        }
    }

    public void userValidation(User user, String password) throws RuntimeException {
        if (user == null) {
            throw new RuntimeException("نام کاربری یا رمز عبور اشتباه است.");
        }
        if (!user.isPasswordCorrect(password)) {
            throw new RuntimeException("نام کاربری یا رمز عبور اشتباه است.");
        }
        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("حساب کاربری شما مسدود شده است!");
        }
    }

    public void registerValidation(String firstname, String lastname, String username, String password, String phoneNumber, String email) throws IllegalArgumentException {

        if (firstname == null || firstname.trim().isEmpty()) {
            throw new IllegalArgumentException("فیلد نام نمی‌تواند خالی باشد.");
        }
        if (lastname == null || lastname.trim().isEmpty()) {
            throw new IllegalArgumentException("فیلد نام خانوادگی نمی‌تواند خالی باشد.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("نام کاربری نمی‌تواند خالی باشد.");
        }

        String usernameRegex = "^[A-Za-z0-9._]{3,20}$";
        if (!username.matches(usernameRegex)) {
            throw new IllegalArgumentException("نام کاربری باید بین ۳ تا ۲۰ کاراکتر و فقط شامل حروف انگلیسی، اعداد، نقطه (.) یا زیرخط (_) باشد.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("کلمه عبور وارد نشده است.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("رمز عبور حداقل باید 6 کاراکتر باشد.");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("فیلد شماره تماس نمی‌تواند خالی باشد.");
        }

        String phoneRegex = "^09[0-9]{9}$";
        if (!phoneNumber.matches(phoneRegex)) {
            throw new IllegalArgumentException("شماره تماس معتبر نیست.");
        }
        if (phoneNumber.length() != 11) {
            throw new IllegalArgumentException("تعداد ارقام شماره تلفن نامعتبر است (باید دقیقاً ۱۱ رقم باشد).");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("فیلد ایمیل نمی‌تواند خالی باشد.");
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("فرمت ایمیل وارد شده معتبر نیست.");
        }
    }
}

