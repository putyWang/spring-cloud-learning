package com.learning.interrogation.domain.dto;

import com.learning.interrogation.domain.po.patient.InformationPatientPo;
import com.learning.interrogation.domain.po.patient.PatientConnectionPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/4/30 下午10:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PatientUserDTO  extends InformationPatientPo {

    /**
     * 绑定用户 id 列表
     */
    private List<PatientConnectionPO> relationUserList;
}
