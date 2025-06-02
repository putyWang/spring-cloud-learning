package com.learning.interrogation.server.service.patient;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.interrogation.domain.po.patient.AdmissionCardInfoPO;
import com.learning.interrogation.domain.po.patient.InformationPatientPo;
import com.learning.interrogation.domain.vo.*;

import java.util.Optional;

/**
 * @ClassName: IPatientIDCardService
 * @Description: 就诊卡管理 业务层
 * @Author: phy
 * @Date: 2020/5/25 15:33
 * @Version V1.0.0
 **/

public interface AdmissionCardInfoService extends IService<AdmissionCardInfoPO> {

    /**
     * 验证是否已注册就诊卡
     * @param accountId 账户 id
     * @param orgCode 机构编码
     * @return 数量
     */
    int checkPatientIDCardIsExist(String accountId, String orgCode);


    /**
     * 就诊卡创建
     * @param register 注册信息
     * @param patientPo 就诊人信息
     * @param userId 用户 id
     * @return
     * @throws Exception
     */
    void createMedicalCard(PatientIDCardRegisterVO register, InformationPatientPo patientPo, String userId);
}
