package com.learning.core.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 字符串工具类
 */
public final class StringUtil {
    public static final String EMPTY_STRING = "";
    public static final char DEFAULT_DELIMITER_CHAR = ',';
    public static final char DEFAULT_QUOTE_CHAR = '"';
    public static final String COMMA = ",";
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public StringUtil() {
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isBlank(CharSequence str) {

        if (null != str) {
            for (int i = 0; i < str.length(); ++i) {
                if (!CharacterUtils.isBlank(str.charAt(i))) {

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断字符串是否由数字组成
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(CharSequence str) {

        if (null != str) {
            for (int i = 0; i < str.length(); ++i) {
                if (!Character.isDigit(str.charAt(i))) {

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 判断CharSequence是否存在空对象
     *
     * @param css
     * @return
     */
    public static boolean isNoneBlank(CharSequence... css) {
        return org.apache.commons.lang3.StringUtils.isNoneBlank(css);
    }

    /**
     * 判断两个字符串是否相等（不忽略大小写）
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean equals(CharSequence s1, CharSequence s2) {
        return equals(s1, s2, false);
    }

    /**
     * 判断两个字符串是否相等（ignoreCase表示是否忽略大小写）
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean equals(CharSequence s1, CharSequence s2, boolean ignoreCase) {
        if (null == s1) {
            return null == s2;
        } else if (null == s2) {
            return false;
        } else {
            return ignoreCase ? s1.toString().equalsIgnoreCase(s2.toString()) : s1.toString().contentEquals(s2);
        }
    }

    /**
     * 去除字符串两端空格
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 去除字符串前部空格
     *
     * @param value
     * @return
     */
    public static String trimStart(CharSequence value) {
        return trim(value, -1);
    }

    /**
     * 去除字符串后端空格
     *
     * @param value
     * @return
     */
    public static String trimEnd(CharSequence value) {
        return trim(value, 1);
    }

    /**
     * 去除字符串两端空格
     *
     * @param value
     * @return
     */
    public static String trimAll(CharSequence value) {
        return trim(value, 0);
    }

    /**
     * 去除字符串端头空格（mode <= 0时去除前端，mode >= 0时去除后端）
     *
     * @param value
     * @return
     */
    public static String trim(CharSequence value, int mode) {
        if (value == null) {
            return "";
        } else {
            int len = value.length();
            int start = 0;
            int end = len;
            if (mode <= 0) {
                while (start < end && CharacterUtils.isBlank(value.charAt(start))) {
                    ++start;
                }
            }

            if (mode >= 0) {
                while (start < end && CharacterUtils.isBlank(value.charAt(end - 1))) {
                    --end;
                }
            }

            return start <= 0 && end >= len ? value.toString() : value.toString().substring(start, end);
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || 0 == str.length();
    }

    /**
     * 判断字符串是否包含v
     *
     * @param str
     * @return
     */
    public static boolean contains(String str, char v) {
        if (isEmpty(str)) {
            return false;
        } else {
            return str.indexOf(v) > -1;
        }
    }

    /**
     * 判断字符串是否包含字符串searchStr
     *
     * @param str
     * @param searchStr
     * @return
     */
    public static boolean contains(String str, String searchStr) {
        return isEmpty(str) && str.contains(searchStr);
    }

    /**
     * 判断strs中是否包含str（忽略大小写）
     *
     * @param str
     * @param strs
     * @return
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {

            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断str是否以suffix结尾（忽略大小写）
     *
     * @param str
     * @param suffix
     * @return
     */
    public static boolean endWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, true);
    }

    /**
     * 判断str是否以c结尾
     *
     * @param str
     * @param c
     * @return
     */
    public static boolean endWith(CharSequence str, char c) {
        return c == str.charAt(str.length() - 1);
    }

    /**
     * 判断str是否以suffix结尾（isIgnoreCase表示是否忽略大小写）
     *
     * @param str
     * @param suffix
     * @return
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean isIgnoreCase) {
        if (null != str && null != suffix) {
            return isIgnoreCase ? str.toString().toLowerCase().endsWith(suffix.toString().toLowerCase()) : str.toString().endsWith(suffix.toString());
        } else {
            return false;
        }
    }

    /**
     * 保证string后缀为suffix且只出现一次
     *
     * @param string
     * @param suffix
     * @return
     */
    public static String ensureSuffix(String string, String suffix) {
        return !isEmpty(string) && !isEmpty(suffix) ? removeEnd(string, suffix) + suffix : string;
    }

    /**
     * 若str以remove结尾，移除remove，其余情况不变
     *
     * @param str
     * @param remove
     * @return
     */
    public static String removeEnd(String str, String remove) {
        if (!isEmpty(str) && !isEmpty(remove)) {
            return str.endsWith(remove) ? str.substring(0, str.length() - remove.length()) : str;
        } else {
            return str;
        }
    }


    /**
     * 将逗号拼接字符串转List
     *
     * @param str
     * @return
     */
    public static List<String> getListByStr(String str) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList<>();
        }

        return Arrays.asList(str.split(","));
    }


    /**
     * 若str不包含appendStr，将appendStr接到str尾部，否则将otherwise接到str尾部
     *
     * @param str
     * @param appendStr
     * @param otherwise
     * @return
     */
    public static String appendIfNotContain(String str, String appendStr, String otherwise) {
        if (!isEmpty(str) && !isEmpty(appendStr)) {
            return str.contains(appendStr) ? str.concat(otherwise) : str.concat(appendStr);
        } else {
            return str;
        }
    }

    /**
     * 若str不以suffix结尾，将suffix接到str尾部
     *
     * @param str
     * @param suffix
     * @param appendStr
     * @return
     */
    public static String appendIfNotEndsWith(String str, String suffix, String appendStr) {
        return endWithIgnoreCase(str, suffix) ? str.concat(appendStr) : str;
    }

    /**
     * 将拼接到collection中的String中
     *
     * @param collection
     * @param str
     * @return
     */
    public static String join(Collection<String> collection, String str) {
        StringBuffer stringBuffer = new StringBuffer();

        for (Iterator<String> it = collection.iterator(); it.hasNext(); stringBuffer.append((String) it.next())) {
            if (stringBuffer.length() != 0) {
                stringBuffer.append(str);
            }
        }

        return stringBuffer.toString();
    }

    /**
     * 拼接字符数组中的所有元素
     *
     * @param elements
     * @return
     */
    public static String join(CharSequence... elements) {
        Validate.notEmpty(elements, "element not empty", new Object[0]);
        StringBuilder sb = new StringBuilder(elements.length);

        for (CharSequence element : elements) {
            sb.append(element);
        }

        return sb.toString();
    }

    /**
     * 判断jsonStr是否为合法json字符串
     *
     * @param jsonStr
     * @return
     */
    public static boolean checkValidJsonObjectStr(String jsonStr) {
        boolean flag = false;
        if (jsonStr != null && !"null".equals(jsonStr)) {
            try {
                JSONObject.parseObject(jsonStr);
                flag = true;
            } catch (Exception var3) {
                logger.info("illegal json string: " + jsonStr);
            }

            return flag;
        } else {
            return false;
        }
    }

    /**
     * 判断jsonStr是否为合法数组json字符串
     *
     * @param jsonStr
     * @return
     */
    public static boolean checkValidJsonArrayStr(String jsonStr) {
        boolean flag = false;

        try {
            JSONArray.parseArray(jsonStr);
            flag = true;
        } catch (Exception var3) {
            logger.info("illegal json string: " + jsonStr);
        }

        return flag;
    }

    /**
     * 为特殊字符添加转义
     *
     * @param str
     * @return
     */
    public static String addEscape(String str) {
        str = str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
        return str;
    }

    /**
     * @param jsonObject
     * @return
     */
    public static JSONObject transformValue(String jsonObject) {
        JSONObject returnJsonObject = null;

        try {
            JSONObject first = JSONObject.parseObject(jsonObject);
            returnJsonObject = valueToStringValue(first);
        } catch (Exception var3) {
            logger.error("transform json object " + jsonObject + "value to string exception!", var3);
        }

        return returnJsonObject;
    }

    /**
     * 替换str中相关子字符串
     *
     * @param str       原字符串
     * @param replaced  被替换的子串
     * @param replacing 用于替换的子串
     * @return 替换后的字符串
     */
    public static String replace(String str, String replaced, String replacing) {

        return org.apache.commons.lang3.StringUtils.replace(str, replaced, replacing);
    }

    public static JSONObject valueToStringValue(JSONObject jsonObject) {
        Iterator keys = jsonObject.keySet().iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jsonObject.getString(key).trim();

            try {
                if (value.indexOf("{") == 0) {
                    JSONObject second = JSONObject.parseObject(value);
                    jsonObject.put(key, valueToStringValue(second));
                } else if (value.indexOf("[") != 0) {
                    jsonObject.put(key, value);
                } else {
                    JSONArray jsonArray = JSONArray.parseArray(value);
                    JSONArray transformJsonArray = new JSONArray();

                    for (int i = 0; i < jsonArray.size(); ++i) {
                        JSONObject second = jsonArray.getJSONObject(i);
                        JSONObject transSecond = valueToStringValue(second);
                        transformJsonArray.add(transSecond);
                    }

                    jsonObject.put(key, transformJsonArray);
                }
            } catch (Exception var9) {
                jsonObject.put(key, value);
            }
        }

        return jsonObject;
    }

    /**
     * 通过数字型字符串value和乘法对key进行深化处理
     *
     * @param key
     * @param value
     * @return
     */
    public static String expandValueByKey(String key, String value) {
        String returnValue = value;

        try {
            String md5 = MD5Utils.strToMd5_32(key).toLowerCase();
            int multiple = md5.charAt(md5.length() - 1) % 10 + 1;
            if (value == null || value.length() == 0) {
                return returnValue;
            }
            //判断字符串是否与"-?\d+\.\d+"匹配(-可能出现，一次数字（0-9），. ，数字（0-9）至少出现一次:小数)
            if (value.matches("-?\\d+\\.\\d+")) {
                returnValue = String.valueOf(Float.parseFloat(value) * (float) multiple);
            }
            //判断字符串是否与"-?\d+"匹配(-可能出现一次，数字（0-9）至少出现一次:整数)
            else if (value.matches("-?\\d+")) {
                returnValue = String.valueOf(Integer.parseInt(value) * multiple);
            }
        } catch (Exception e) {
            logger.error("key is " + key + ", value is " + value + ", expand value exception!", e);
        }

        return returnValue;
    }

    /**
     * 通过数字型字符串value和除法对key进行深化处理
     *
     * @param key
     * @param value
     * @return
     */
    public static String reduceValueByKey(String key, String value) {
        String returnValue = "";

        try {
            String md5 = MD5Utils.strToMd5_32(key).toLowerCase();
            int multiple = md5.charAt(md5.length() - 1) % 10 + 1;
            if (value == null || value.length() == 0) {
                return returnValue;
            }
            //判断字符串是否与"-?\d+\.\d+"匹配(-可能出现，一次数字（0-9），. ，数字（0-9）至少出现一次:小数)
            if (value.matches("-?\\d+\\.\\d+")) {
                returnValue = String.valueOf(Float.parseFloat(value) / (float) multiple);
            }
            //判断字符串是否与"-?\d+"匹配(-可能出现一次，数字（0-9）至少出现一次:整数)
            else if (value.matches("-?\\d+")) {
                returnValue = String.valueOf(Integer.parseInt(value) / multiple);
            }
        } catch (Exception var5) {
            logger.error("key is " + key + ", value is " + value + ", reduce value exception!", var5);
        }

        return returnValue;
    }

    /**
     * 替换jsonObject中的key
     *
     * @param jsonObject
     * @param oriKey
     * @param desKey
     */
    public static void replaceJsonKey(JSONObject jsonObject, String oriKey, String desKey) {
        if (jsonObject != null) {
            try {
                if (jsonObject.containsKey(oriKey)) {
                    String oriValue = jsonObject.getString(oriKey);
                    jsonObject.put(desKey, "null".equals(oriValue) ? "" : oriValue);
                    if (!oriKey.equals(desKey)) {
                        jsonObject.remove(oriKey);
                    }
                }
            } catch (Exception var4) {
                logger.error("exception: " + var4 + ", 替换JSONObject中第一层的key失败：" + jsonObject + ", " + oriKey + ", " + desKey, var4);
            }

        }
    }

    /**
     * 替换map中的key
     *
     * @param map
     * @param oriKey
     * @param desKey
     */
    public static void replaceMapKey(Map<String, Object> map, String oriKey, String desKey) {
        try {
            if (map.containsKey(oriKey)) {
                Object value = map.get(oriKey);
                map.put(desKey, "null".equals(value) ? "" : value);
                if (!oriKey.equals(desKey)) {
                    map.remove(oriKey);
                }
            }
        } catch (Exception var4) {
            logger.error("替换map中的key失败：" + map + ", " + oriKey + ", " + desKey, var4);
        }

    }

    public static Map<String, Object> removeNullValueOfMap(Map<String, Object> map) {
        HashMap<String, Object> returnMap = new HashMap();
        Iterator var2 = map.entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) var2.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                returnMap.put(key, value);
            }
        }

        return returnMap;
    }

    public static String getKeyOfJson(int level, JSONObject json, String someKey) {
        String someValue = null;

        try {
            if (level >= 1) {
                for (int i = 1; i <= level; ++i) {
                    if (i == level) {
                        someValue = json.getString(someKey);
                        return someValue;
                    }

                    String[] strings = iterateJson(json);
                    String[] var6 = strings;
                    int var7 = strings.length;
                    int var8 = 0;

                    while (var8 < var7) {
                        String string = var6[var8];

                        try {
                            JSONObject jsonObject = JSONObject.parseObject(string);
                            someValue = getKeyOfJson(level - 1, jsonObject, someKey);
                            return someValue;
                        } catch (Exception var11) {
                            ++var8;
                        }
                    }
                }
            }
        } catch (Exception var12) {
            logger.error(json + "中第" + level + "层无tid！", var12);
        }

        return someValue;
    }

    public static String[] iterateJson(JSONObject jsonObject) {
        ArrayList<String> list = new ArrayList();
        if (jsonObject != null) {
            Iterator keys = jsonObject.keySet().iterator();

            try {
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    String value = jsonObject.getString(key);
                    list.add(value);
                }
            } catch (Exception var5) {
                logger.error("get json object " + jsonObject + " key's value exception!", var5);
            }
        }

        return (String[]) list.toArray(new String[0]);
    }

    public static String etlJsonStr(String jsonStr) {
        if (jsonStr == null) {
            return null;
        } else {
            String returnStr = jsonStr;

            try {
                if (checkValidJsonObjectStr(jsonStr)) {
                    JSONObject returnJsonObject = JSONObject.parseObject(jsonStr);
                    etlJsonObject(returnJsonObject);
                    returnStr = returnJsonObject.toString();
                } else if (!checkValidJsonArrayStr(jsonStr)) {
                    returnStr = CharacterUtils.qj2bjCharNumber(jsonStr);
                } else {
                    JSONArray returnJsonArray = JSONArray.parseArray(jsonStr);

                    for (int i = 0; i < returnJsonArray.size(); ++i) {
                        returnJsonArray.add(i, etlJsonStr(returnJsonArray.getString(i)));
                    }

                    returnStr = returnJsonArray.toString();
                }

                if ("null".equals(returnStr)) {
                    returnStr = "";
                }
            } catch (Exception var4) {
                logger.error("parse json string " + jsonStr + " exception!", var4);
            }

            return returnStr;
        }
    }

    public static void etlJsonObject(JSONObject jsonObject) {
        Iterator var1 = jsonObject.keySet().iterator();

        while (true) {
            while (var1.hasNext()) {
                String s = (String) var1.next();
                String key = CharacterUtils.qj2bjCharNumber(s);
                String value = CharacterUtils.qj2bjCharNumber(jsonObject.getString(key));
                if ("null".equals(value)) {
                    value = "";
                    jsonObject.put(key, value);
                } else if (checkValidJsonObjectStr(value)) {
                    jsonObject.put(key, etlJsonStr(value));
                } else if (!checkValidJsonArrayStr(value)) {
                    jsonObject.put(key, value);
                } else {
                    JSONArray jsonArray = JSONArray.parseArray(value);

                    for (int i = 0; i < jsonArray.size(); ++i) {
                        jsonArray.set(i, etlJsonStr(jsonArray.getString(i)));
                    }

                    jsonObject.put(key, jsonArray);
                }
            }

            return;
        }
    }

    public static HashMap<String, String> iterateJsonMap(JSONObject jsonObject) {
        HashMap<String, String> returnMap = new HashMap();
        if (jsonObject != null) {
            Iterator keys = jsonObject.keySet().iterator();

            try {
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    String value = jsonObject.getString(key);
                    returnMap.put(key, "null".equals(value) ? "" : value);
                }
            } catch (Exception var5) {
                logger.error("json object" + jsonObject + " to hash map exception!", var5);
            }
        }

        return returnMap;
    }

