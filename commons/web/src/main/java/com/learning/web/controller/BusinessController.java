package com.learning.web.controller;

import com.learning.core.model.UserContext;
import com.learning.web.model.dto.BaseDto;
import com.learning.web.model.entity.BaseEntity;
import org.apache.shiro.SecurityUtils;

public abstract class BusinessController<T extends BaseEntity, D extends BaseDto>
        extends BaseController<T, D> {

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public UserContext getUser() {
        return (UserContext) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public Long getUserId() {
        return getUser().getUserId();
    }
}
