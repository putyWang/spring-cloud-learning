package com.learning.job.schedule.controller;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.model.XxlJobRegistry;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.dao.XxlJobGroupDao;
import com.learning.job.schedule.dao.XxlJobInfoDao;
import com.learning.job.schedule.dao.XxlJobRegistryDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping({"/jobgroup/v2"})
public class JobGroupV2Controller {
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    public JobGroupV2Controller() {
    }

    @RequestMapping
    public String index(Model model) {
        List<XxlJobGroup> list = this.xxlJobGroupDao.findAll();
        model.addAttribute("list", list);
        return "jobgroup/jobgroup.index";
    }

    @RequestMapping({"/save"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> save(@RequestBody XxlJobGroup xxlJobGroup) {
        ReturnT<String> x = this.groupValid(xxlJobGroup);
        if (x != null) {
            return x;
        } else {
            int byappName = this.xxlJobGroupDao.findByappName(xxlJobGroup.getAppName());
            if (byappName > 0) {
                return new ReturnT(500, I18nUtil.getString("服务名称已存在"));
            } else {
                if (xxlJobGroup.getAddressType() != 0) {
                    label52: {
                        if (xxlJobGroup.getAddressList() != null && xxlJobGroup.getAddressList().trim().length() != 0) {
                            String[] addresss = xxlJobGroup.getAddressList().split(",");
                            String[] var5 = addresss;
                            int var6 = addresss.length;
                            int var7 = 0;

                            while(true) {
                                if (var7 >= var6) {
                                    break label52;
                                }

                                String item = var5[var7];
                                if (item == null || item.trim().length() == 0) {
                                    return new ReturnT(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                                }

                                ++var7;
                            }
                        }

                        return new ReturnT(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
                    }
                }

                int ret = this.xxlJobGroupDao.save(xxlJobGroup);
                return ret > 0 ? new ReturnT(xxlJobGroup.getId()) : ReturnT.FAIL;
            }
        }
    }

    private ReturnT<String> groupValid(@RequestBody XxlJobGroup xxlJobGroup) {
        if (xxlJobGroup.getAppName().length() >= 4 && xxlJobGroup.getAppName().length() <= 64) {
            if (xxlJobGroup.getAppName() != null && xxlJobGroup.getAppName().trim().length() != 0) {
                return xxlJobGroup.getTitle() != null && xxlJobGroup.getTitle().trim().length() != 0 ? null : new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title"));
            } else {
                return new ReturnT(500, I18nUtil.getString("system_please_input") + "AppName");
            }
        } else {
            return new ReturnT(500, I18nUtil.getString("jobgroup_field_appName_length"));
        }
    }

    @RequestMapping({"/update"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> update(@RequestBody XxlJobGroup xxlJobGroup) {
        ReturnT<String> x = this.groupValid(xxlJobGroup);
        if (x != null) {
            return x;
        } else {
            int byappName = this.xxlJobGroupDao.notIdAppName(xxlJobGroup);
            if (byappName > 0) {
                return new ReturnT(500, I18nUtil.getString("服务名称已存在"));
            } else {
                if (xxlJobGroup.getAddressType() == 0) {
                    List<String> registryList = this.findRegistryByAppName(xxlJobGroup.getAppName());
                    String addressListStr = null;
                    if (registryList != null && !registryList.isEmpty()) {
                        Collections.sort(registryList);
                        addressListStr = "";

                        String item;
                        for(Iterator var12 = registryList.iterator(); var12.hasNext(); addressListStr = addressListStr + item + ",") {
                            item = (String)var12.next();
                        }

                        addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
                    }

                    xxlJobGroup.setAddressList(addressListStr);
                } else {
                    label74: {
                        if (xxlJobGroup.getAddressList() != null && xxlJobGroup.getAddressList().trim().length() != 0) {
                            String[] addresss = xxlJobGroup.getAddressList().split(",");
                            String[] var5 = addresss;
                            int var6 = addresss.length;
                            int var7 = 0;

                            while(true) {
                                if (var7 >= var6) {
                                    break label74;
                                }

                                String item = var5[var7];
                                if (item == null || item.trim().length() == 0) {
                                    return new ReturnT(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                                }

                                ++var7;
                            }
                        }

                        return new ReturnT(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
                    }
                }

                int ret = this.xxlJobGroupDao.update(xxlJobGroup);
                return ret > 0 ? ReturnT.SUCCESS : ReturnT.FAIL;
            }
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
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> remove(@RequestParam int id) {
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
    @PermissionLimit(
            limit = false
    )
    public ReturnT<XxlJobGroup> loadById(int id) {
        XxlJobGroup jobGroup = this.xxlJobGroupDao.load(id);
        return jobGroup != null ? new ReturnT(jobGroup) : new ReturnT(500, (String)null);
    }
}
