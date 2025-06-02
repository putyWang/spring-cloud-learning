package com.learning.interrogation.server.service.org;


import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.interrogation.domain.po.org.InstitutionalExtensionPO;

import java.util.Set;

/**
 * @ClassName: TbWlylJgxxkzService
 * @Description: 机构信息扩展
 * @Author:
 * @Date: 2019-09-29
 * @Version V1.0
 **/
public interface IInstitutionalExtensionService extends IService<InstitutionalExtensionPO> {


    /**
     * 根据机构编码获取该机构开启的就诊卡类型
     *
     * @param orgCode 机构编码
     * @return 开启的就诊卡类型
     */
    Set<Integer> getPatientIdCardBusinessFields(String orgCode);
}