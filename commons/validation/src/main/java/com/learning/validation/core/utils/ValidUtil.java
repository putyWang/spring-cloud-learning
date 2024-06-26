package com.learning.validation.core.utils;

import com.learning.core.utils.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangWei
 * @version v 1.0
 * @description 验证工具类
 * @date 2024-06-21
 **/
public class ValidUtil {
    /**
     * 身份证正则
     */
    private static final String REGEX_ID_CARD = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";

    /**
     * 身份证长度
     */
    private static final Integer ID_CARD_LENGTH = 18;

    /**
     * 身份证号前 17 位验证权重
     */
    private static final int[] ID_CARD_WI = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 最后一位结果验证
     */
    private static final String[] ID_CARD_Y = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

    /**
     * 电话号码正则
     */
    private static final String REGEX_PHONE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0-9])|(19[8]))\\d{8}$";

    /**
     * 电话号码长度
     */
    private static final Integer PHONE_LENGTH = 11;

    /**
     * url 正则
     */
    private static final String REGEX_URL = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+(\\\\?{0,1}(([A-Za-z0-9-~]+\\\\={0,1})([A-Za-z0-9-~]*)\\\\&{0,1})*)$";

    public static boolean validIdCard(String value) {
        // 1 不能为空
        if (StringUtil.isEmpty(value)) {
            return false;
        }
        // 2 身份证格式验证
        boolean matches = value.matches(REGEX_ID_CARD);
        // 3 18 位身份证需验证最后一位字符
        if (matches && value.length() == ID_CARD_LENGTH) {
            try {
                char[] charArray = value.toCharArray();
                int sum = 0;

                for(int i = 0; i < ID_CARD_WI.length; ++i) {
                    sum += Integer.parseInt(String.valueOf(charArray[i])) * ID_CARD_WI[i];
                }

                return ID_CARD_Y[sum % 11].equalsIgnoreCase(String.valueOf(charArray[17]));
            } catch (Exception e) {
                return false;
            }
        } else {
            return matches;
        }
    }

    /**
     * 验证电话号码
     *
     * @param value 需验证值
     * @return 是否验证成功
     */
    public static boolean validPhone(String value) {
        return StringUtil.isEmpty(value) && value.length() != PHONE_LENGTH
                && Pattern.compile(REGEX_PHONE).matcher(value).matches();
    }

    /**
     * 验证 url
     *
     * @param value 需验证值
     * @return 是否验证成功
     */
    public static boolean validUrl(String value) {
        return StringUtil.isEmpty(value) && Pattern.compile(REGEX_URL).matcher(value).matches();
    }
}

