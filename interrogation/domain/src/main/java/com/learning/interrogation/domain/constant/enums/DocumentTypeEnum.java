package com.learning.interrogation.domain.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author WangWei
 * @version v 2.7.0
 * @description 证件类型枚举
 * @date 2024-11-04
 **/
@AllArgsConstructor
public enum DocumentTypeEnum {

    /**
     * 证件类型枚举
     */
    IDENTIFICATION_CARD("01", "身份证"),
    RESIDENCE_BOOKLET( "02","居民户口簿"),
    PASSPORT("03", "护照"),
    HONGKONG_MACAO_PASS( "05","港澳居民来往内地通行证"),
    TAIWAN_PASS( "06","台湾居民来往内地通行证"),
    CHINA_GREEN_CARD("07","外国人永久居留证"),
    PERMANENT_RESIDENCE_PERMIT_FOR_FOREIGNERS("08", "外国人永久居留证"),
    JHRSFZ("11", "监护人身份证"),
    MOTHER_ID_CARD( "19","母亲身份证"),
    OTHER("99", "其他");



    @Getter
    private final String code;

    @Getter
    private final String name;

    public static DocumentTypeEnum getByCode(String code) {
        for (DocumentTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }

        return OTHER;
    }
}
