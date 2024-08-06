package com.learning.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.learning.core.domain.model.RoleModel;
import com.learning.system.mapper.UserRoleMapper;
import com.learning.system.model.dto.UserRoleDto;
import com.learning.system.model.entity.UserRoleEntity;
import com.learning.system.service.UserRoleService;
import com.learning.web.service.impl.BaseServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl
        extends BaseServiceImpl<UserRoleMapper, UserRoleEntity, UserRoleDto>
        implements UserRoleService {

    @Override
    public List<RoleModel> listRoleByUserId(Long userId) {
        return this.baseMapper.selectRoleByUserId(userId);
    }

    @Override
    public void bindRole(UserRoleEntity userRole) {

        //获取之前用户绑定的用户角色对象
        List<UserRoleEntity> userRoleEntities = this.baseMapper.selectList(new QueryWrapper<UserRoleEntity>().lambda()
                .eq(UserRoleEntity::getUserId, userRole.getUserId()));

        //用户已经绑定了角色
        if(! CollectionUtils.isEmpty(userRoleEntities)){
            UserRoleEntity userRoleEntity = userRoleEntities.get(0);
            //角色未变
            if(userRoleEntity.getRoleId().equals(userRole.getRoleId())) {
                return;
            }else {
                //更新角色
                userRoleEntity.setRoleId(userRole.getRoleId());
                this.baseMapper.updateById(userRoleEntity);
            }
        } else {
            //用户未绑定角色
            this.baseMapper.insert(userRole);
        }
    }
}
