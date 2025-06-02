package com.learning.interrogation.domain.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/4/30 下午10:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientAddVO {

    private  String CJZRBM;

    private  String CJZRXM;

    private String  CZJLX;

    private String  CZJLXMC;

    private String  CJZRSFZH;

    private String  CMZBM;

    private String  CMZMC;

    private String  CXBBM;

    private String  CXBMC;

    private String  CGXBM;

    private String  CJZRSJ;

    private String  CSDZBM;

    private String  CSDZMC;

    private String  CSQDZBM;

    private String  CSQDZMC;

    private String  CQDZBM;

    private String  CQDZMC;

    private String  CMPH;

    private String  CZYBM;

    private String  CZYMC;

    private String  CHJXXDZ;

    private Integer ISFMR = 0;

    private String CJHRXM;

    private String CJHRSFZH;

    private String CJHRLXFS;

    private String orgCode;

    private String orgName;

    private String DCSNY;
}
