package com.learning.interrogation.server.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.learning.interrogation.domain.po.user.TokenInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: UserTokenInfo
 * @Description: token封装成对象返回
 * @Author: wr
 * @Date: 2019/12/05 14:38
 * @Version V1.0
 **/
@Slf4j
@Component
public class MyUserTokenInfo {

    /**
     * @return com.yanhua.ed.aes.UserInfoBean
     * @Author wr
     * @Description 根据主线程拦截的cToken获取微信用户信息   推荐使用
     * @Date 2019/12/05 14:38
     * @UpdateUser:    
     * @UpdateDate:     2019/12/05 14:38
     * @UpdateRemark:  
     * @Param []
     **/
    public static TokenInfoPo getUserInfoForToken() {
        String token = TokenContext.getToken();
        return decryptToken(token);
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    public static TokenInfoPo getUserInfoThrowEx() {
        try {
            return getUserInfoForToken();
        } catch (Exception e) {
            throw new RuntimeException("当前用户信息获取失败", e);
        }
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    public static TokenInfoPo getUserInfo() {
        try {
            return getUserInfoForToken();
        } catch (Exception e) {
            log.error("当前用户信息获取失败", e);
            return null;
        }
    }

    /**
     * 当前 请求是否存在 居民端 token
     *
     * @return
     */
    public static boolean isClientTokenExist() {
        try {
            return ! StrUtil.isEmpty(TokenContext.getToken());
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 解密CTOKEN
     *
     * @return
     */
    private static TokenInfoPo decryptToken(String token) {
        try {
            //解密cToken信息
            String tokenText = AesUtil.decrypt(token);
            //转TOKEN对象
            TokenInfoPo tokenInfoPo = JSON.parseObject(tokenText, TokenInfoPo.class);
            Assert.notNull(tokenInfoPo, "授权信息异常！");
            return tokenInfoPo;
        } catch (Exception e) {
            throw new RuntimeException("授权信息异常", e);
        }
    }
}
