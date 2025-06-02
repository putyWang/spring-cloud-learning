package com.learning.interrogation.domain.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: PatientIDCardQueryVo
 * @Description: 就诊卡列表查询 vo
 * @Author: phy
 * @Date: 2020/5/25 16:36
 * @Version V1.0.0
 **/

@Data
@Accessors(chain = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientIDCardQueryVO {

    private String CJGBM;

    private Integer IKLX;

    private String CJZRBM;

    private String idCard;

    private String userId;

    private String cardCode;
}
