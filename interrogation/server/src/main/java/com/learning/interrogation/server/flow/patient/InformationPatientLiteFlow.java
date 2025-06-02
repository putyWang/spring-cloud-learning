package com.learning.interrogation.server.flow.patient;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.learning.interrogation.domain.constant.enums.DocumentTypeEnum;
import com.learning.interrogation.domain.dto.AddPatientContext;
import com.learning.interrogation.domain.dto.PatientUserDTO;
import com.learning.interrogation.domain.po.patient.InformationPatientPo;
import com.learning.interrogation.domain.po.patient.PatientConnectionPO;
import com.learning.interrogation.domain.po.user.UserInfoPO;
import com.learning.interrogation.domain.vo.PatientAddVO;
import com.learning.interrogation.server.mapper.user.UserInfoMapper;
import com.learning.interrogation.server.service.patient.InformationPatientService;
import com.learning.interrogation.server.util.IDCardUtil;
import com.learning.interrogation.server.util.SnowflakeIdUtil;
import com.yomahub.liteflow.annotation.LiteflowFact;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/3 下午8:33
 */
@Component
@RequiredArgsConstructor
public class InformationPatientLiteFlow {

    private static final Logger log = LoggerFactory.getLogger(InformationPatientLiteFlow.class);
    private final UserInfoMapper userInfoMapper;

    private final ThreadPoolTaskExecutor interrogationExecutor;

    private final InformationPatientService patientService;

    private final SnowflakeIdUtil snowflakeIdUtil;

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "addPatientValid", nodeName = "添加就诊人验证", nodeType = NodeTypeEnum.COMMON)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addPatientValid(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        log.info("开始注册就诊卡");
        PatientAddVO patient = addPatientContext.getPatient();
        // 1 身份证格式校验
        if (DocumentTypeEnum.IDENTIFICATION_CARD.equals(patient.getCZJLX())) {
            //校验身份证
            Assert.isTrue(!IDCardUtil.validateCard(patient.getCJZRSFZH()), "身份证号码不合法");
            patient.setDCSNY(DateUtil.format(IDCardUtil.idToDate(patient.getCJZRSFZH()), "yyyy-MM-dd"));
        } else {
            Assert.notNull(patient.getDCSNY(), "患者出生年月不能为空！");
        }
        // 2 异步验证当前登录用户信息
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(
                () -> {
                    UserInfoPO userInfoPO = userInfoMapper.selectById(addPatientContext.getTokenInfo().getUserId());
                    return userInfoPO;
                },
                interrogationExecutor
        ).thenAccept(
                user -> {
                    Assert.notNull(user, "当前用户不存在，请勿非法操作！");
                    Assert.equals(user.getUserStatus(), 1, "当前用户不存在，请勿非法操作！");
                });
        // 3 就诊人相关验证
        // 3.1 查询关联就诊人列表
        List<PatientUserDTO> patientList = patientService.getPatientInfo(patient.getCZJLX(), patient.getCJZRSFZH(), addPatientContext.getTokenInfo().getUserId());
        if (CollUtil.isNotEmpty(patientList)) {
            for (PatientUserDTO patientUserDTO : patientList) {
                if (ObjectUtil.equals(patientUserDTO.getCJZRSFZH(), patient.getCJZRSFZH())) {
                    addPatientContext.setCurrentPatient(patientUserDTO);
                } else {
                    addPatientContext.getPatientList().add(patientUserDTO);
                }
            }
        }
        // 3.2 当前就诊人信息验证
        PatientUserDTO currentPatient = addPatientContext.getCurrentPatient();
        if (ObjectUtil.isNotNull(currentPatient)) {
            List<PatientConnectionPO> relationUserList = currentPatient.getRelationUserList();
            if (CollUtil.isNotEmpty(relationUserList)) {
                for (PatientConnectionPO relationUser : relationUserList) {
                    // 3.2.1 校验就诊人是否已被本人绑定
                    if (ObjectUtil.equals(relationUser.getCYHZHID(), addPatientContext.getTokenInfo().getUserId())) {
                        throw new RuntimeException("当前就诊人已绑定");
                    }
                    // 3.2.2 添加为本人时验证是否被其他人作为本人绑定
                    if (ObjectUtil.equals(patient.getCGXBM(), "0") && ObjectUtil.equals(relationUser.getCGXBM(), "0")) {
                        throw new RuntimeException("就诊人已被其他账号绑定为本人");
                    }
                }
            }
            currentPatient.setCZYBM(patient.getCZYBM());
            currentPatient.setCZYMC(patient.getCZYMC());
        }
        // 3.3 从账号绑定就诊人验证
        // 3.3.1 查询账户绑定就诊人列表
        List<PatientUserDTO> patientConnectionPos = addPatientContext.getPatientList();
        if (CollUtil.isEmpty(patientConnectionPos) && 6 >= IdcardUtil.getAgeByIdCard(patient.getCJZRSFZH())) {
            throw new RuntimeException("第一次绑定就诊人年龄必须大于6岁以上");
        }
        // 3.3.2 验证是否已绑定本人信息
        if (ObjectUtil.equals(patient.getCGXBM(), "0")
                && patientConnectionPos.parallelStream().anyMatch(patientConnectionPo -> ObjectUtil.equals(patientConnectionPo.getRelationUserList().get(0).getCGXBM(), "0"))) {
            throw new RuntimeException("账号已存在本人信息，请勿重复绑定");
        }
        voidCompletableFuture.join();
    }

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "addPatientInfo", nodeName = "保存就诊人信息", nodeType = NodeTypeEnum.COMMON)
    public void addPatientInfo(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        PatientUserDTO currentPatient = addPatientContext.getCurrentPatient();
        Date createTime = new Date();

        if (ObjectUtil.isEmpty(currentPatient)) {
            // 1 在当前就诊人不存在时，直接新建
            currentPatient = BeanUtil.copyProperties(addPatientContext.getPatient(), PatientUserDTO.class);
            currentPatient.setDCSNY(DateUtil.parse(addPatientContext.getPatient().getDCSNY(), DatePattern.NORM_DATE_PATTERN))
                    .setCJZRBM(snowflakeIdUtil.getSnowflakeId()).setDCJSJ(createTime)
                    .setCZYBM(addPatientContext.getPatient().getCZYBM())
                    .setCZYMC(addPatientContext.getPatient().getCZYMC());
            this.patientService.save(currentPatient);
        } else {
            // 2 当前就诊人存在时，进行更新
            currentPatient = BeanUtil.copyProperties(addPatientContext.getPatient(), PatientUserDTO.class);
            currentPatient.setCMPH(ObjectUtil.defaultIfNull(addPatientContext.getPatient().getCMPH(), "").replaceAll(" ", " "))
                    .setCJZRBM(currentPatient.getCJZRBM());
            this.patientService.updateById(currentPatient);
        }
        addPatientContext.setCurrentPatient(currentPatient);
    }

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS_BOOLEAN, nodeId = "isSelf", nodeName = "是否本人", nodeType = NodeTypeEnum.BOOLEAN)
    public boolean isSelf(NodeComponent bindCmp, @LiteflowFact("addPatientContext") AddPatientContext addPatientContext) {
        return "00".equals(addPatientContext.getPatient().getCGXBM());
    }
}
