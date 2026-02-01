package com.arbitrage.common.utils;

import java.util.Random;

public class LogUtils {
    private static final String characters = "0123456789abcdef";

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
