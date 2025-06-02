package com.learning.interrogation.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * his 就诊卡注册结果
 * @author WangWei
 * @version v 2.9.2
 * @date 2025-04-21
 **/
@Data
@Accessors(chain = true)
public class HisPatientRegistryRespVO {

    /**
     * 病人 id
     */
    private String patientId;

    /**
     * 卡号
     */
    private String cardId;
}
