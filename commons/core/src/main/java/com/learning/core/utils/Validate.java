package com.learning.core.utils;

import java.util.Collection;
import java.util.Map;

public class Validate {
    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false";
    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";
    private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "The validated array is empty";
    private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "The validated collection is empty";
    private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "The validated map is empty";
    private static final String DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence is empty";
    private static final String DEFAULT_NOT_BLANK_EX_MESSAGE = "The validated character sequence is blank";

    public Validate() {
    }

    public static void isTrue(boolean expression, String message, Object value) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, value));
        }
    }

    public static void isTrue(boolean expression, String message, Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException("The validated expression is false");
        }
    }

    public static void notNull(Object value) {
        notNull(value, "The validated object is null");
    }

    public static void notNull(Object value, String message, Object... values) {
        if (null == value) {
            throw new NullPointerException(String.format(message, values));
        }
    }

    public static void notEmpty(Object[] array) {
        notEmpty(array, "The validated array is empty");
    }

    public static void notEmpty(Object[] array, String message, Object... values) {
        if (null == array) {
            throw new NullPointerException(String.format(message, values));
        } else if (0 == array.length) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T extends Collection<?>> void notEmpty(T collection) {
        notEmpty(collection, "The validated collection is empty");
    }

    public static <T extends Collection<?>> void notEmpty(T collection, String message, Object... values) {
        if (null == collection) {
            throw new NullPointerException(String.format(message, values));
        } else if (collection.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T extends Map<?, ?>> void notEmpty(T map) {
        notEmpty(map, "The validated map is empty");
    }

    public static <T extends Map<?, ?>> void notEmpty(T map, String message, Object... values) {
        if (null == map) {
            throw new NullPointerException(String.format(message, values));
        } else if (map.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T extends CharSequence> void notEmpty(T str) {
        notEmpty(str, "The validated character sequence is empty");
    }

    public static <T extends CharSequence> void notEmpty(T str, String message, Object... values) {
        if (null == str) {
            throw new NullPointerException(String.format(message, values));
        } else if (0 == str.length()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T extends CharSequence> void notBlank(T str) {
        notBlank(str, "The validated character sequence is blank");
    }

    public static <T extends CharSequence> void notBlank(T str, String message, Object... values) {
        if (null == str) {
            throw new NullPointerException(String.format(message, values));
        } else if (StringUtil.isBlank(str)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }
}
