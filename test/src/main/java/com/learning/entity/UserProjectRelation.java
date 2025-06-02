package com.learning.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/4/5 下午9:55
 */
@Data
@Accessors(chain = true)
public class UserProjectRelation {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 子项目表id
     */
    private Long projectId;

    /**
     * 项目类型角色id
     */
    private Long projRoleId;
}
