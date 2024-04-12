package com.learning.system.service.impl;

import com.learning.core.model.RoleModel;
import com.learning.core.utils.CollectionUtils;
import com.learning.system.model.dto.RolePermissionDto;
import com.learning.system.model.entity.RolePermissionEntity;
import com.learning.system.mapper.RolePermissionMapper;
import com.learning.system.service.RolePermissionService;
import com.learning.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolePermissionServiceImpl
        extends BaseServiceImpl<RolePermissionMapper, RolePermissionEntity, RolePermissionDto>
        implements RolePermissionService {

    @Override
    public List<RoleModel> listByRoleIds(List<Long> roleIdList) {
        return CollectionUtils.isEmpty(roleIdList) ? new ArrayList<>() : this.baseMapper.selectListByRoleIds(roleIdList);
    }
}
