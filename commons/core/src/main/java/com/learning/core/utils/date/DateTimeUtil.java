package com.learning.core.utils.date;

import com.learning.core.exception.LearningException;
import com.learning.core.utils.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 时间日期类
 */
public class DateTimeUtil {
    private static final String dateRegex = "\\d{4}-\\d{2}-\\d{2}";
    private static final String timeRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    public DateTimeUtil() {
    }

    /**
     * 获取字符串类型的当前时间
     *
     * @return
     */
    public static String getNowTimeStr() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 将字符串类型的时间转化为Date对象
     *
     * @param timeStr 时间字符串
     * @return
     * @throws ParseException
     */
    public static Date getByTimeStr(String timeStr)
            throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return dateFormat.parse(timeStr);
    }

    /**
     * 获取yyyyMMddHHmmss样式的字符串时间
     *
     * @return
     */
    public static String getNoSeparNowTimeStr() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    /**
     * 获取字符串类型的当前日期
     *
     * @return
     */
    public static String getNowDateStr() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 将yyyy-MM-dd样式的当前日期对象转化为字符串
     *
     * @param date 日期对象
     * @return
     */
    public static String getDateStrByLocalDate(LocalDate date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 将日期类型转化为yyyy-MM-dd HH:mm:ss格式
     *
     * @param date 时间
     * @return
     */
    public static String getDataStrByDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 将日期字符串转换为日期字符串
     *
     * @param str 日期字符串或者时间字符串
     * @return
     */
    public static String getDataStrByStr(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = getDateByStr(str);
        return date == null ? "" : dateFormat.format(date);
    }

    /**
     * 解析日期字符串为yyyy-MM-dd格式日期对象
     *
     * @param timeStr 日期字符串
     * @return
     */
    public static Date getByDateStr(String timeStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return dateFormat.parse(timeStr);
        } catch (ParseException e) {
            throw new LearningException("日期解析错误");
        }
    }

    /**
     * 将时间字符串解析为对应的日期对象
     *
     * @param str 日期字符串
     * @return
     */
    public static Date getDateByStr(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        if (!StringUtil.isEmpty(str)) {
            try {
                if (Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", str)) {
                    date = timeFormat.parse(str);
                } else {
                    if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", str)) {
                        throw new LearningException("日期格式不正确");
                    }

                    date = dateFormat.parse(str);
                }
            } catch (ParseException e) {
                throw new LearningException("日期解析错误");
            }
        }

        return date;
    }

    /**
     * 将时间字符串解析为对应的日期字符串
     *
     * @param str 时间日期字符串
     * @return
     */
    public static String getTimeStampByStr(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";
        if (!StringUtil.isEmpty(str)) {
            try {
                if (Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", str)) {
                    timestamp = (new Long(timeFormat.parse(str).getTime())).toString();
                } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", str)) {
                    timestamp = (new Long(dateFormat.parse(str).getTime())).toString();
                } else {
                    timestamp = str;
                }
            } catch (ParseException e) {
                throw new LearningException("日期解析错误");
            }
        }

        return timestamp;
    }

    /**
     * 获取两个时间之间差距天数
     *
     * @param start 开始日期
     * @param end   截止日期
     * @return
     * @throws ParseException
     */
    public static int getDaysBetweenTwo(String start, String end) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long date1 = simpleDateFormat.parse(start).getTime();
        long date2 = simpleDateFormat.parse(end).getTime();
        return (int) ((date2 - date1) / 86400000L);
    }

    /**
     * 根据生日获取年纪
     *
     * @param birthDay 出生日期
     * @return
     */
    public static Integer getAge(Date birthDay) {
        return getAge(birthDay, null);
    }

    /**
     * 根据出生日期与结束日期获取结束日期时年纪
     *
     * @param birthDay 出生日期
     * @param endDate  目标日期
     * @return
     */
    public static Integer getAge(Date birthDay, Date endDate) {
        if (birthDay == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            //设置日期
            if (endDate != null) {
                cal.setTime(endDate);
            }

            if (cal.before(birthDay)) {
                throw new LearningException("出生日期晚于计算日期");
            } else {
                //设置结束日期
                int yearNow = cal.get(Calendar.YEAR);
                int monthNow = cal.get(Calendar.MONTH);
                int dayOfMonthNow = cal.get(Calendar.DATE);
                //设置出生日期
                cal.setTime(birthDay);
                int yearBirth = cal.get(Calendar.YEAR);
                int monthBirth = cal.get(Calendar.MONTH);
                int dayOfMonthBirth = cal.get(Calendar.DATE);
                //计算年纪
                int age = yearNow - yearBirth;
                if (monthNow <= monthBirth) {
                    if (monthNow == monthBirth) {
                        if (dayOfMonthNow < dayOfMonthBirth) {
                            --age;
                        }
                    } else {
                        --age;
                    }
                }

                return age;
            }
        }
    }

    public static String dealDateFormat(String oldDate) {
        DateFormat df2 = null;
        Date date = null;

        try {
            oldDate = oldDate.replace("Z", " UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            date = df.parse(oldDate);
            df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (ParseException var4) {
            var4.printStackTrace();
        }

        return df2 == null ? "" : df2.format(date);
    }

    public static String dealDateFormatRevese(String oldDate) {
        DateFormat df = null;
        DateFormat df2 = null;
        Date date = null;

        try {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = df.parse(oldDate);
            df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return df2 == null ? "" : df2.format(date);
    }
}
