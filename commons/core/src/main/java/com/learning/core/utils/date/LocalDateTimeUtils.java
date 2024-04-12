package com.learning.core.utils.date;

import com.learning.core.utils.ObjectUtils;
import com.learning.core.utils.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author: wangwei
 * @Description:
 */
public class LocalDateTimeUtils {
    public LocalDateTimeUtils() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDateTime of(Instant instant, ZoneId zoneId) {
        return null == instant ? null : LocalDateTime.ofInstant(instant, ObjectUtils.defaultIfNull(zoneId, ZoneId.systemDefault()));
    }

    public static LocalDateTime of(Instant instant, String zone) {
        ZoneId zoneId = StringUtils.isBlank(zone) ? ZoneId.systemDefault() : ZoneId.of(zone);
        return of(instant, zoneId);
    }

    public static LocalDateTime of(Instant instant) {
        return of(instant, ZoneId.systemDefault());
    }

    public static LocalDateTime of(ZonedDateTime zonedDateTime) {
        return null == zonedDateTime ? null : zonedDateTime.toLocalDateTime();
    }

    public static LocalDateTime of(Instant instant, TimeZone timeZone) {
        return of(instant, ((TimeZone) ObjectUtils.defaultIfNull(timeZone, TimeZone.getDefault())).toZoneId());
    }

    public static LocalDateTime of(long epochMilli, TimeZone timeZone) {
        return of(Instant.ofEpochMilli(epochMilli), timeZone);
    }

    public static LocalDateTime of(long epochMilli, String timeZone) {
        TimeZone zone = StringUtils.isBlank(timeZone) ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZone);
        return of(epochMilli, zone);
    }

    public static LocalDateTime of(long epochMilli) {
        return of(epochMilli, TimeZone.getDefault());
    }

    public static LocalDateTime of(Date date) {
        return null == date ? null : of(date.toInstant());
    }

    public static LocalDateTime of(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        } else {
            return temporalAccessor instanceof LocalDate ? ((LocalDate) temporalAccessor).atStartOfDay() : LocalDateTime.of(TemporalAccessorUtils.get(temporalAccessor, ChronoField.YEAR), TemporalAccessorUtils.get(temporalAccessor, ChronoField.MONTH_OF_YEAR), TemporalAccessorUtils.get(temporalAccessor, ChronoField.DAY_OF_MONTH), TemporalAccessorUtils.get(temporalAccessor, ChronoField.HOUR_OF_DAY), TemporalAccessorUtils.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR), TemporalAccessorUtils.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE), TemporalAccessorUtils.get(temporalAccessor, ChronoField.NANO_OF_SECOND));
        }
    }

    public static LocalDate ofDate(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        } else {
            return temporalAccessor instanceof LocalDateTime ? ((LocalDateTime) temporalAccessor).toLocalDate() : LocalDate.of(TemporalAccessorUtils.get(temporalAccessor, ChronoField.YEAR), TemporalAccessorUtils.get(temporalAccessor, ChronoField.MONTH_OF_YEAR), TemporalAccessorUtils.get(temporalAccessor, ChronoField.DAY_OF_MONTH));
        }
    }

    public static LocalDateTime parse(CharSequence datetime, DateTimeFormatter formatter) {
        if (null == datetime) {
            return null;
        } else {
            return null == formatter ? LocalDateTime.parse(datetime) : of(formatter.parse(datetime));
        }
    }

    public static LocalDateTime parse(CharSequence datetime) {
        return parse(datetime, (DateTimeFormatter) null);
    }

    public static LocalDateTime parse(CharSequence datetime, String format) {
        return null == datetime ? null : parse(datetime, DateTimeFormatter.ofPattern(format));
    }

    public static LocalDate parseDate(CharSequence date, DateTimeFormatter formatter) {
        if (null == date) {
            return null;
        } else {
            return null == formatter ? LocalDate.parse(date) : ofDate(formatter.parse(date));
        }
    }

    public static LocalDate parseDate(CharSequence date) {
        return parseDate(date, (DateTimeFormatter) null);
    }

    public static LocalDate parseDate(CharSequence date, String format) {
        return parseDate(date, DateTimeFormatter.ofPattern(format));
    }

    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return TemporalAccessorUtils.format(dateTime, formatter);
    }

    public static String format(LocalDateTime dateTime, String format) {
        return format(dateTime, DateTimeFormatter.ofPattern(format));
    }

    public static LocalDateTime offset(LocalDateTime time, long number, TemporalUnit field) {
        return null == time ? null : time.plus(number, field);
    }

    public static Duration between(LocalDateTime startTime, LocalDateTime endTime) {
        return Duration.between(startTime, endTime);
    }

    public static LocalDateTime beginOfDay(LocalDateTime time) {
        return time.with(LocalTime.of(0, 0, 0, 0));
    }

    public static LocalDateTime endOfDay(LocalDateTime time) {
        return time.with(LocalTime.of(23, 59, 59, 999999999));
    }
}
