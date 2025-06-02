package com.learning.interrogation.server.flow;

import com.learning.interrogation.domain.dto.AddPatientContext;
import com.learning.interrogation.domain.po.patient.RealRecordPO;
import com.learning.interrogation.server.mapper.patient.RealRecordMapper;
import com.learning.interrogation.server.util.SnowflakeIdUtil;
import com.yomahub.liteflow.annotation.LiteflowFact;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/3 下午11:51
 */
@Component
@RequiredArgsConstructor
public class RealRecordLiteFlow {

    private final RealRecordMapper realRecordMapper;

    private final SnowflakeIdUtil snowflakeIdUtil;

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "addSelfPatientInfo", nodeName = "保存就诊人信息", nodeType = NodeTypeEnum.COMMON)
    public void addSelfPatientInfo(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        /***保存实名结果(没有走真正面部识别，但是校验了用户身份真实性，这里也默认保存一条实名记录)***/
        RealRecordPO authRecord = new RealRecordPO();
        authRecord.setCBM(snowflakeIdUtil.getSnowflakeId()).setCJZRBM(addPatientContext.getCurrentPatient().getCJZRBM())
                .setCSSYWBM(addPatientContext.getTokenInfo().getUserAuthorizeCode())
                .setIYWLY(1).setISMJG(3)
                .setCSHYJ("已通过实名系统校验真实性").setDCJSJ(new Date());
        realRecordMapper.insert(authRecord);
    }
}
