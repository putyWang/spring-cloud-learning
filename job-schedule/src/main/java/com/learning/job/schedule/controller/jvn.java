package com.learning.job.schedule.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.model.XxlJobUser;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.core.utils.TokenUtil;
import com.learning.job.schedule.dao.XxlJobGroupDao;
import com.learning.job.schedule.dao.XxlJobUserDao;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/user"})
public class UserController {
    @Resource
    private XxlJobUserDao xxlJobUserDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    public UserController() {
    }

    @RequestMapping
    @PermissionLimit(
            adminUser = true
    )
    public String index(Model model) {
        List<XxlJobGroup> groupList = this.xxlJobGroupDao.findAll();
        model.addAttribute("groupList", groupList);
        return "user/user.index";
    }

    @RequestMapping({"/pageList"})
    @ResponseBody
    @PermissionLimit(
            adminUser = true
    )
    public ReturnT<Map<String, Object>> pageList(@RequestParam(required = false,defaultValue = "0") int start, @RequestParam(required = false,defaultValue = "10") int length, String username, int role) {
        List<XxlJobUser> list = this.xxlJobUserDao.pageList(start, length, username, role);
        int list_count = this.xxlJobUserDao.pageListCount(start, length, username, role);
        Map<String, Object> maps = new HashMap();
        maps.put("recordsTotal", list_count);
        maps.put("recordsFiltered", list_count);
        maps.put("data", list);
        return new ReturnT(maps);
    }

    @RequestMapping({"/add"})
    @ResponseBody
    @PermissionLimit(
            adminUser = true
    )
    public ReturnT<String> add(XxlJobUser xxlJobUser) {
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("user_username"));
        } else {
            xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
            if (xxlJobUser.getUsername().length() >= 4 && xxlJobUser.getUsername().length() <= 20) {
                if (!StringUtils.hasText(xxlJobUser.getPassword())) {
                    return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("user_password"));
                } else {
                    xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
                    if (xxlJobUser.getPassword().length() >= 4 && xxlJobUser.getPassword().length() <= 20) {
                        xxlJobUser.setPassword(ByteUtils.toHexString(SM3Util.hash(xxlJobUser.getPassword().getBytes())));
                        XxlJobUser existUser = this.xxlJobUserDao.loadByUserName(xxlJobUser.getUsername());
                        if (existUser != null) {
                            return new ReturnT(500, I18nUtil.getString("user_username_repeat"));
                        } else {
                            this.xxlJobUserDao.save(xxlJobUser);
                            return ReturnT.SUCCESS;
                        }
                    } else {
                        return new ReturnT(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
                    }
                }
            } else {
                return new ReturnT(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
            }
        }
    }

    @RequestMapping({"/update"})
    @ResponseBody
    @PermissionLimit(
            adminUser = true
    )
    public ReturnT<String> update(HttpServletRequest request, XxlJobUser xxlJobUser) {
        XxlJobUser loginUser = TokenUtil.getClaim(request);
        if (loginUser.getUsername().equals(xxlJobUser.getUsername())) {
            return new ReturnT(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        } else {
            if (StringUtils.hasText(xxlJobUser.getPassword())) {
                xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
                if (xxlJobUser.getPassword().length() < 4 || xxlJobUser.getPassword().length() > 20) {
                    return new ReturnT(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
                }

                xxlJobUser.setPassword(ByteUtils.toHexString(SM3Util.hash(xxlJobUser.getPassword().getBytes())));
            } else {
                xxlJobUser.setPassword((String)null);
            }

            this.xxlJobUserDao.update(xxlJobUser);
            return ReturnT.SUCCESS;
        }
    }

    @RequestMapping({"/remove"})
    @ResponseBody
    @PermissionLimit(
            adminUser = true
    )
    public ReturnT<String> remove(HttpServletRequest request, int id) {
        XxlJobUser loginUser = TokenUtil.getClaim(request);
        if (loginUser.getId() == id) {
            return new ReturnT(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        } else {
            this.xxlJobUserDao.delete(id);
            return ReturnT.SUCCESS;
        }
    }

    @RequestMapping({"/updatePwd"})
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, String password) {
        if (password != null && password.trim().length() != 0) {
            password = password.trim();
            if (password.length() >= 4 && password.length() <= 20) {
                String sm3Password = ByteUtils.toHexString(SM3Util.hash(password.getBytes()));
                XxlJobUser loginUser = TokenUtil.getClaim(request);
                XxlJobUser existUser = this.xxlJobUserDao.loadByUserName(loginUser.getUsername());
                existUser.setPassword(sm3Password);
                this.xxlJobUserDao.update(existUser);
                return ReturnT.SUCCESS;
            } else {
                return new ReturnT(500, I18nUtil.getString("system_lengh_limit") + "[4-20]");
            }
        } else {
            return new ReturnT(ReturnT.FAIL.getCode(), "密码不可为空");
        }
    }
}

