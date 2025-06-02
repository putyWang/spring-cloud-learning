package com.learning.interrogation.server.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.learning.interrogation.domain.constant.enums.DocumentTypeEnum;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WangWei
 * @version v 2.7.0
 * @description 获取年龄工具
 * @date 2024-11-04
 **/
public class AgeUtil {

    private AgeUtil(){}

    /**
     * 中国公民身份证号码最小长度。
     */
    private final static int CHINA_ID_MIN_LENGTH = 15;


    /**
     * 根据证件类型获取整型年龄(小于1岁返回0)
     *
     * @param documentType 证件类型
     * @param idNumber     证件号
     * @param birthday     出生日期
     * @return 年龄
     */
    public static int getAge(DocumentTypeEnum documentType, String idNumber, String birthday) {
        if (DocumentTypeEnum.IDENTIFICATION_CARD.equals(documentType)) {
            return getAgeByIdCard(idNumber);
        } else {
            return YearUtil.yearDiscrepancy(birthday, DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        }
    }

    /**
     * 获取患者-->年龄单位
     *
     * @param birthday 出生日期
     * @return 小于一月 显示 天数，小于一年，显示月份，小于
     */
    public static Integer getAgeWithYear(Date birthday) {
        Assert.notNull(birthday, "患者出生年月不能为空");
        Map<String, String> ageWithUnitMap = getAgeWithUnitMap(DateUtil.format(birthday, DatePattern.NORM_DATE_FORMATTER));
        if ("岁".equals(ageWithUnitMap.get("ageUnit"))) {
            return Integer.parseInt(ageWithUnitMap.get("ageNum"));
        }
        return 0;
    }


    /**
     * 通过患者 字符串格式的生日（Str "2025-01-01"） 获取-->带单位年龄
     * @param birthdayStr
     * @return {"ageNum":"1","ageUnit":"个月"}
     */
    public static Map<String, String> getAgeWithUnitMap(String birthdayStr) {

        Assert.isTrue(StrUtil.isNotBlank(birthdayStr), "患者出生年月不能为空");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(birthdayStr, formatter);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);

        Map<String, String> ageMap = new HashMap<>();
        int years = period.getYears();
        int months = period.getMonths();
        int ageNum;
        String ageUnit;

        if (years < 1) {
            if (months < 1) {
                long totalDays = java.time.temporal.ChronoUnit.DAYS.between(birthDate, currentDate);
                ageNum = (int)totalDays;
                ageUnit = "天";
            } else {
                ageNum = months + years * 12;
                ageUnit = "个月";
            }
        } else {
            ageNum = years;
            ageUnit = "岁";
        }

        ageMap.put("ageNum", String.valueOf(ageNum));
        ageMap.put("ageUnit", ageUnit);

        return ageMap;
    }

    /**
     * 根据身份编号获取年龄
     *
     * @param idCard 身份编号
     * @return 年龄
     */
    public static int getAgeByIdCard(String idCard) {

        Assert.isTrue(StrUtil.isNotBlank(idCard), "根据身份编号获取年龄,传入的身份编号不能为空");

        int age = 0;

        //身份证转换
        if (idCard.length() == CHINA_ID_MIN_LENGTH) {
            idCard = IDCardUtil.conver15CardTo18(idCard);
        }
        if (StrUtil.isNotBlank(idCard)) {
            String year = idCard.substring(6, 10);
            Calendar cal = Calendar.getInstance();
            int iCurrYear = cal.get(Calendar.YEAR);
            age = iCurrYear - Integer.parseInt(year);
        }
        return age;
    }
}
