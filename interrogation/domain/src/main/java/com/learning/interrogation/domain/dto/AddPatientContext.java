package com.learning.interrogation.domain.dto;

import com.learning.interrogation.domain.po.patient.AdmissionCardInfoPO;
import com.learning.interrogation.domain.po.user.TokenInfoPo;
import com.learning.interrogation.domain.vo.PatientAddVO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/3 下午8:45
 */
@Data
@Accessors(chain = true)
public class AddPatientContext {

    /**
     * 保存就诊人入参
     */
    private PatientAddVO patient;

    /**
     * 当前用户信息
     */
    private TokenInfoPo tokenInfo;

    /**
     * 当前用户关联的就诊人列表
     */
    private List<PatientUserDTO> patientList = new ArrayList<>();

    /**
     * 当前就诊人编码对应的就诊人信息
     */
    private PatientUserDTO currentPatient;

    /**
     * 就诊卡信息
     */
    private AdmissionCardInfoPO admissionCardInfo;
}
