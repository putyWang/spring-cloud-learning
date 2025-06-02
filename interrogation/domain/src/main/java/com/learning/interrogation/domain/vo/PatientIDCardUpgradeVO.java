package com.learning.interrogation.domain.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * PatientIDCardUpgradeVo
 *
 * @author huanghaoran
 * @date 2022/3/4
 */
@Data
@Accessors(chain = true)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientIDCardUpgradeVO {

    private String cardId;

    private String ehealthCardId;

    /**
     *     微信电子健康卡所需参数
      */
    private String weChatCode;
}
