package com.learning.Job.schedule.core.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    private static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
    private static final String COOKIE_PATH = "/";

    public CookieUtil() {
    }

    public static void set(HttpServletResponse response, String key, String value, boolean ifRemember) {
        int age = ifRemember ? Integer.MAX_VALUE : -1;
        set(response, key, value, (String)null, "/", age, true);
    }

    private static void set(HttpServletResponse response, String key, String value, String domain, String path, int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }

        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        response.addCookie(cookie);
    }

    public static String getValue(HttpServletRequest request, String key) {
        Cookie cookie = get(request, key);
        return cookie != null ? cookie.getValue() : null;
    }

    private static Cookie get(HttpServletRequest request, String key) {
        Cookie[] arr_cookie = request.getCookies();
        if (arr_cookie != null && arr_cookie.length > 0) {
            Cookie[] var3 = arr_cookie;
            int var4 = arr_cookie.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Cookie cookie = var3[var5];
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public static void remove(HttpServletRequest request, HttpServletResponse response, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            set(response, key, "", (String)null, "/", 0, true);
        }

    }
}
