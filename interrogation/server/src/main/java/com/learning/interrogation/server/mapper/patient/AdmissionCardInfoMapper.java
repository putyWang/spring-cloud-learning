package com.learning.interrogation.server.mapper.patient;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.interrogation.domain.po.patient.AdmissionCardInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 就诊卡信息表
 * 
 * @author cc
 * @email yanhua@chinaforwards.com
 * @date 2019-11-19 15:54:07
 */
@Mapper
public interface AdmissionCardInfoMapper extends BaseMapper<AdmissionCardInfoPO> {

    /**
     * 校验 用户 在当前机构下是否存在 电子就诊卡 或者就诊卡
     * @param yhbm
     * @param jgbm
     * @return
     */
    int checkPatientIDCardIsExist(@Param("yhbm") String yhbm, @Param("jgbm") String jgbm);
}
