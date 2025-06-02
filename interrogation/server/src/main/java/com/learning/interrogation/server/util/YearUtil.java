package com.learning.interrogation.server.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangWei
 * @version v 1.0
 * @description 年工具类
 * @date 2024-07-18
 **/
public interface YearUtil {

    /**
     * 获提指定时间列表中所有年份
     *
     * @param timeList 时间列表
     * @return 去重年份列表
     */
    static List<String> getYearList(List<String> timeList) {
        return timeList.parallelStream()
                .map(YearUtil::getYear)
                .distinct().collect(Collectors.toList());
    }

    /**
     * 获取最小时间与最大时间之间的年份列表
     *
     * @param minTime
     * @param maxTime
     * @return
     */
    static List<String> getYearList(String minTime, String maxTime) {
        return getYearList(minTime, maxTime, false);
    }

    /**
     * 获取最小时间与最大时间之间的年份列表
     * @param minTime 开始时间
     * @param maxTime 结束时间
     * @param desc 是否倒序
     * @return
     */
    static List<String> getYearList(String minTime, String maxTime, boolean desc) {
        try {
            LocalDate minDate = handleTimeStr(minTime);
            LocalDate maxDate = handleTimeStr(maxTime);
            List<String> yearList = new ArrayList<>();
            if (desc) {
                for (;minDate.compareTo(maxDate) <= 0 ; maxDate = maxDate.plusYears(-1L)) {
                    yearList.add(String.valueOf(maxDate.getYear()));
                }
            } else {
                for (;minDate.compareTo(maxDate) <= 0 ; minDate = minDate.plusYears(1L)) {
                    yearList.add(String.valueOf(minDate.getYear()));
                }
            }
            return yearList;
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s 或 %s 转化为 date 失败", minTime, maxTime));
        }
    }

    /**
     * 两个时间之间日期差异
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 差距年
     */
    static int yearDiscrepancy(String begin, String end) {
        try {
            LocalDate minDate = handleTimeStr(begin);
            LocalDate maxDate = handleTimeStr(end);
            int number = 0;
            for (; minDate.compareTo(maxDate) <= 0; minDate = minDate.plusYears(1L)) {
                number++;
            }
            return number;
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s 或 %s 转化为 date 失败", begin, end));
        }
    }

    /**
     * 获取时间对应的年
     * @param time 时间
     * @return 年
     */
    static String getYear(String time) {
        try {
            return String.valueOf(handleTimeStr(time).getYear());
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s 转化为 date 失败", time));
        }
    }

    /**
     * 将时间字符串转换为 date 对象
     * @param timeStr 时间字符串
     * @return 转换的 date
     */
    static LocalDate handleTimeStr(String timeStr) {
        Assert.isTrue(StrUtil.isNotBlank(timeStr), "时间字段不能为空");
        String time = timeStr.replace("-", "").replace("/", "");
        Assert.isTrue(StrUtil.isNotBlank(time), "时间字段无效");
        if (time.length() >= 4) {
            time = time.substring(0, 4) + "1231";
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(time, df);
    }

}
