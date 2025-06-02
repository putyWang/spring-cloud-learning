package com.learning.interrogation.server.service.patient.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.interrogation.domain.dto.PatientIDCardAddDTO;
import com.learning.interrogation.domain.po.patient.AdmissionCardInfoPO;
import com.learning.interrogation.domain.po.patient.InformationPatientPo;
import com.learning.interrogation.domain.po.patient.VisitCardAssociationPO;
import com.learning.interrogation.domain.vo.*;
import com.learning.interrogation.server.mapper.patient.AdmissionCardInfoMapper;
import com.learning.interrogation.server.mapper.patient.InformationPatientMapper;
import com.learning.interrogation.server.service.patient.AdmissionCardInfoService;
import com.learning.interrogation.server.service.patient.IVisitCardAssociationService;
import com.learning.interrogation.server.service.patient.InformationPatientService;
import com.learning.interrogation.server.util.AgeUtil;
import com.learning.interrogation.server.util.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/1 下午2:20
 */
@RequiredArgsConstructor
@Service
@Log4j2
public class AdmissionCardInfoServiceImpl
        extends ServiceImpl<AdmissionCardInfoMapper, AdmissionCardInfoPO>
        implements AdmissionCardInfoService {

    @Resource
    @Lazy
    private InformationPatientService informationPatientService;

    private final SnowflakeIdUtil snowflakeIdUtil;

    private final IVisitCardAssociationService iVisitCardAssociationService;



    @Override
    public int checkPatientIDCardIsExist(String accountId, String orgCode) {
        return this.baseMapper.checkPatientIDCardIsExist(accountId, orgCode);
    }

    @Override
    public void createMedicalCard(PatientIDCardRegisterVO register, InformationPatientPo patientPo, String userId) {
        // 1 设置手机号
        // 1.1 若当前被申请就诊卡患者没有手机号，则取当前微信用户手机号
        String phoneNum = StrUtil.isNotBlank(register.getCJZRSJ()) ? register.getCJZRSJ()
                : StrUtil.isNotBlank(patientPo.getCJZRSJ()) ? patientPo.getCJZRSJ()
                : informationPatientService.getPriorityPatientPhone();
        Assert.isTrue(StrUtil.isNotBlank(phoneNum), "当前就诊人手机号缺失，请在个人中心-家庭管理完善其信息");
        // 1.2 设置手机号
        patientPo.setCJZRSJ(phoneNum);
        // 2 同步 his 就诊卡信息
        // 2.1 是否限制患者先窗口建卡（通过实名卡查询接口判断患者是否已经线下建卡）
//        Optional<HisPatientInfo> hisPatientInfo = hisPatientService.queryCardInfo(
//                new HisPatientQueryReqVO().setCardNo(patientPo.getCJZRSFZH())
//                        .setCardType(patientPo.getCZJLX()).setIdCard(patientPo.getCJZRSFZH()).setOrgId(register.getCJGBM())
//                        .setIdCardType(CardTypeTransformEnum.getTransformType(cardTypeTransform, patientPo.getCZJLX()))
//        );
        // 2.2 未注册时调用 his 注册卡数据
//        HisPatientRegistryRespVO result = hisPatientInfo.isPresent() ? hisPatientInfo.get() : hisPatientService.registry(
//                HisPatientRegistryReqVO.build().setPatientPo(patientPo).setOrgCode(register.getCJGBM()).build()
//        );
        HisPatientRegistryRespVO result = new HisPatientRegistryRespVO();
        // 3 根据 his 同步的就诊卡信息本地新增就诊卡
        log.info("调用 his，注册院内就诊卡。返回参数 转 map：{}", result);
        // 3.1 设置基础信息
        PatientIDCardAddDTO patientIDCardAdd = new PatientIDCardAddDTO().setCJZRBM(register.getCJZRBM())
                .setISFMR(register.getISFMR()).setCJGBM(register.getCJGBM())
                .setIKLX(2).setCJGMC(register.getCJGMC())
                .setCKH(String.valueOf(result.getCardId()))
                .setPatientId(result.getPatientId())
                .setIBDZT(1);
        // 3.2 设置监护人信息
        if (AgeUtil.getAgeWithYear(patientPo.getDCSNY()) <= 6) {
            PatientExtendVO priorityPatient = informationPatientService.getPriorityPatient(false);
            patientIDCardAdd.setCJHRXM(priorityPatient.getCJZRXM()).setCJHRNL(priorityPatient.getINL())
                    .setCJHRSFZH(priorityPatient.getCJHRSFZH()).setCJHRLXFS(priorityPatient.getCJZRSJ()).setISFMR(register.getISFMR());
        }
        // 3.3 新增就诊卡
        addPatientIDCard(patientIDCardAdd, userId);
        informationPatientService.updateById(
                BeanUtil.copyProperties(register, InformationPatientPo.class)
                        .setCJZRBM(patientPo.getCJZRBM()).setDXGSJ(new Date())
        );
    }

    /**
     * 新增就诊卡
     *
     * @param patientIDCardAdd
     * @param yhbm
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPatientIDCard(PatientIDCardAddDTO patientIDCardAdd, String yhbm) {
        Date createTime = new Date();
        // 1 保存就诊卡信息
        AdmissionCardInfoPO jzkxx = BeanUtil.copyProperties(patientIDCardAdd, AdmissionCardInfoPO.class);
        this.baseMapper.insert(jzkxx.setCBM(snowflakeIdUtil.getSnowflakeId())
                .setDCJSJ(createTime).setDBDSJ(createTime)
                .setIBDZT(1).setIKZT(1));
        // 2 需要更新默认就诊卡时，先将库里账户关联所有就诊卡设为非默认
        if (ObjectUtil.equals(patientIDCardAdd.getISFMR(), 1)) {
            iVisitCardAssociationService.update(
                    Wrappers.<VisitCardAssociationPO>lambdaUpdate()
                            .eq(VisitCardAssociationPO::getCYHZHID, yhbm)
                            .set(VisitCardAssociationPO::getISFMR, 0)
            );
        }
        // 2 保存当前账户与当前就诊卡关系
        iVisitCardAssociationService.save(new VisitCardAssociationPO()
                .setCJZKID(jzkxx.getCBM()).setCYHZHID(yhbm)
                .setCZHJZKGLID(snowflakeIdUtil.getSnowflakeId()).setDCJSJ(createTime)
                .setISFMR(patientIDCardAdd.getISFMR()));

        // 3 若当前绑定的卡为电子健康卡，则需要解除当前用户与当前患者的 所有非电子健康卡关联
        if (patientIDCardAdd.getIKLX() == 3) {
            // 3.1 查询当前患者所有普通就诊卡
            List<AdmissionCardInfoPO> admissionCardInfoPos = this.baseMapper.selectList(new QueryWrapper<AdmissionCardInfoPO>().lambda()
                    .eq(AdmissionCardInfoPO::getCJZRBM, patientIDCardAdd.getCJZRBM())
                    .ne(AdmissionCardInfoPO::getIKLX, 3));
            // 3.2 去除当前用户与以上普通就诊卡的关联关系
            if (CollUtil.isNotEmpty(admissionCardInfoPos)) {
                List<String> cardIdList = admissionCardInfoPos.stream()
                        .map(AdmissionCardInfoPO::getCBM)
                        .collect(Collectors.toList());
                iVisitCardAssociationService.remove(new QueryWrapper<VisitCardAssociationPO>().lambda()
                        .eq(VisitCardAssociationPO::getCYHZHID, yhbm)
                        .in(VisitCardAssociationPO::getCJZKID, cardIdList)
                );
            }
        }
    }
}
