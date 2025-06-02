package com.learning.interrogation.domain.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PatientIdCardEnum
 * 就诊卡枚举
 *
 * @author huanghaoran
 * @date 2022/2/14
 */
@Getter
@AllArgsConstructor
public enum PatientIdCardEnum {

    /**
     * 就诊卡类型
     */

    UNKNOWN("unknown",0,"未知的卡类型"),
    STJZK("STJZK", 1,"实体就诊卡"),
    DZJZK("DZJZK", 2,"电子就诊卡"),
    DZJKK("DZJKK", 3,"电子健康卡"),
    TXDZJKK("TXDZJKK", 4,"腾讯电子健康卡");

    /**
     * 卡属性名 如：STJZK
     */
    private final String propertyName;
    /**
     * 卡类型数值 如：1
     */
    private final Integer cardType;
    /**
     * 卡实际名 如：实体就诊卡
     */
    private final String cardName;


    /**
     * 根据卡类型名名获取卡名
     * @param propertyName 卡类型名
     * @return 卡实际名
     */
    public static String getCardNameByPropertyName(String propertyName) {
        for (PatientIdCardEnum e : PatientIdCardEnum.values()) {
            if (e.getPropertyName().equals(propertyName)) {
                return e.getCardName();
            }
        }
        return PatientIdCardEnum.UNKNOWN.getCardName();
    }

    /**
     * 根据卡类型名名获取卡类型int值
     * @param propertyName 卡类型名
     * @return 卡类型int值
     */
    public static int getCardTypeByPropertyName(String propertyName) {
        for (PatientIdCardEnum e : PatientIdCardEnum.values()) {
            if (e.getPropertyName().equals(propertyName)) {
                return e.getCardType();
            }
        }
        return PatientIdCardEnum.UNKNOWN.getCardType();
    }

    /**
     * 根据卡类型名名获取卡类型int值
     * @param cardType 卡类数值
     * @return 卡类型名字
     */
    public static String getCardNameByCardType(Integer cardType) {
        for (PatientIdCardEnum e : PatientIdCardEnum.values()) {
            if (e.getCardType().equals(cardType)) {
                return e.getCardName();
            }
        }
        return PatientIdCardEnum.UNKNOWN.getCardName();
    }
}
