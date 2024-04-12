package com.learning.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.core.annotation.UnionUnique;
import com.learning.core.annotation.UnionUniqueCode;
import com.learning.core.annotation.Unique;
import com.learning.web.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
@UnionUniqueCode(group = "user_role_id_index", code = "指定用户已存在目标角色")
public class UserRoleEntity extends BaseEntity {

    /**
     * 用户id
     */
    @UnionUnique(group = "user_role_index")
    private Long userId;

    /**
     * 角色id
     */
    @UnionUnique(group = "user_role_index")
    private Long roleId;
}
