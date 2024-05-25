package com.learning.job.schedule.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.dao.XxlJobRegistryDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/jobgroup"})
public class JobGroupController {
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    public JobGroupController() {
    }

    @RequestMapping
    @ResponseBody
    public ReturnT<Map<String, Object>> pageList(@RequestParam(required = false,defaultValue = "0") int start, @RequestParam(required = false,defaultValue = "10") int length) {
        List<XxlJobGroup> list = this.xxlJobGroupDao.pageList(start == 0 ? 0 : start * length, length);
        int list_count = this.xxlJobGroupDao.findAllCount();
        Map<String, Object> maps = new HashMap();
        maps.put("recordsTotal", list_count);
        maps.put("recordsFiltered", list_count);
        maps.put("data", list);
        return new ReturnT(maps);
    }

    @RequestMapping({"/save"})
    @ResponseBody
    public ReturnT<String> save(XxlJobGroup xxlJobGroup) {
        if (xxlJobGroup.getAppName() != null && xxlJobGroup.getAppName().trim().length() != 0) {
            if (xxlJobGroup.getAppName().length() >= 4 && xxlJobGroup.getAppName().length() <= 64) {
                if (xxlJobGroup.getTitle() != null && xxlJobGroup.getTitle().trim().length() != 0) {
                    if (xxlJobGroup.getAddressType() != 0) {
                        label59: {
                            if (xxlJobGroup.getAddressList() != null && xxlJobGroup.getAddressList().trim().length() != 0) {
                                String[] addresss = xxlJobGroup.getAddressList().split(",");
                                String[] var3 = addresss;
                                int var4 = addresss.length;
                                int var5 = 0;

                                while(true) {
                                    if (var5 >= var4) {
                                        break label59;
                                    }

                                    String item = var3[var5];
                                    if (item == null || item.trim().length() == 0) {
                                        return new ReturnT(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                                    }

                                    ++var5;
                                }
                            }

                            return new ReturnT(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
                        }
                    }

                    int ret = this.xxlJobGroupDao.save(xxlJobGroup);
                    return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
                } else {
                    return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
                }
            } else {
                return new ReturnT(500, I18nUtil.getString("jobgroup_field_appName_length"));
            }
        } else {
            return new ReturnT(500, I18nUtil.getString("system_please_input") + "AppName");
        }
    }

    @RequestMapping({"/update"})
    @ResponseBody
    public ReturnT<String> update(XxlJobGroup xxlJobGroup) {
        if (xxlJobGroup.getAppName() != null && xxlJobGroup.getAppName().trim().length() != 0) {
            if (xxlJobGroup.getAppName().length() >= 4 && xxlJobGroup.getAppName().length() <= 64) {
                if (xxlJobGroup.getTitle() != null && xxlJobGroup.getTitle().trim().length() != 0) {
                    if (xxlJobGroup.getAddressType() == 0) {
                        List<String> registryList = this.findRegistryByAppName(xxlJobGroup.getAppName());
                        String addressListStr = null;
                        if (registryList != null && !registryList.isEmpty()) {
                            Collections.sort(registryList);
                            addressListStr = "";

                            String item;
                            for(Iterator var10 = registryList.iterator(); var10.hasNext(); addressListStr = addressListStr + item + ",") {
                                item = (String)var10.next();
                            }

                            addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
                        }

                        xxlJobGroup.setAddressList(addressListStr);
                    } else {
                        label82: {
                            if (xxlJobGroup.getAddressList() != null && xxlJobGroup.getAddressList().trim().length() != 0) {
                                String[] addresss = xxlJobGroup.getAddressList().split(",");
                                String[] var3 = addresss;
                                int var4 = addresss.length;
                                int var5 = 0;

                                while(true) {
                                    if (var5 >= var4) {
                                        break label82;
                                    }

                                    String item = var3[var5];
                                    if (item == null || item.trim().length() == 0) {
                                        return new ReturnT(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                                    }

                                    ++var5;
                                }
                            }

                            return new ReturnT(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
                        }
                    }

                    int ret = this.xxlJobGroupDao.update(xxlJobGroup);
                    return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
                } else {
                    return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
                }
            } else {
                return new ReturnT(500, I18nUtil.getString("jobgroup_field_appName_length"));
            }
        } else {
            return new ReturnT(500, I18nUtil.getString("system_please_input") + "AppName");
        }
    }

    private List<String> findRegistryByAppName(String appNameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap();
        List<XxlJobRegistry> list = this.xxlJobRegistryDao.findAll(90, new Date());
        if (list != null) {
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
                XxlJobRegistry item = (XxlJobRegistry)var4.next();
                if (RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appName = item.getRegistryKey();
                    List<String> registryList = (List)appAddressMap.get(appName);
                    if (registryList == null) {
                        registryList = new ArrayList();
                    }

                    if (!((List)registryList).contains(item.getRegistryValue())) {
                        ((List)registryList).add(item.getRegistryValue());
                    }

                    appAddressMap.put(appName, registryList);
                }
            }
        }

        return (List)appAddressMap.get(appNameParam);
    }

    @RequestMapping({"/remove"})
    @ResponseBody
    public ReturnT<String> remove(int id) {
        int count = this.xxlJobInfoDao.pageListCount(0, 10, id, -1, (String)null, (String)null, (String)null);
        if (count > 0) {
            return new ReturnT(500, I18nUtil.getString("jobgroup_del_limit_0"));
        } else {
            List<XxlJobGroup> allList = this.xxlJobGroupDao.findAll();
            if (allList.size() == 1) {
                return new ReturnT(500, I18nUtil.getString("jobgroup_del_limit_1"));
            } else {
                int ret = this.xxlJobGroupDao.remove(id);
                return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
            }
        }
    }

    @RequestMapping({"/loadById"})
    @ResponseBody
    public ReturnT<XxlJobGroup> loadById(int id) {
        XxlJobGroup jobGroup = this.xxlJobGroupDao.load(id);
        return jobGroup != null ? new ReturnT(jobGroup) : new ReturnT(500, (String)null);
    }
}
