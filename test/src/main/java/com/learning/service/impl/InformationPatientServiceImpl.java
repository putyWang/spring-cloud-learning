//package com.learning.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.DatePattern;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.lang.Assert;
//import cn.hutool.core.util.IdcardUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import com.learning.entity.po.InformationPatientPO;
//import com.learning.entity.po.PatientConnectionPO;
//import com.learning.repository.patient.PatientRepository;
//import com.learning.service.InformationPatientService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import reactor.core.publisher.Mono;
//
//import java.util.*;
//
///**
// * @author wangwei
// * @version 1.0
// * @date 2025/4/30 下午10:09
// */
//@Log4j2
//@RequiredArgsConstructor
//@Service
//public class InformationPatientServiceImpl
//        extends ServiceImpl<PatientRepository, InformationPatientPO>
//        implements InformationPatientService {
//
//    @Value("${maxAuthTimes:20}")
//    private Integer maxAuthTimes;
//
//    /**
//     * 添加就诊人数据
//     *
//     * @param patient
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map<String,Object> addPatient(PatientAddVO patient) {
//        Map<String, Object> dataMap = new HashMap<>();
//        // 1 参数验证
//        // 1.1 身份证校验
//        if (DocumentTypeEnum.IDENTIFICATION_CARD.equals(patient.getCZJLX())) {
//            //校验身份证
//            if (!IDCardUtil.validateCard(patient.getCJZRSFZH())) {
//                throw new RuntimeException("身份证号码不合法");
//            }
//        } else {
//            Assert.notNull(patient.getDCSNY(), "患者出生年月不能为空！");
//        }
//
//        //获取当前登录用户
//        TokenInfoPo tokenInfo = MyUserTokenInfo.getUserInfoThrowEx();
//        String userId = tokenInfo.getUserId();
//        //校验用户状态
//        UserInfoPO userPo = userInfoMapper.selectById(userId);
//        if (ObjectUtil.isEmpty(userPo)) {
//            throw new RuntimeException("当前用户不存在，请勿非法操作！");
//        }
//        if (!userPo.getUserStatus().equals(1)) {
//            throw new RuntimeException("当前用户状态异常，请联系管理员！");
//        }
//        //恶意认证用户key
//        Object authObject = redisUtil.get(RedisConstant.MALICE_USER_AUTH_TIMES + userId);
//        if (ObjectUtil.isNotEmpty(authObject)) {
//            Assert.isTrue(Integer.valueOf(String.valueOf(authObject)) <= maxAuthTimes, "当日认证次数已达上限，请明日再试");
//        }
//
//        // 1.2 从就诊人绑定账号验证
//        // 1.2.1 查询就诊人信息及其关联用户 id
//        List<PatientUserDTO> patientList = getPatientInfo(patient.getCZJLX(), patient.getCJZRSFZH(), null);
//        PatientUserDTO jzrxx = CollUtil.isEmpty(patientList) ? null : patientList.get(0);
//        String cjzrbm = ObjectUtil.isNotEmpty(patient.getCJZRBM()) ? patient.getCJZRBM() : SnowflakeIdWorker.getInstance().nextIdStr();
//
//        if (ObjectUtil.isNotNull(jzrxx)) {
//            List<PatientConnectionPO> relationUserList = jzrxx.getRelationUserList();
//            if (CollUtil.isNotEmpty(relationUserList)) {
//                for (PatientConnectionPO relationUser : relationUserList) {
//                    // 1.2.2 校验就诊人是否已被本人绑定
//                    if (ObjectUtil.equals(relationUser.getCYHZHID(), userId)) {
//                        dataMap.put("code", 5100);
//                        return dataMap;
//                    }
//                    // 1.2.3 添加为本人时验证是否被其他人作为本人绑定
//                    if (ObjectUtil.equals(patient.getCGXBM(), "0") && ObjectUtil.equals(relationUser.getCGXBM(), "0")) {
//                        throw new RuntimeException("就诊人已被其他账号绑定为本人");
//                    }
//                }
//            }
//            // 设置就诊人职业为当前用户提交数据
//            jzrxx.setCZYBM(patient.getCZYBM());
//            jzrxx.setCZYMC(patient.getCZYMC());
//            // 设置就诊人编码
//            cjzrbm = jzrxx.getCJZRBM();
//        }
//        // 1.3 从账号绑定就诊人验证
//        // 1.3.1 查询账户绑定就诊人列表
//        List<PatientConnectionPO> patientConnectionPos = patientConnectionService
//                .list(
//                        Wrappers.<PatientConnectionPO>lambdaQuery()
//                                .eq(PatientConnectionPO::getCYHZHID, userId)
//                );
//        if (patientConnectionPos == null) {
//            patientConnectionPos = new ArrayList<>();
//            // 1.3.2 验证用户第一个就诊人年龄必须大于 6 岁
//        } else if (CollUtil.isEmpty(patientConnectionPos) && 6 >= IdcardUtil.getAgeByIdCard(patient.getCJZRSFZH())) {
//            throw new RuntimeException("第一次绑定就诊人年龄必须大于6岁以上");
//        }
//        // 1.3.4 验证是否已绑定本人信息
//        if (ObjectUtil.equals(patient.getCGXBM(), "0")
//                && patientConnectionPos.parallelStream().anyMatch(patientConnectionPo -> ObjectUtil.equals(patientConnectionPo.getCGXBM(), "0"))) {
//            throw new RuntimeException("账号已存在本人信息，请勿重复绑定");
//        }
//        // 2 添加就诊人关联数据
//        // 2.2 更新默认就诊人
//        if (patient.getISFMR() != null && patient.getISFMR() == 1) {
//            this.upDefault(userId);
//        }
//        // 2.3 添加本人就诊人数据
//        if ("0".equals(patient.getCGXBM())) {
//            if (StrUtil.isEmpty(patient.getCJZRSJ())) {
//                throw new RuntimeException("手机号不能为空！");
//            }
//            //【因OCR实名通过率太低，暂时跳过，这里放开让其可以直接添加 “本人” 数据】
//            jzrxx = saveOneself(cjzrbm, patient, jzrxx);
//        } else {
//            // 2.4 非本人就诊人数据添加
//            Date createTime = new Date();
//            // 2.4.1 就诊人不存在时新增就诊人数据
//            if (ObjectUtil.isEmpty(jzrxx)) {
//                //如果新增就诊人信息为空，添加就诊人数据
//                jzrxx = BeanUtil.copyProperties(patient, PatientUserDTO.class);
//                if ("01".equals(patient.getCZJLX())) {
//                    jzrxx.setDCSNY(IDCardUtil.idToDate(patient.getCJZRSFZH()));
//                } else {
//                    jzrxx.setDCSNY(DateUtil.parse(patient.getDCSNY(), DatePattern.NORM_DATETIME_FORMAT));
//                }
//                jzrxx.setCJZRBM(cjzrbm);
//                jzrxx.setDCJSJ(createTime);
//                //单独处理职业，设置就诊人职业为当前用户提交数据
//                jzrxx.setCZYBM(patient.getCZYBM());
//                jzrxx.setCZYMC(patient.getCZYMC());
//                this.baseMapper.insert(jzrxx);
//            } else {
//                this.baseMapper.updateById(
//                        BeanUtil.copyProperties(patient, InformationPatientPo.class)
//                                .setCMPH(ObjectUtil.defaultIfNull(patient.getCMPH(), "").replaceAll(" ", " "))
//                                .setCJZRBM(jzrxx.getCJZRBM())
//                );
//            }
//
//            // 2.4.3 添加  用户&就诊人 关联关系
//            PatientConnectionPO wlylYhjzrxxgl = BeanUtil.copyProperties(patient, PatientConnectionPO.class);
//            wlylYhjzrxxgl.setCBM(SnowflakeIdWorker.getInstance().nextIdStr());
//            wlylYhjzrxxgl.setCJZRBM(cjzrbm);
//            wlylYhjzrxxgl.setCGXBM(patient.getCGXBM());
//            wlylYhjzrxxgl.setCYHZHID(userId);
//            wlylYhjzrxxgl.setDCJSJ(createTime);
//            patientConnectionService.save(wlylYhjzrxxgl);
//        }
//
//        //返回就诊人编码
//        dataMap.put("CJZRBM", cjzrbm);
//
//        // 1 单院模式  开始建卡
//        log.info("单院模式  开始建卡");
//        String orgCode = patient.getOrgCode();
//        // 查询机构已经开启的就诊卡模式.
//        Set<Integer> switchList = iInstitutionalExtensionService.getPatientIdCardBusinessFields(orgCode);
//        // 如果没有就诊卡，判断将要创建的第一个就诊卡是否大于6岁
//        if (0 == admissionCardInfoService.checkPatientIDCardIsExist(userId, patient.getOrgCode())) {
//            int ageNum = AgeUtil.getAge(DocumentTypeEnum.getByCode(patient.getCZJLX()), patient.getCJZRSFZH(), patient.getDCSNY());
//            Assert.isTrue(6 < ageNum, "第一次绑定需大于6岁以上");
//        }
//        // 申请电子就诊卡
//        if (switchList.size() != 0 && switchList.contains(PatientIdCardEnum.DZJZK.getCardType())) {
//            // 查询是否已经存在电子健康卡，存在就将就诊人和卡关联
//            if (! this.patientJointCard(cjzrbm, patient.getOrgCode(), userId, PatientIdCardEnum.DZJKK, patient.getISFMR())) {
//                PatientIDCardRegisterVO patientIdCardRegisterVo = BeanUtil.copyProperties(patient, PatientIDCardRegisterVO.class)
//                        .setCJZRBM(cjzrbm)
//                        .setCJGBM(orgCode)
//                        .setCJGMC(patient.getOrgName());
//                admissionCardInfoService.createMedicalCard(patientIdCardRegisterVo, jzrxx, userId);
//            }
//        }
//        return dataMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Mono<Map<String,Object>> liteFlowAddPatient(PatientAddVO patient) {
//        log.info("开始注册就诊卡-liteFlow");
//        AddPatientContext addPatientContext = new AddPatientContext()
//                .setPatient(patient).setTokenInfo(MyUserTokenInfo.getUserInfoThrowEx());
//        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp("add_patient", patient, addPatientContext);
//        if(liteflowResponse.isSuccess()) {
//            Map<String, Object> result = new HashMap<>();
//            result.put("CJZRBM", addPatientContext.getCurrentPatient().getCJZRBM());
//            return Mono.create(sink -> sink.success(result));
//        }
//        return Mono.error(new RuntimeException("新增就诊人失败"));
//    }
//
//    @Override
//    public String getPriorityPatientPhone() {
//        String userId = MyUserTokenInfo.getUserInfoThrowEx().getUserId();
//        return this.baseMapper.getPriorityPatientPhone(userId);
//    }
//
//    @Override
//    public PatientExtendVO getPriorityPatient(boolean error) {
//        String userId = MyUserTokenInfo.getUserInfoThrowEx().getUserId();
//        // 查询
//        Optional<PatientExtendVO> priorityPatient = this.baseMapper.getPriorityPatient(userId);
//        // 是否抛出异常
//        if (error) {
//            return priorityPatient.orElseThrow(() -> new RuntimeException("此账号下未查询到已添加的就诊人"));
//        } else {
//            return priorityPatient.orElse(new PatientExtendVO());
//        }
//    }
//
//    @Override
//    public List<PatientUserDTO> getPatientInfo(String idCardType, String idCard, String userId) {
//        //根据证件号码 + 证件类型 查询
//        List<PatientUserDTO> patientUserDtos = this.baseMapper.listPatientWithUserId(
//                Wrappers.<InformationPatientPo>query()
//                        .eq(StrUtil.isNotEmpty(userId), "gl.CYHZHID", userId).or(StrUtil.isNotEmpty(userId))
//                        .eq("jzr.CZJLX", idCardType)
//                        .eq("jzr.CJZRSFZH", idCard)
//        );
//        if (CollUtil.isEmpty(patientUserDtos)) {
//            return null;
//        }
//        return patientUserDtos;
//    }
//
//    public boolean upDefault(String userId) {
//        return patientConnectionService.update(
//                null,
//                Wrappers.<PatientConnectionPO>lambdaUpdate()
//                        .set(PatientConnectionPO::getISFMR, 0)
//                        .eq(PatientConnectionPO::getISFMR, 1)
//                        .eq(PatientConnectionPO::getCYHZHID, userId)
//        );
//    }
//
//    /**
//     * 保存就诊人“本人”数据
//     *
//     * @param jzrBm        就诊人编码
//     * @param patientVo    前端患者信息VO
//     * @param patientData  数据库保存就诊人信息
//     * @return
//     */
//    private PatientUserDTO saveOneself(String jzrBm, PatientAddVO patientVo, PatientUserDTO patientData) {
//        //证件类型
//        String idType = String.valueOf(patientVo.getCZJLX());
//        //证件号码
//        String idNo = String.valueOf(patientVo.getCJZRSFZH());
//        //获取出生年月
//        Date csny = DateUtil.parse(patientVo.getDCSNY(), DatePattern.NORM_DATETIME_FORMAT);
//        if ("01".equals(idType)) {
//            csny = IDCardUtil.idToDate(patientVo.getCJZRSFZH());
//        }
//
//        /***保存就诊人数据***/
//        int savePatientRes;
//        if (null == patientData) {
//            //mr 其它属性值
//            patientData = BeanUtil.copyProperties(patientVo, PatientUserDTO.class);
//            patientData.setCJZRBM(jzrBm)
//                    .setCJZRSFZH(idNo).setCZJLX(idType)
//                    .setDCSNY(csny).setDXGSJ(new Date())
//                    .setDCJSJ(new Date());
//            savePatientRes = this.baseMapper.insert(patientData);
//        } else {
//            //mr 其它属性值
//            BeanUtil.copyProperties(patientVo, patientData);
//            //修改就诊人信息
//            patientData.setCJZRBM(jzrBm)
//                    .setCJZRSFZH(idNo).setCZJLX(idType)
//                    .setDCSNY(csny).setDXGSJ(new Date());
//            savePatientRes = this.baseMapper.updateById(patientData);
//        }
//
//        TokenInfoPo userInfoForToken = MyUserTokenInfo.getUserInfoThrowEx();
//
//        /***保存实名结果(没有走真正面部识别，但是校验了用户身份真实性，这里也默认保存一条实名记录)***/
//        RealRecordPO authRecord = new RealRecordPO();
//        authRecord.setCBM(SnowflakeIdWorker.getInstance().nextIdStr()).setCJZRBM(jzrBm)
//                .setCSSYWBM(userInfoForToken.getUserAuthorizeCode()).setIYWLY(1)
//                .setISMJG(3)
//                .setCSHYJ("已通过实名系统校验真实性").setDCJSJ(new Date());
//        int saveAuthRes = realRecordMapper.insert(authRecord);
//
//
//        /***保存就诊人信息、实名信息成功后  开始更新用户基础信息【添加本人就诊人数据成功，这里将本人真实数据向上抽取，保存到用户基础信息表中】***/
//        if (savePatientRes > 0 && saveAuthRes > 0) {
//            try {
//                /***更新用户就诊人关联关系【存在同一个用户拥有多个微信号情况，并且这多个微信号将同一个人绑定为本人情况，此时向上抽取的多条用户基础信息会合并成一条，
//                 * 抽取规则：1⃣️ 同一个用户【先】绑定本人关系就诊人数据账号继续使用，
//                 *         2⃣️ 【后】绑定本人就诊人数据的账号将其数据更新 并 关联到上一步骤到用户基础信息上
//                 所以：此处需要更新用户（更新成一次绑定就诊人的那个用户编码）就诊人关联关系表***/
//
//                //构建当前微信对新用户编码查询条件
//                QueryWrapper<PatientConnectionPO> newPatientUserWrapper = new QueryWrapper<>();
//                newPatientUserWrapper.eq("CYHBM", userInfoForToken.getUserCode());
//                //保存用户就诊人信息关联数据
//                PatientConnectionPO wlylYhjzrxxgl = BeanUtil.copyProperties(patientVo, PatientConnectionPO.class);
//                wlylYhjzrxxgl.setCBM(SnowflakeIdWorker.getInstance().nextIdStr());
//                wlylYhjzrxxgl.setCJZRBM(jzrBm);
//                wlylYhjzrxxgl.setCGXBM("0");
//                wlylYhjzrxxgl.setCYHZHID(userInfoForToken.getUserId());
//                wlylYhjzrxxgl.setDCJSJ(new Date());
//                if (patientConnectionService.save(wlylYhjzrxxgl)) {
//                    throw new RuntimeException("用户基础信息合并保存失败");
//                }
//
//            } catch (Exception e) {
//                throw new RuntimeException("用户基础信息合并异常");
//            }
//        }
//        return patientData;
//    }
//
//    /**
//     * 查询是否已经存在就诊卡，存在就将就诊人和卡关联
//     *
//     * @param orgCode           机构编码
//     * @param userId            用户id
//     * @param patientIdCardEnum 就诊卡类型枚举
//     * @return 卡存在返回true
//     */
//    private Boolean patientJointCard(String patientCode, String orgCode, String userId, PatientIdCardEnum patientIdCardEnum, Integer defaultPerson) {
//        // 1 卡信息不存在返回 false
//        AdmissionCardInfoPO admissionCardInfoPo = admissionCardInfoService.getOne(
//                Wrappers.<AdmissionCardInfoPO>lambdaQuery()
//                        .eq(AdmissionCardInfoPO::getCJGBM, orgCode)
//                        .eq(AdmissionCardInfoPO::getCJZRBM, patientCode)
//                        .eq(AdmissionCardInfoPO::getIKLX, patientIdCardEnum.getCardType())
//        );
//
//        if (admissionCardInfoPo == null) {
//            return false;
//        }
//        // 2 查询是否存在关联
//        VisitCardAssociationPO associationPo = visitCardAssociationService.getOne(
//                Wrappers.<VisitCardAssociationPO>lambdaQuery()
//                        .eq(VisitCardAssociationPO::getCYHZHID, userId)
//                        .eq(VisitCardAssociationPO::getCZHJZKGLID, admissionCardInfoPo.getCBM())
//        );
//        // 3 不存在直接添加
//        if (associationPo == null) {
//            // 如果存在就重新关联人和卡
//            visitCardAssociationService.save(new VisitCardAssociationPO()
//                    .setCZHJZKGLID(SnowflakeIdWorker.getInstance().nextIdStr())
//                    .setCJZKID(admissionCardInfoPo.getCBM())
//                    .setCYHZHID(userId)
//                    .setISFMR(defaultPerson)
//                    .setDCJSJ(new Date()));
//        } else if (!ObjectUtil.equals(defaultPerson, associationPo.getISFMR())) {
//            // 4 存在但是否默认不一致时进行更新
//            visitCardAssociationService.updateById(
//                    associationPo
//                            .setISFMR(defaultPerson)
//                            .setDCJSJ(new Date())
//            );
//        }
//        return true;
//    }
//}
