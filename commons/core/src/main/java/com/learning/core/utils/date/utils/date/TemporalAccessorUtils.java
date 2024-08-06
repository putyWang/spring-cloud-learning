package com.learning.core.utils.date.utils.date;

import com.learning.core.utils.date.utils.StringUtil;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

/**
 * @Author: wangwei
 * @Description:
 */
public class TemporalAccessorUtils {
    public TemporalAccessorUtils() {
    }

    public static int get(TemporalAccessor temporalAccessor, TemporalField field) {
        return temporalAccessor.isSupported(field) ? temporalAccessor.get(field) : (int) field.range().getMinimum();
    }

    public static String format(TemporalAccessor temporalAccessor, DateTimeFormatter formatter) {
        if (null == temporalAccessor) {
            return null;
        } else {
            if (null == formatter) {
                formatter = DateTimeFormatter.ISO_DATE_TIME;
            }

            return formatter.format(temporalAccessor);
        }
    }

    public static String format(TemporalAccessor temporalAccessor, String formatter) {
        DateTimeFormatter format = StringUtil.isBlank(formatter) ? null : DateTimeFormatter.ofPattern(formatter);
        return format(temporalAccessor, format);
    }

    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        } else {
            Instant result;
            if (temporalAccessor instanceof Instant) {
                result = (Instant) temporalAccessor;
            } else if (temporalAccessor instanceof LocalDateTime) {
                result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
            } else if (temporalAccessor instanceof ZonedDateTime) {
                result = ((ZonedDateTime) temporalAccessor).toInstant();
            } else if (temporalAccessor instanceof OffsetDateTime) {
                result = ((OffsetDateTime) temporalAccessor).toInstant();
            } else if (temporalAccessor instanceof LocalDate) {
                result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
            } else if (temporalAccessor instanceof LocalTime) {
                result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
            } else if (temporalAccessor instanceof OffsetTime) {
                result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toInstant();
            } else {
                result = Instant.from(temporalAccessor);
            }

            return result;
        }
    }
}

