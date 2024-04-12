package com.learning.core.utils.date;

import com.learning.core.utils.StringUtils;
import com.learning.core.utils.Validate;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: wangwei
 * @Description:
 */
public class DateUtils {
    public static final String FORMAT_DEFAULT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DEFAULT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_ZONE = "GMT+8";
    public static SimpleDateFormat formatYMDSlash = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat formatYMSlash = new SimpleDateFormat("yyyy/MM");
    public static SimpleDateFormat formatMDY2Slash = new SimpleDateFormat("MM/dd/yy");
    public static SimpleDateFormat formatMDY4Slash = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat formatY_M_D = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat formatY_M = new SimpleDateFormat("yyyy-MM");
    public static SimpleDateFormat formatY = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat formatYMD = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat formatYM = new SimpleDateFormat("yyyyMM");
    public static SimpleDateFormat formatY_M_D_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat formatY_M_D_HM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat formatYMDHMS = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat formatY_M_D_HMSMILLS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat formatYMDChinese = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat formatYMDBlankHMSChinese = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    public static SimpleDateFormat formatYMDHMSChinese = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
    public static SimpleDateFormat formatYMDChineseBlankHMS = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    public static SimpleDateFormat formatYMDChineseHMS = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
    public static SimpleDateFormat formatYMDChineseBlankHM = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    public static SimpleDateFormat formatYMDChineseHM = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
    public static SimpleDateFormat formatYMChinese = new SimpleDateFormat("yyyy年MM月");
    public static SimpleDateFormat formatYChinese = new SimpleDateFormat("yyyy年");

    static {
        formatYMDSlash.setLenient(false);
        formatYMSlash.setLenient(false);
        formatMDY2Slash.setLenient(false);
        formatMDY4Slash.setLenient(false);
        formatY_M_D.setLenient(false);
        formatY_M.setLenient(false);
        formatY.setLenient(false);
        formatYMD.setLenient(false);
        formatYM.setLenient(false);
        formatY_M_D_HMS.setLenient(false);
        formatY_M_D_HM.setLenient(false);
        formatYMDHMS.setLenient(false);
        formatY_M_D_HMSMILLS.setLenient(false);
        formatYMDChinese.setLenient(false);
        formatYMDBlankHMSChinese.setLenient(false);
        formatYMDHMSChinese.setLenient(false);
        formatYMDChineseHMS.setLenient(false);
        formatYMDChineseBlankHMS.setLenient(false);
        formatYMDChineseHM.setLenient(false);
        formatYMDChineseBlankHM.setLenient(false);
        formatYMChinese.setLenient(false);
        formatYChinese.setLenient(false);
    }

    public DateUtils() {
    }

    public static Date now() {
        return new Date();
    }

