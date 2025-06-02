//package com.learning.service;
//
//import com.learning.entity.po.InformationPatientPO;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author wangwei
// * @version 1.0
// * @date 2025/4/30 下午10:08
// */
//public interface InformationPatientService extends IService<InformationPatientPO> {
//
//    /**
//     * @return java.lang.Boolean
//     * @throws
//     * @Description 添加就诊人
//     * @Date 2020/8/11 9:37
//     * @Param [patient] 就诊人信息
//     */
//    Map<String,Object> addPatient(PatientAddVO patient);
//
//    /**
//     * 使用 liteFlow 添加就诊人
//     * @param patient
//     * @return
//     */
//    Mono<Map<String,Object>> liteFlowAddPatient(PatientAddVO patient);
//
//    /**
//     * 获取优先选择的就诊人的手机号
//     * 优先顺序：默认人>本人>第一个
//     *
//     * @return 返回就诊人的手机号，没有时返回null
//     */
//    String getPriorityPatientPhone();
//
//    /**
//     * 获取优先选择的就诊人信息。
//     * 优先顺序：默认人>本人>第一个
//     *
//     * 根据error参数可能返回空属性对象
//     * @param error 是否抛出为空异常.
//     *             true : 当查询的结果为空时，会抛出异常信息“此账号为查询到就诊人”。
//     *             false ： 当查询的结果为空时，会new一个空的PatientExtendVo对象返回
//     * @return 就诊人信息扩展对象，根据error参数可能返回空属性对象
//     * @author huanghaoran
//     * @throws Exception 获取用户信息失败
//     */
//    PatientExtendVO getPriorityPatient(boolean error);
//
//    /**
//     * 获取关联属性信息
//     * @param idCardType 证件类型
//     * @param idCard 证件号
//     * @param userId 用户 id
//     * @return 获取关联就诊人信息
//     */
//    List<PatientUserDTO> getPatientInfo(String idCardType, String idCard, String userId);
//}
