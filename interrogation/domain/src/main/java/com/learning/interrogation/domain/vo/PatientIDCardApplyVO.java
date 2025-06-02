package com.learning.interrogation.domain.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: ApplyPatientIDCardVO
 * @Description:
 * @Author: phy
 * @Date: 2020/8/13 9:06
 * @Version V1.0.0
 **/
@Data
@Accessors(chain = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientIDCardApplyVO {

    private String CJZRBM;

    //微信电子健康卡所需参数
    private String wechatCode;

    private String healthCard;

    private String ehealthCardId;

    private Integer idCardMethod;
}
