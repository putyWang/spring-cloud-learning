package com.learning.auth.vo;

import com.learning.core.holder.UserContext;
import lombok.Data;

@Data
public class LoginVo {

    private UserContext userInfo;

    private String token;
}