    public static String getDateString(Date date) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(date);
        }
    }

    public static String getDateTimeString(Date date) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(date);
        }
    }

    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDate(Date date, String format) {
        return formatDate(date, format, TimeZone.getTimeZone("GMT+8"));
    }

    public static Date parse(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (dateStr != null && !"".equals(dateStr)) {
            try {
                return sdf.parse(dateStr);
            } catch (ParseException var4) {
                System.out.println("日期转换错误: " + var4.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public static String formatDate(Date date, String format, TimeZone timeZone) {
        if (null == date) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setTimeZone(timeZone);
            return dateFormat.format(date);
        }
    }

    public static LocalDateTime parseLocalDateTime(CharSequence date, String format) {
        return LocalDateTimeUtils.parse(date, format);
    }

    public static Date toDate(String date) throws ParseException {
        return toDate(date, "yyyy-MM-dd");
    }

    public static Date toDateTime(String date) throws ParseException {
        return toDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date toDate(String date, String format) throws ParseException {
        return toDate(date, format, TimeZone.getTimeZone("GMT+8"));
    }

    public static Date toDate(String date, String format, TimeZone timeZone) throws ParseException {
        if (StringUtils.isBlank(date)) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setTimeZone(timeZone);
            return dateFormat.parse(date);
        }
    }

    public static Instant toInstant(Date date) {
        return null == date ? null : date.toInstant();
    }

    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtils.toInstant(temporalAccessor);
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTimeUtils.of(instant);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTimeUtils.of(date);
    }

    public static Date add(Date date, long time, TimeUnit timeUnit) {
        if (null != date && time >= 0L && timeUnit != null) {
            int timeIntValue;
            if (time > 2147483647L) {
                timeIntValue = 2147483647;
            } else {
                timeIntValue = Long.valueOf(time).intValue();
            }

            Date result;
            switch (timeUnit) {
                case DAYS:
                    result = addDays(date, timeIntValue);
                    break;
                case HOURS:
                    result = addHours(date, timeIntValue);
                    break;
                case MINUTES:
                    result = addMinutes(date, timeIntValue);
                    break;
                case SECONDS:
                    result = addSeconds(date, timeIntValue);
                    break;
                case MILLISECONDS:
                    result = addMilliseconds(date, timeIntValue);
                    break;
                default:
                    result = date;
            }

            return result;
        } else {
            return null;
        }
    }

    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }

    public static Date getFutureDateTmdHms(int day) {
        if (day <= 0) {
            return parse("2099-01-01", "yyyy-MM-dd");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(6, calendar.get(6) + day);
            return calendar.getTime();
        }
    }

    private static Date add(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    private static void validateDateNotNull(Date date) {
        Validate.isTrue(date != null, "The date must not be null", new Object[0]);
    }

    public static long between(Date startDate, Date endDate, DateMsUnit msUnit) {
        return between(startDate, endDate, msUnit, true);
    }

    public static long between(Date startDate, Date endDate, DateMsUnit msUnit, boolean isAbs) {
        Validate.notNull(startDate, "start date must not null", new Object[0]);
        Validate.notNull(endDate, "end date must not null", new Object[0]);
        Validate.notNull(msUnit, "time unit must not null", new Object[0]);
        Date begin = startDate;
        Date end = endDate;
        if (isAbs && startDate.after(endDate)) {
            begin = endDate;
            end = startDate;
        }

        long diff = end.getTime() - begin.getTime();
        return diff / msUnit.getMillis();
    }

    public static Date getServerStartDate() {
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(startTime);
    }

    public static Calendar convertTo(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static boolean isAfter(Date date1, Date date2) {
        return null != date1 && null != date2 ? date1.after(date2) : false;
    }

    public static boolean isAfter(Date date) {
        return isAfter(date, now());
    }

    public static boolean isBefore(Date date1, Date date2) {
        return null != date1 && null != date2 ? date1.before(date2) : false;
    }

    public static boolean isBefore(Date date) {
        return isBefore(date, new Date());
    }

    public static String dateStr2DateStr(String dateStr) {
        String returnStr = dateStr;
        String dateTimeFromStr = getDateStrFromStr(dateStr);
        Date date = parseStringToDate(dateTimeFromStr);
        if (date != null) {
            returnStr = getDateFormat2(dateTimeFromStr).format(date);
        }

        return returnStr;
    }

    public static Date parseStringToDate(String dateStr) {
        if (dateStr != null && !"".equals(dateStr.trim())) {
            dateStr = dateStr.trim();
            Date date = null;

            try {
                if (dateStr.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                    date = formatYMDSlash.parse(dateStr);
                } else if (dateStr.matches("\\d{4}/\\d{1,2}")) {
                    date = formatYMSlash.parse(dateStr);
                } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                    date = formatY_M_D.parse(dateStr);
                } else if (dateStr.matches("\\d{4}-\\d{1,2}")) {
                    date = formatY_M.parse(dateStr);
                } else if (dateStr.matches("\\d{4}")) {
                    date = formatY.parse(dateStr);
                } else if (dateStr.matches("\\d{6,8}")) {
                    try {
                        date = formatYMD.parse(dateStr);
                    } catch (Exception var3) {
                        date = formatYM.parse(dateStr);
                    }
                } else if (dateStr.matches("\\d{5,6}")) {
                    date = formatYM.parse(dateStr);
                } else if (dateStr.matches("\\d{9,14}")) {
                    date = formatYMDHMS.parse(dateStr);
                } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}")) {
                    date = formatY_M_D_HM.parse(dateStr);
                } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                    date = formatY_M_D_HMS.parse(dateStr);
                } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3}")) {
                    date = formatY_M_D_HMSMILLS.parse(dateStr);
                } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
                    date = formatMDY2Slash.parse(dateStr);
                } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                    date = formatMDY4Slash.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日")) {
                    date = formatYMDChinese.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒")) {
                    date = formatYMDHMSChinese.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d{1,2}秒")) {
                    date = formatYMDBlankHMSChinese.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                    date = formatYMDChineseHMS.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                    date = formatYMDChineseBlankHMS.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}")) {
                    date = formatYMDChineseHM.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}")) {
                    date = formatYMDChineseBlankHM.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年\\d{2}月")) {
                    date = formatYMChinese.parse(dateStr);
                } else if (dateStr.matches("\\d{4}年")) {
                    date = formatYChinese.parse(dateStr);
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return date;
        } else {
            return null;
        }
    }

    public static String getDateStrFromStr(String str) {
        if (str == null) {
            return null;
        } else {
            String returnStr = "";

            try {
                Pattern ymdSlashPattern = Pattern.compile("(\\d{4}/\\d{1,2}/\\d{1,2})");
                Matcher matcher = ymdSlashPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymSlashPattern = Pattern.compile("(\\d{4}/\\d{1,2})");
                matcher = ymSlashPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern mdy4SlashPattern = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4})");
                matcher = mdy4SlashPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern mdy2SlashPattern = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{2})");
                matcher = mdy2SlashPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern y_m_d_h_m_s_SPattern = Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3})");
                matcher = y_m_d_h_m_s_SPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern y_m_d_h_m_sPattern = Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2})");
                matcher = y_m_d_h_m_sPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern y_m_d_h_mPattern = Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2})");
                matcher = y_m_d_h_mPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern y_m_dPattern = Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2})");
                matcher = y_m_dPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern y_mPattern = Pattern.compile("(\\d{4}-\\d{1,2})");
                matcher = y_mPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdblankhmschinesePattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d{1,2}秒)");
                matcher = ymdblankhmschinesePattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdhmschinesePattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒)");
                matcher = ymdhmschinesePattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdchineseblankhmsPattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d{1,2})");
                matcher = ymdchineseblankhmsPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdchineseblankhmPattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2})");
                matcher = ymdchineseblankhmPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdchinesehmsPattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2})");
                matcher = ymdchinesehmsPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdchinesehmPattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2})");
                matcher = ymdchinesehmPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdchinesePattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日)");
                matcher = ymdchinesePattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymchinesePattern = Pattern.compile("(\\d{4}年\\d{2}月)");
                matcher = ymchinesePattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ychinesePattern = Pattern.compile("(\\d{4}年)");
                matcher = ychinesePattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdhmsPattern = Pattern.compile("(\\d{9,14})");
                matcher = ymdhmsPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymdPattern = Pattern.compile("(\\d{6,8})");
                matcher = ymdPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern ymPattern = Pattern.compile("(\\d{5,6})");
                matcher = ymPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }

                Pattern yPattern = Pattern.compile("(\\d{4})");
                matcher = yPattern.matcher(str);
                if (matcher.find()) {
                    returnStr = matcher.group();
                    return returnStr;
                }
            } catch (Exception var25) {
                var25.printStackTrace();
            }

            return returnStr;
        }
    }

    public static SimpleDateFormat getDateFormat(String dateStr) {
        if (dateStr == null) {
            return null;
        } else if (dateStr.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
            return formatYMDSlash;
        } else if (dateStr.matches("\\d{4}/\\d{1,2}")) {
            return formatYMSlash;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}")) {
            return formatY_M;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            return formatY_M_D;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}")) {
            return formatY_M;
        } else if (dateStr.matches("\\d{4}")) {
            return formatY;
        } else if (dateStr.matches("\\d{6,8}")) {
            if (dateStr.length() == 6) {
                return !"0".equals(String.valueOf(dateStr.charAt(4))) ? formatYMD : formatYM;
            } else {
                return formatYMD;
            }
        } else if (dateStr.matches("\\d{5,6}")) {
            return formatYM;
        } else if (dateStr.matches("\\d{9,14}")) {
            return formatYMDHMS;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HM;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3}")) {
            return formatY_M_D_HMSMILLS;
        } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
            return formatMDY2Slash;
        } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            return formatMDY4Slash;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日")) {
            return formatYMDChinese;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒")) {
            return formatYMDHMSChinese;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d{1,2}秒")) {
            return formatYMDBlankHMSChinese;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            return formatYMDChineseHMS;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            return formatYMDChineseBlankHMS;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}")) {
            return formatYMDChineseHM;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}")) {
            return formatYMDChineseBlankHM;
        } else if (dateStr.matches("\\d{4}年\\d{2}月")) {
            return formatYMChinese;
        } else {
            return dateStr.matches("\\d{4}年") ? formatYChinese : null;
        }
    }

    public static SimpleDateFormat getDateFormat2(String dateStr) {
        if (dateStr == null) {
            return null;
        } else if (dateStr.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
            return formatY_M_D;
        } else if (dateStr.matches("\\d{4}/\\d{1,2}")) {
            return formatY_M;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}")) {
            return formatY_M;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            return formatY_M_D;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}")) {
            return formatY_M;
        } else if (dateStr.matches("\\d{4}")) {
            return formatY;
        } else if (dateStr.matches("\\d{6,8}")) {
            if (dateStr.length() == 6) {
                return !"0".equals(String.valueOf(dateStr.charAt(4))) ? formatY_M_D : formatY_M;
            } else {
                return formatY_M_D;
            }
        } else if (dateStr.matches("\\d{5,6}")) {
            return formatY_M_D;
        } else if (dateStr.matches("\\d{9,14}")) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HM;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3}")) {
            return formatY_M_D_HMSMILLS;
        } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{2}")) {
            return formatY_M_D;
        } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            return formatY_M_D;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日")) {
            return formatY_M_D;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒")) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d{1,2}秒")) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HM;
        } else if (dateStr.matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}")) {
            return formatY_M_D_HM;
        } else if (dateStr.matches("\\d{4}年\\d{2}月")) {
            return formatY_M;
        } else {
            return dateStr.matches("\\d{4}年") ? formatY : null;
        }
    }

    public static int getAgeFromDateStr(String birthTimeString) {
        String[] strs = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(1);
        int monthNow = cal.get(2) + 1;
        int dayNow = cal.get(5);
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;
        int age = yearMinus;
        if (yearMinus < 0) {
            age = 0;
        } else if (yearMinus == 0) {
            if (monthMinus < 0) {
                age = 0;
            } else if (monthMinus == 0) {
                if (dayMinus < 0) {
                    age = 0;
                } else if (dayMinus >= 0) {
                    age = 1;
                }
            } else if (monthMinus > 0) {
                age = 1;
            }
        } else if (yearMinus > 0 && monthMinus >= 0) {
            if (monthMinus == 0) {
                if (dayMinus >= 0 && dayMinus >= 0) {
                    age = yearMinus + 1;
                }
            } else if (monthMinus > 0) {
                age = yearMinus + 1;
            }
        }

        return age;
    }
}
