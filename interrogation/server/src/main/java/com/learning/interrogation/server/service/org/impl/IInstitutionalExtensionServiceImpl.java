package com.learning.interrogation.server.service.org.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.interrogation.domain.constant.enums.PatientIdCardEnum;
import com.learning.interrogation.domain.po.org.InstitutionalExtensionPO;
import com.learning.interrogation.server.mapper.org.InstitutionalExtensionMapper;
import com.learning.interrogation.server.service.org.IInstitutionalExtensionService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/1 下午1:57
 */
@Service
public class IInstitutionalExtensionServiceImpl
        extends ServiceImpl<InstitutionalExtensionMapper, InstitutionalExtensionPO>
        implements IInstitutionalExtensionService {

    /**
     * 根据机构编码获取该机构开启的就诊卡类型
     *
     * @param orgCode 机构编码
     * @return 开启的就诊卡类型
     */
    @Override
    public Set<Integer> getPatientIdCardBusinessFields(String orgCode) {

        Map<String, Object> patientIdCardBusinessSwitch = JSON.parseObject(
                Optional.ofNullable(this.baseMapper.selectOne(
                                Wrappers.lambdaQuery(InstitutionalExtensionPO.class)
                                        .select(InstitutionalExtensionPO::getCJZKYW)
                                        .eq(InstitutionalExtensionPO::getCJGBM, orgCode)))
                        .orElseThrow(() -> new RuntimeException("未找到{" + orgCode + "}的机构扩展信息")).getCJZKYW()
        );
        Assert.notNull(patientIdCardBusinessSwitch, "就诊卡业务字段转换后为空");
        Set<Integer> switchList = new HashSet<>();
        patientIdCardBusinessSwitch.forEach((k, v) -> {
            if (1 == (int)v) {
                switchList.add(PatientIdCardEnum.getCardTypeByPropertyName(k));
            }
        });
        return switchList;

    }
}
