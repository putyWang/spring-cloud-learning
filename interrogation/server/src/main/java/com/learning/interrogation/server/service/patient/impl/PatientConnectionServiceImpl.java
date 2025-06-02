package com.learning.interrogation.server.service.patient.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.interrogation.domain.po.patient.PatientConnectionPO;
import com.learning.interrogation.server.service.patient.PatientConnectionService;
import com.learning.interrogation.server.mapper.patient.PatientConnectionMapper;
import org.springframework.stereotype.Service;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/4/30 下午11:04
 */
@Service
public class PatientConnectionServiceImpl
        extends ServiceImpl<PatientConnectionMapper, PatientConnectionPO>
        implements PatientConnectionService {
}
