package com.learning.core.utils.date.utils;

import com.learning.core.domain.constants.AsciiStrCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterUtils {
    public static final char SPACE = ' ';
    private static final char DBC_CHAR_START = '!';
    private static final char DBC_CHAR_END = '~';
    private static final char SBC_CHAR_START = '！';
    private static final char SBC_CHAR_END = '～';
    private static final int CONVERT_STEP = 65248;
    private static final char SBC_SPACE = '　';
    private static final char DBC_SPACE = ' ';
    private static final char DBC_NUMBER_START = '0';
    private static final char DBC_NUMBER_END = '9';
    private static final char SBC_NUMBER_START = '０';
    private static final char SBC_NUMBER_END = '９';
    private static final char SBC_CHAR_UPPERCASE_START = 'Ａ';
    private static final char SBC_CHAR_UPPERCASE_END = 'Ｚ';
    private static final char SBC_CHAR_LOWERCASE_START = 'ａ';
    private static final char SBC_CHAR_LOWERCASE_END = 'ｚ';
    private static Logger logger = LoggerFactory.getLogger(CharacterUtils.class);

    public CharacterUtils() {
    }

    /**
     * 判断对象是否为字符
     *
     * @param value
     * @return
     */
    public static boolean isChar(Object value) {
        return value != null && (value instanceof Character || value.getClass() == Character.TYPE);
    }

    /**
     * 判断字符对象是否为英文数字
     *
     * @param value
     * @return
     */
    public static boolean isNumber(char value) {
        return DBC_NUMBER_START <= value && value <= DBC_NUMBER_END;
    }

    /**
     * 判断字符是否为英文字符
     *
     * @param value
     * @return
     */
    public static boolean isLetter(char value) {
        return isLetterLower(value) || isLetterUp(value);
    }

    /**
     * 判断字符是否为大写英文字符
     *
     * @param value
     * @return
     */
    public static boolean isLetterUp(char value) {
        return SBC_CHAR_UPPERCASE_START <= value && value <= SBC_CHAR_UPPERCASE_END;
    }

    /**
     * 判断字符是否为小写英文字符
     *
     * @param value
     * @return
     */
    public static boolean isLetterLower(char value) {
        return SBC_CHAR_LOWERCASE_START <= value && value <= SBC_CHAR_LOWERCASE_END;
    }

    /**
     * 判断字符是否为空
     *
     * @param value
     * @return
     */
    public static boolean isBlank(char value) {
        return isBlank((int) value);
    }

    /**
     * 判断字符二进制编码是否为空
     *
     * @param value
     * @return
     */
    public static boolean isBlank(int value) {
        return Character.isWhitespace(value) || Character.isSpaceChar(value) || value == 65279 || value == 8234;
    }

    /**
     * 将字符转化为字符串
     *
     * @param c
     * @return
     */
    public static String toString(char c) {
        return AsciiStrCache.toString(c);
    }

    /**
     * 半角字符串转换为全角字符串
     *
     * @param src
     * @return
     */
    public static String bj2qj(String src) {
        if (src == null) {
            return null;
        } else {
            StringBuilder buf = new StringBuilder(src.length());
            char[] ca = src.toCharArray();

            for (char c : ca) {
                if (c == DBC_SPACE) {
                    buf.append(SBC_SPACE);
                } else if (c >= DBC_CHAR_START && c <= DBC_CHAR_END) {
                    buf.append((char) (c + 'ﻠ'));
                } else {
                    buf.append(c);
                }
            }

            return buf.toString();
        }
    }

    /**
     * 全角字符串转换为半角字符串(不包含数字)
     *
     * @param src
     * @return
     */
    public static String qj2bj(String src) {
        if (src == null) {
            return null;
        } else {
            StringBuilder buf = new StringBuilder(src.length());
            char[] ca = src.toCharArray();

            for (char c : ca) {
                if (c >= SBC_CHAR_START && c <= SBC_CHAR_END) {
                    buf.append((char) (c - 'ﻠ'));
                } else if (c == SBC_SPACE) {
                    buf.append(DBC_SPACE);
                } else {
                    buf.append(c);
                }
            }

            return buf.toString();
        }
    }

    /**
     * 全角字符串转换为半角字符串(包含数字)
     *
     * @param src
     * @return
     */
    public static String qj2bjCharNumber(String src) {
        if (src == null) {
            return null;
        } else {
            StringBuilder buf = new StringBuilder(src.length());
            char[] ca = src.toCharArray();

            for (char c : ca) {
                if (c >= SBC_NUMBER_START && c <= SBC_NUMBER_END) {
                    buf.append((char) (c - 'ﻠ'));
                } else if (c >= SBC_CHAR_LOWERCASE_START && c <= SBC_CHAR_LOWERCASE_END) {
                    buf.append((char) (c - 'ﻠ'));
                } else if (c >= SBC_CHAR_UPPERCASE_START && c <= SBC_CHAR_UPPERCASE_END) {
                    buf.append((char) (c - 'ﻠ'));
                } else {
                    buf.append(c);
                }
            }

            return buf.toString();
        }
    }
}
