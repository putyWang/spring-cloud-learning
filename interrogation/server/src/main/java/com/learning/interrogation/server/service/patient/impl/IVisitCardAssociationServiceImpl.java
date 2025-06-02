package com.learning.interrogation.server.service.patient.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.interrogation.domain.po.patient.VisitCardAssociationPO;
import com.learning.interrogation.server.mapper.patient.VisitCardAssociationMapper;
import com.learning.interrogation.server.service.patient.IVisitCardAssociationService;
import org.springframework.stereotype.Service;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/1 下午10:26
 */
@Service
public class IVisitCardAssociationServiceImpl
        extends ServiceImpl<VisitCardAssociationMapper, VisitCardAssociationPO>
        implements IVisitCardAssociationService {
}
