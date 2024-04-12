package com.learning.system.service.impl;

import com.learning.system.model.dto.RoleDto;
import com.learning.system.model.entity.RoleEntity;
import com.learning.system.mapper.RoleMapper;
import com.learning.system.service.RoleService;
import com.learning.web.eums.BaseOperationEnum;
import com.learning.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RoleServiceImpl
        extends BaseServiceImpl<RoleMapper, RoleEntity, RoleDto>
        implements RoleService {
}
