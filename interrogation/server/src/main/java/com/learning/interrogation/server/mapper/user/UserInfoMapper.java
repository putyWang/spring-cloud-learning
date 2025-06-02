package com.learning.interrogation.server.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.interrogation.domain.po.user.UserInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoPO> {

    @Update("UPDATE TB_WLYL_YHZHXX SET user_id = #{userInfoPO.userId}, token = #{userInfoPO.token} WHERE user_code = #{userInfoPO.userCode};")
    int updateIdByUserCode(@Param("userInfoPO") UserInfoPO userInfoPO);
}
