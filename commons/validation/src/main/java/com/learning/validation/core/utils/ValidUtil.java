package com.learning.validation.core.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangWei
 * @version v 1.0
 * @description
 * @date 2024-06-21
 **/
public class ValidUtil {
    private static final String REGEX_IDCARD = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
    private static final String REGEX_PHONE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0-9])|(19[8]))\\d{8}$";
    private static final String REGEX_URL = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+(\\\\?{0,1}(([A-Za-z0-9-~]+\\\\={0,1})([A-Za-z0-9-~]*)\\\\&{0,1})*)$";

    public ValidUtil() {
    }

    public static boolean validIdCard(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        } else {
            boolean matches = value.matches("(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)");
            if (matches && value.length() == 18) {
                try {
                    char[] charArray = value.toCharArray();
                    int[] idCardWi = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    String[] idCardY = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;

                    int idCardMod;
                    for(int i = 0; i < idCardWi.length; ++i) {
                        idCardMod = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = idCardMod * idCardWi[i];
                        sum += count;
                    }

                    char idCardLast = charArray[17];
                    idCardMod = sum % 11;
                    return idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase());
                } catch (Exception var9) {
                    return false;
                }
            } else {
                return matches;
            }
        }
    }

    public static boolean validPhone(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        } else if (value.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0-9])|(19[8]))\\d{8}$");
            Matcher m = p.matcher(value);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }

    public static boolean validUrl(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+(\\\\?{0,1}(([A-Za-z0-9-~]+\\\\={0,1})([A-Za-z0-9-~]*)\\\\&{0,1})*)$");
            return pattern.matcher(value).matches();
        }
    }
}

