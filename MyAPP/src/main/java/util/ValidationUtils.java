package main.java.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidationUtils {
    // Prevent instantiation
    private ValidationUtils() {}

    // ---------- NAME ----------
    public static boolean isValidName(String name) {
        return name != null && name.matches("^[A-Za-z]+$");
    }

    // ---------- AGE ----------
    public static boolean isValidAge(String ageStr) {
        try {
            int age = Integer.parseInt(ageStr);
            return age >= 1 && age <= 150;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ---------- DATE ----------
    public static Date parseValidDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}