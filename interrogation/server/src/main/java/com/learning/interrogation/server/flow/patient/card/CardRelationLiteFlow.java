package com.learning.interrogation.server.flow.patient.card;

import com.learning.interrogation.domain.dto.AddPatientContext;
import com.learning.interrogation.domain.po.patient.VisitCardAssociationPO;
import com.learning.interrogation.server.service.patient.IVisitCardAssociationService;
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
 * @date 2025/5/4 下午8:12
 */
@Component
@RequiredArgsConstructor
public class CardRelationLiteFlow {

    private final IVisitCardAssociationService iVisitCardAssociationService;

    private final SnowflakeIdUtil snowflakeIdUtil;

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "addCardRelation", nodeName = "保存", nodeType = NodeTypeEnum.COMMON)
    public void addCardRelation(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        iVisitCardAssociationService.save(new VisitCardAssociationPO()
                .setCJZKID(addPatientContext.getAdmissionCardInfo().getCBM())
                .setCYHZHID(addPatientContext.getTokenInfo().getUserId())
                .setCZHJZKGLID(snowflakeIdUtil.getSnowflakeId())
                .setDCJSJ(new Date())
                .setISFMR(addPatientContext.getPatient().getISFMR()));
    }
}
