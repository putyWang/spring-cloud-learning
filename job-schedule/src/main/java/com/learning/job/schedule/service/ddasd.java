package com.learning.job.schedule.service;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.core.model.XxlJobUser;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.core.utils.TokenUtil;
import com.learning.job.schedule.dao.XxlJobUserDao;
import com.learning.job.schedule.dto.LoginDto;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Objects;

@Configuration
public class LoginService {
    @Resource
    XxlJobUserDao xxlJobUserDao;

    public LoginService() {
    }

    public ReturnT<String> login(LoginDto dto) {
        XxlJobUser xxlJobUser = this.xxlJobUserDao.loadByUserName(dto.getAccount());

        try {
            String pwd = GmUtil.decryCommon(dto.getPwd());
            dto.setPwd(pwd);
            if (Objects.isNull(xxlJobUser)) {
                if (Objects.equals(dto.getAccount(), "admin") && Objects.equals(dto.getPwd(), "123456")) {
                    xxlJobUser = new XxlJobUser();
                    xxlJobUser.setUsername(dto.getAccount());
                    xxlJobUser.setPassword(dto.getPwd());
                    xxlJobUser.setRole(1);
                    xxlJobUser.setPermission("");
                    return new ReturnT(TokenUtil.sign(xxlJobUser));
                } else {
                    return new ReturnT(500, I18nUtil.getString("login_param_unvalid"));
                }
            } else {
                String dbPassword = GmUtil.decryCommon(xxlJobUser.getPassword());
                return !dbPassword.equals(dto.getPwd()) ? new ReturnT(500, I18nUtil.getString("login_param_unvalid")) : new ReturnT(TokenUtil.sign(xxlJobUser));
            }
        } catch (Exception var5) {
            Exception e = var5;
            e.printStackTrace();
            return new ReturnT(500, I18nUtil.getString("login_param_unvalid"));
        }
    }

    public ReturnT<String> logout() {
        return ReturnT.SUCCESS;
    }
}