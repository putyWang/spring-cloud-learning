package com.learning.interrogation.domain.po.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@Accessors(chain = true)
@TableName("TB_WLYL_YHZHXX")
public class UserInfoPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String userId;

    private String userCode;

    private String name;

    private String avatar;

    private String phone;

    private String password;

    private Integer userStatus;

    private String cancelReason;

    private Date userLastTime;

    private Date createTime;

    private Date updateTime;

    private Date logoutDateTime;

    /**
     * 操作人
     * */
    private String operateUser;

    private String token;
}
