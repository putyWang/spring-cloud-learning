package com.learning.core.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommonUtils {
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    public static Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");
    private static String SLAT = "354816d26912441ab280f08831c38453";

    private CommonUtils() {
    }

    public static boolean matchPath(List<String> list, String path) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        } else if (!list.contains(path)) {
            Optional<String> any = list.stream().filter((s) -> antPathMatcher.match(s, path)).findAny();
            return any.isPresent();
        } else {
            return true;
        }
    }

    public static String getPassKey(long id) {
        return DigestUtils.md5DigestAsHex((SLAT + "_" + id).getBytes(StandardCharsets.UTF_8));
    }

    public static String UUID() {
        return UUID.randomUUID().toString().replaceAll("\\-", "");
    }

    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    public static String formatDate(Date date, String pattern) {
        return (new SimpleDateFormat(pattern)).format(date);
    }

    public static String toJSONString(Object object) {
        return JSONObject.toJSONString(object);
    }

    public static String replaceFormatString(String source, Map<String, Object> map) {
        Matcher matcher = pattern.matcher(source);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String variable = matcher.group(1);
            Object value = map.get(variable);
            if (value != null) {
                matcher.appendReplacement(sb, String.valueOf(value));
            }
        }

        return sb.toString();
    }

    public static String replaceFormatString(String source, Object param) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(param));
        return replaceFormatString(source, jsonObject);
    }
}

