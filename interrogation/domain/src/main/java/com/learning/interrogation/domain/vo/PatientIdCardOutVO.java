package com.learning.interrogation.domain.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * PatientIdCardOutVo
 * 卡信息
 * <p>
 * 关于此vo重写的set方法是因为要兼容旧接口   卡列表查询，卡详情查询。 返回的字段。
 *
 * @author huanghaoran
 * @date 2022/2/16
 */

@Data
@Accessors(chain = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientIdCardOutVO {

    private String cardId;

    private String patientId;

    private String userName;

    private String orgName;

    private String orgCode;

    private String idCard;

    private String typeName;

    private Integer typeNumber;

    private Integer reserve;

    private Integer reservePatient;

    private Integer bind;

    private Integer realName;

    private Integer gender;

    private String phone;

    private String cardNumber;

    private String provinceAddress;

    private String cityAddress;

    private String countyAddress;

    private String burgAddress;

    private String villageAddress;

    private String doorplate;

    private String address;

    private Integer relationshipType;

    private String relationshipName;

    private String qRCode;

    private String CJZRXM;

    private String CBM;

    private String CJZRBM;

    private String CZJHM;

    private String CGXBM;

    private String CJGMC;

    private String CJZKMC;

    private String CXM;

    private Integer ISFMR;

    private Integer ISMZT;

    private String CKLXMC;

    private String CKH;

    private String CXBMC;

    private String CSR;

    private String CDHHM;

    private String CDZ;

    private String occupationName;

    private String nationName;
}
