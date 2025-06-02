package com.learning.interrogation.domain.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * PatientExtendVo
 * <p>
 * 此vo是就诊人信息和账号就诊人关联表在关联后查询出的数据
 *
 * @author huanghaoran
 * @date 2022年3月9日 14:54:38
 */

@Data
@Accessors(chain = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientExtendVO {

    // 就诊人信息表

    private String CJZRBM;


    private String CJZRTX;

    private String CJZRXM;

    private String CJZRSFZH;

    private String CJZRSJ;

    private String CXBBM;

    private String CXBMC;

    private String CMZBM;

    private String CMZMC;

    private Date DCJSJ;

    private Date DXGSJ;

    private String CHZZSY;

    private String CZYBM;

    private String CZYMC;

    private String CSDZBM;

    private String CSDZMC;

    private String CSQDZBM;

    private String CSQDZMC;

    private String CQDZBM;

    private String CQDZMC;

    private String CXDZBM;

    private String CXDZMC;

    private String CCDZBM;

    private String CCDZMC;

    private String CMPH;

    private Date DCSNY;

    private String CHYBM;

    private String CHYMC;

    private String CZJLX;

    private String CZJLXMC;

    private String CHJXXDZ;

    // 就诊人账号关联表
    private String CBM;

    private String CYHBM;

    private String CYHZHID;

    private String CGXBM;

    private Integer ISFMR;

    private String CJHRSFZH;

    private String CJHRLXFS;

    private Date connectionDCJSJ;

    private Date connectionDXGSJ;

    private String INL;
}
