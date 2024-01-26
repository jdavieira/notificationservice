package com.critical.notificationservice.util;

import java.util.regex.Pattern;

public class EmailValidation {

    public static boolean isEmailInvalid(String email) {
        String regexPattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        return !Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE)
                .matcher(email)
                .matches();
    }
}