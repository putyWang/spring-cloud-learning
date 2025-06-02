package com.learning.interrogation.server.service.user.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.interrogation.domain.po.user.UserInfoPO;
import com.learning.interrogation.server.mapper.user.UserInfoMapper;
import com.learning.interrogation.server.service.user.UserInfoService;
import com.learning.interrogation.server.util.AesUtil;
import com.learning.interrogation.server.util.SnowflakeIdUtil;
import com.learning.interrogation.domain.po.user.TokenInfoPo;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/2 下午5:20
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl
        extends ServiceImpl<UserInfoMapper, UserInfoPO>
        implements UserInfoService {

    private final SnowflakeIdUtil snowflakeIdUtil;

    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public List<UserInfoPO> updateToken() {
        List<UserInfoPO> list = this.list();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        UserInfoMapper mapper = sqlSession.getMapper(UserInfoMapper.class);
        list.forEach(
                user -> {
                    try {

                        TokenInfoPo tokenInfo = TokenInfoPo.build()
                                .setUserInfoPO(user.setUserId(snowflakeIdUtil.getSnowflakeId()))
                                .build();
                        String tokenJSON = JSON.toJSONString(tokenInfo);
                        user.setToken(AesUtil.encrypt(tokenJSON));
                        mapper.updateIdByUserCode(user);
                    } catch (Exception e) {
                        throw new RuntimeException("授权信息异常", e);
                    }
                }
        );
        sqlSession.commit();
        return list;
    }
}
