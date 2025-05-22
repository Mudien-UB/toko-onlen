package com.example.toko_onlen.util;

import java.math.BigDecimal;

public class BigDecimalParseUtil {

    public static BigDecimal strToBigDecimal(String str, String message) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(
                    (message == null || message.isBlank()) ? "Invalid BigDecimal value" : message
            );
        }
    }

    public static BigDecimal strToBigDecimal(String str) {
        return strToBigDecimal(str, null);
    }

    // Validasi nilai > 0 (positif)
    public static void validatePositive(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    (message == null || message.isBlank()) ? "Value must be positive" : message
            );
        }
    }

    public static void validatePositive(BigDecimal value) {
        validatePositive(value, null);
    }

    public static void validatePositive(String value, String message) {
        validatePositive(strToBigDecimal(value), message);
    }

    public static void validatePositive(String value) {
        validatePositive(strToBigDecimal(value), null);
    }



}
