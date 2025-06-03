package com.example.toko_onlen.model.enums;

public enum ORDER_STATUS {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELED;

    public static ORDER_STATUS fromString(String plain) {
        if (plain == null) return null;
        try {
            return ORDER_STATUS.valueOf(plain.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // atau lempar exception jika ingin menangani invalid value
        }
    }
}
