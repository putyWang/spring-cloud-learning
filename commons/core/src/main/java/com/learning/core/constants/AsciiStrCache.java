package com.learning.core.constants;

/**
 * 字符缓存（小于128的字符）
 */
public class AsciiStrCache {
    private static final int ASCII_LENGTH = 128;
    private static final String[] CACHE = new String[128];

    static {
        for (char c = 0; c < ASCII_LENGTH; ++c) {
            CACHE[c] = String.valueOf(c);
        }

    }

    public AsciiStrCache() {
    }

    public static String toString(char c) {
        return c < ASCII_LENGTH ? CACHE[c] : String.valueOf(c);
    }
}