    public static JSONObject getNeedColumn(JSONObject jsonObject, String... params) {
        JSONObject newJsonObject = new JSONObject();
        String[] var3 = params;
        int var4 = params.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String columnName = var3[var5];
            newJsonObject.put(columnName, jsonObject.getString(columnName));
        }

        return newJsonObject;
    }

    public static String jsonStrKeyCamelNamed(String jsonStr) {
        JSONObject returnJson = new JSONObject();

        String returnStr;
        try {
            String value;
            if (checkValidJsonObjectStr(jsonStr)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                Iterator var4 = jsonObject.keySet().iterator();

                while (true) {
                    while (var4.hasNext()) {
                        String key = (String) var4.next();
                        value = jsonObject.getString(key);
                        String camelNamedKey = camelNamed(key);
                        if (checkValidJsonObjectStr(value)) {
                            String s = jsonStrKeyCamelNamed(value);
                            returnJson.put(camelNamedKey, s);
                        } else if (!checkValidJsonArrayStr(value)) {
                            returnJson.put(camelNamedKey, value);
                        } else {
                            JSONArray jsonArray = JSONArray.parseArray(value);

                            for (int i = 0; i < jsonArray.size(); ++i) {
                                String s = jsonStrKeyCamelNamed(jsonArray.getString(i));
                                jsonArray.set(i, s);
                            }

                            returnJson.put(camelNamedKey, jsonArray);
                        }
                    }

                    returnStr = returnJson.toString();
                    break;
                }
            } else if (checkValidJsonArrayStr(jsonStr)) {
                JSONArray jsonArray = JSONArray.parseArray(jsonStr);

                for (int i = 0; i < jsonArray.size(); ++i) {
                    JSONObject jsonObject = JSONObject.parseObject(jsonArray.getString(i));
                    value = jsonStrKeyCamelNamed(jsonObject.toString());
                    jsonArray.set(i, value);
                }

                returnStr = jsonArray.toString();
            } else {
                returnStr = jsonStr;
            }
        } catch (Exception var11) {
            logger.error(jsonStr + ", json string key camel named exception!", var11);
            returnStr = jsonStr;
        }

        return returnStr;
    }

    public static String camelNamed(String key) {
        StringBuilder sb = new StringBuilder("");

        try {
            if (key == null || !key.contains("_")) {
                return key;
            }

            String[] splits = key.split("_");
            sb.append(splits[0].substring(0, 1).toLowerCase());
            sb.append(splits[0].substring(1));

            for (int i = 1; i < splits.length; ++i) {
                sb.append(splits[i].substring(0, 1).toUpperCase());
                sb.append(splits[i].substring(1));
            }
        } catch (Exception var4) {
            logger.error(key + " get camel named exception!", var4);
        }

        return sb.toString();
    }

    public static String substring(final String str, int start, int end) {
        return org.apache.commons.lang3.StringUtils.substring(str, start, end);
    }
}

