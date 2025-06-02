package com.learning.interrogation.server.mapper.patient;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.interrogation.domain.dto.PatientUserDTO;
import com.learning.interrogation.domain.po.patient.InformationPatientPo;
import com.learning.interrogation.domain.vo.PatientExtendVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 就诊人信息表
 * 
 * @author cc
 * @email yanhua@chinaforwards.com
 * @date 2019-10-17 09:32:55
 */
@Mapper
public interface InformationPatientMapper extends BaseMapper<InformationPatientPo> {

    /**
     * 获取优先选择的就诊人。
     * 优先顺序：默认人>本人>第一个
     *
     * @param userId 账号id
     * @return 就诊人信息扩展对象
     */
    Optional<PatientExtendVO> getPriorityPatient(@Param("userId") String userId);


    /**
     * 获取优先选择的就诊人的手机号
     * 优先顺序：默认人>本人>第一个
     *
     * @param userId 用户账号id
     * @return 返回就诊人的手机号，没有时返回null
     * @author huanghaoran
     */
    String getPriorityPatientPhone(@Param("userId") String userId);

    /**
     * 携带用户 id 的就诊人信息列表查询
     * @param qw 查询条件
     * @return 就诊人条件列表
     */
    List<PatientUserDTO> listPatientWithUserId(@Param("ew") LambdaQueryWrapper<InformationPatientPo> qw);

}
