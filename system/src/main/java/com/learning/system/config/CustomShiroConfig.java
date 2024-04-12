package com.learning.system.config;

import com.learning.core.config.shiro.CommonRealm;
import com.learning.core.config.shiro.ShiroConfig;
import org.springframework.context.annotation.Bean;


public class CustomShiroConfig extends ShiroConfig {

    /**
     * 注入认证 realm
     *
     * @return
     */
    @Override
    protected CommonRealm generateRealm() {
        return new CustomRealm();
    }
}
