package com.learning.job.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static ThreadLocal<Map<String, DateFormat>> dateFormatThreadLocal = new ThreadLocal();

    public DateUtil() {
    }

    private static DateFormat getDateFormat(String pattern) {
        if (pattern != null && pattern.trim().length() != 0) {
            Map<String, DateFormat> dateFormatMap = (Map)dateFormatThreadLocal.get();
            if (dateFormatMap != null && ((Map)dateFormatMap).containsKey(pattern)) {
                return (DateFormat)((Map)dateFormatMap).get(pattern);
            } else {
                synchronized(dateFormatThreadLocal) {
                    if (dateFormatMap == null) {
                        dateFormatMap = new HashMap();
                    }

                    ((Map)dateFormatMap).put(pattern, new SimpleDateFormat(pattern));
                    dateFormatThreadLocal.set(dateFormatMap);
                }

                return (DateFormat)((Map)dateFormatMap).get(pattern);
            }
        } else {
            throw new IllegalArgumentException("pattern cannot be empty.");
        }
    }

    public static String formatDate(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    public static String formatDateTime(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String patten) {
        return getDateFormat(patten).format(date);
    }

    public static Date parseDate(String dateString) {
        return parse(dateString, "yyyy-MM-dd");
    }

    public static Date parseDateTime(String dateString) {
        return parse(dateString, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parse(String dateString, String pattern) {
        try {
            Date date = getDateFormat(pattern).parse(dateString);
            return date;
        } catch (Exception var3) {
            Exception e = var3;
            logger.warn("parse date error, dateString = {}, pattern={}; errorMsg = ", new Object[]{dateString, pattern, e.getMessage()});
            return null;
        }
    }

    public static Date addDays(final Date date, final int amount) {
        return add(date, 5, amount);
    }

    public static Date addYears(final Date date, final int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(final Date date, final int amount) {
        return add(date, 2, amount);
    }

    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }
}
