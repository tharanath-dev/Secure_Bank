package com.securebank.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility for validating incoming form inputs and parameters.
 * Demonstrates Module IV: Map/HashMap for field error tracking.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static Double parsePositiveAmount(String amountStr) {
        if (isBlank(amountStr)) return null;
        try {
            double val = Double.parseDouble(amountStr.trim());
            return val > 0 ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Module IV: Validates customer registration fields and populates an error HashMap.
     */
    public static Map<String, String> validateRegistration(String name, String email, String phone, String password, String accountType) {
        Map<String, String> errors = new HashMap<>();

        if (isBlank(name)) {
            errors.put("name", "Full name is required.");
        } else if (name.trim().length() < 3) {
            errors.put("name", "Name must be at least 3 characters long.");
        }

        if (!isValidEmail(email)) {
            errors.put("email", "Please provide a valid email address.");
        }

        if (!isValidPhone(phone)) {
            errors.put("phone", "Phone number must contain 10 to 15 digits.");
        }

        if (!isValidPassword(password)) {
            errors.put("password", "Password must be at least 6 characters long.");
        }

        if (!"SAVINGS".equalsIgnoreCase(accountType) && !"CURRENT".equalsIgnoreCase(accountType)) {
            errors.put("accountType", "Invalid account type selected.");
        }

        return errors;
    }
}

