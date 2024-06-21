package com.learning.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.learning.core.exception.LearningException;
import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.CommonBeanUtil;
import com.learning.core.utils.MD5Utils;
import com.learning.core.utils.StringUtils;
import com.learning.system.constant.SysConstant;
import com.learning.system.model.dto.UserDto;
import com.learning.system.model.entity.UserEntity;
import com.learning.system.model.entity.UserRoleEntity;
import com.learning.system.mapper.UserMapper;
import com.learning.system.service.UserRoleService;
import com.learning.system.service.UserService;
import com.learning.web.eums.BaseOperationEnum;
import com.learning.web.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl
        extends BaseServiceImpl<UserMapper, UserEntity, UserDto>
        implements UserService {

    @Resource
    private UserRoleService userRoleService;

    @Override
    @Transactional(
            rollbackFor = Exception.class
    )
    public void registered(UserDto userDto){
        //保存用户基本信息
        UserEntity user = new UserEntity();
        CommonBeanUtil.copyAndFormat(user, userDto);
        this.insert(user);
        //将用户角色设置为普通用户
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(user.getId());
        userRole.setRoleId(0L);
        userRoleService.save(userRole);
    }

    @Override
    public UserEntity getByUserName(String userName){
        List<UserEntity> userEntities = this.baseMapper.selectList(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getUsername, userName));

        if (CollectionUtils.isEmpty(userEntities)) {
            throw new LearningException("用户名不存在");
        }

        return userEntities.get(0);
    }

    @Override
    public void processBeforeOperation(UserEntity userEntity, BaseOperationEnum baseOperationEnum) {
        String password = userEntity.getPassword();
        
        switch (baseOperationEnum) {
            case INSERT:
            case BATCH_INSERT:
                //设置默认密码
                if (StringUtils.isEmpty(password)) {
                    password = SysConstant.DEFAULT_PASSWORD;
                }
                userEntity.setPassword(MD5Utils.encrypt(password));

                //设置账号创建时间
                userEntity.setCreateTime(new Date());
                break;
            case UPDATE:
                //更新密码
                if (! StringUtils.isEmpty(password)) {
                    userEntity.setPassword(MD5Utils.encrypt(password));
                }
                break;
        }
    }
}
