package com.learning.interrogation.server.flow.patient.card;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.learning.interrogation.domain.constant.enums.DocumentTypeEnum;
import com.learning.interrogation.domain.constant.enums.PatientIdCardEnum;
import com.learning.interrogation.domain.dto.AddPatientContext;
import com.learning.interrogation.domain.po.patient.AdmissionCardInfoPO;
import com.learning.interrogation.domain.vo.HisPatientRegistryRespVO;
import com.learning.interrogation.domain.vo.PatientIDCardRegisterVO;
import com.learning.interrogation.server.service.org.IInstitutionalExtensionService;
import com.learning.interrogation.server.service.patient.AdmissionCardInfoService;
import com.learning.interrogation.server.util.AgeUtil;
import com.learning.interrogation.server.util.SnowflakeIdUtil;
import com.yomahub.liteflow.annotation.LiteflowFact;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 就诊卡相关流程
 * @author wangwei
 * @version 1.0
 * @date 2025/5/4 下午7:50
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class AdmissionCardLiteFlow {

    private final AdmissionCardInfoService admissionCardInfoService;

    private final IInstitutionalExtensionService iInstitutionalExtensionService;

    private final SnowflakeIdUtil snowflakeIdUtil;

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "obtainCardInfo", nodeName = "获取当前就诊卡信息", nodeType = NodeTypeEnum.COMMON)
    public void obtainCardInfo(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        // 1 获取本地就诊人信息
        AdmissionCardInfoPO admissionCardInfo = admissionCardInfoService.getOne(
                Wrappers.<AdmissionCardInfoPO>lambdaQuery()
                        .eq(AdmissionCardInfoPO::getCJZRBM, addPatientContext.getCurrentPatient().getCJZRBM())
                        .eq(AdmissionCardInfoPO::getIKLX, 2)
                        .eq(AdmissionCardInfoPO::getCJGBM, addPatientContext.getPatient().getOrgCode())
        );

        if (ObjectUtil.isNull(admissionCardInfo)) {
            // 2 若未获取则从 his 获取
            HisPatientRegistryRespVO result = new HisPatientRegistryRespVO();
            log.info("调用 his，注册院内就诊卡。返回参数 转 map：{}", result);
            // 3 保存就诊卡信息
            admissionCardInfo = new AdmissionCardInfoPO()
                    .setCBM(snowflakeIdUtil.getSnowflakeId())
                    .setCJZRBM(addPatientContext.getCurrentPatient().getCJZRBM())
                    .setISFMR(addPatientContext.getPatient().getISFMR())
                    .setCJGBM(addPatientContext.getPatient().getOrgCode())
                    .setIKLX(2).setCJGMC(addPatientContext.getPatient().getOrgName())
                    .setCKH(String.valueOf(result.getCardId()))
                    .setPatientId(result.getPatientId()).setIBDZT(1);
            admissionCardInfoService.save(admissionCardInfo);
        }
        addPatientContext.setAdmissionCardInfo(admissionCardInfo);
    }

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS_SWITCH, nodeId = "obtainCardType", nodeName = "获取就诊卡注册类型", nodeType = NodeTypeEnum.SWITCH)
    public String obtainCardType(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        // 1 单院模式  开始建卡
        log.info("单院模式  开始建卡");
        // 查询机构已经开启的就诊卡模式.
        Set<Integer> switchList = iInstitutionalExtensionService.getPatientIdCardBusinessFields(addPatientContext.getPatient().getOrgCode());
        // 申请电子就诊卡
        if (switchList.size() != 0 && switchList.contains(PatientIdCardEnum.DZJZK.getCardType())) {
            return "addCard";
        }
        return "";
    }
}
