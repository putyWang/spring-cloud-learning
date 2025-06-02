package com.learning.interrogation.server.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.interrogation.domain.po.user.UserInfoPO;

import java.util.List;


public interface UserInfoService extends IService<UserInfoPO> {
    List<UserInfoPO> updateToken();
}
