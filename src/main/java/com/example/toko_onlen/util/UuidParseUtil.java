package com.example.toko_onlen.util;

import java.util.UUID;

public class UuidParseUtil {
    public static String uuidToString(UUID uuid) {
        try{
            return uuid != null ? uuid.toString() : null;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid UUID");
        }
    }
    public static UUID stringToUuid(String plainUuid) {
        try{
            if(plainUuid == null){
                return null;
            }
            return UUID.fromString(plainUuid);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid UUID");
        }
    }
    public static boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
