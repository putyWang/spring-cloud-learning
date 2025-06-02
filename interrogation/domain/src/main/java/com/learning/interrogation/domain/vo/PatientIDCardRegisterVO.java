package com.learning.interrogation.domain.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: PatientIDCardRegisterVo
 * @Description:
 * @Author: phy
 * @Date: 2020/8/13 14:45
 * @Version V1.0.0
 **/
@Data
@Accessors(chain = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientIDCardRegisterVO {

    private String CJZRBM;

    private String CJGBM;

    private String CJGMC;

    private String CJZRSJ;

    private String  CSDZBM;

    private String  CSDZMC;

    private String  CSQDZBM;

    private String  CSQDZMC;

    private String  CQDZBM;

    private String  CQDZMC;

    private String  CMPH;

    private Integer ISFMR = 0;
}
