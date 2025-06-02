package com.learning.interrogation.server.flow.patient;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.learning.interrogation.domain.dto.AddPatientContext;
import com.learning.interrogation.domain.dto.PatientUserDTO;
import com.learning.interrogation.domain.po.patient.PatientConnectionPO;
import com.learning.interrogation.server.service.patient.PatientConnectionService;
import com.learning.interrogation.server.util.SnowflakeIdUtil;
import com.yomahub.liteflow.annotation.LiteflowFact;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/3 下午8:33
 */
@Component
@RequiredArgsConstructor
public class PatientRelationLiteFlow {

    private final PatientConnectionService patientConnectionService;

    private final SnowflakeIdUtil snowflakeIdUtil;

    private final ThreadPoolTaskExecutor interrogationExecutor;

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "patientRelationAdd", nodeName = "新增就诊人关联关系", nodeType = NodeTypeEnum.COMMON)
    public void patientRelationAdd(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        PatientUserDTO currentPatient = addPatientContext.getCurrentPatient();
        // 1 若新增就诊人为默认，需要将其他所有人全更新为非默认
        // 2 异步验证当前登录用户信息
        CompletableFuture<Void> completableFuture = null;
        if(1 == addPatientContext.getPatient().getISFMR() && CollUtil.isNotEmpty(currentPatient.getRelationUserList())) {
            completableFuture = CompletableFuture.runAsync(
                    () -> patientConnectionService.updateBatchById(
                            currentPatient.getRelationUserList().stream()
                                    .peek(relation -> relation.setISFMR(0))
                                    .collect(Collectors.toList())
                    ),
                    interrogationExecutor
            );
        }
        // 2 添加  用户&就诊人 关联关系
        patientConnectionService.save(
                BeanUtil.copyProperties(addPatientContext.getPatient(), PatientConnectionPO.class)
                        .setCBM(snowflakeIdUtil.getSnowflakeId()).setCJZRBM(currentPatient.getCJZRBM())
                        .setCGXBM(addPatientContext.getPatient().getCGXBM())
                        .setCYHZHID(addPatientContext.getTokenInfo().getUserId())
                        .setDCJSJ(new Date())
        );
        if (ObjectUtil.isNotNull(completableFuture)) {
            completableFuture.join();
        }
    }
}
