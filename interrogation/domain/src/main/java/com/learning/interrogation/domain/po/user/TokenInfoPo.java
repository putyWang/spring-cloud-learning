package com.learning.interrogation.domain.po.user;

import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * cToken
 *
 * @author wr
 * @date 2019-11-26
 */
@Data
public class TokenInfoPo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户帐号ID
     */
    private String userId;

    /**
     * 用户授权编码
     */
    private String userAuthorizeCode;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * openId
     */
    private String openId;

    /**
     * 第三方平台类型
     */
    private Integer thirdPlatformType;

    /**
     * cToken 生成日期点 时间戳
     */
    private Date createTime;

    public static Builder build(){
        return new Builder();
    }

    public static class Builder {
        @Setter
        @Accessors(chain = true)
        private UserInfoPO userInfoPO;

        public TokenInfoPo build() {
            TokenInfoPo tokenInfoPo = new TokenInfoPo();
            tokenInfoPo.userId = userInfoPO.getUserId();
            tokenInfoPo.createTime = new Date();
            return tokenInfoPo;
        }
    }

}
