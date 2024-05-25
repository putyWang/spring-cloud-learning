package com.learning.job.schedule.service.impl;

import com.learning.job.biz.AdminBiz;
import com.learning.job.biz.model.HandleCallbackParam;
import com.learning.job.biz.model.RegistryParam;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.handler.IJobHandler;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobLog;
import com.learning.job.schedule.core.thread.JobTriggerPoolHelper;
import com.learning.job.schedule.core.trigger.TriggerTypeEnum;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.dao.XxlJobInfoDao;
import com.learning.job.schedule.dao.XxlJobLogDao;
import com.learning.job.schedule.dao.XxlJobRegistryDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@Log4j2
public class AdminBizImpl implements AdminBiz {
    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    public AdminBizImpl() {
    }

    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        Iterator var2 = callbackParamList.iterator();

        while(var2.hasNext()) {
            HandleCallbackParam handleCallbackParam = (HandleCallbackParam)var2.next();
            ReturnT<String> callbackResult = this.callback(handleCallbackParam);
            log.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}", new Object[]{callbackResult.getCode() == IJobHandler.SUCCESS.getCode() ? "success" : "fail", handleCallbackParam, callbackResult});
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        XxlJobLog log = this.xxlJobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT(500, "log item not found.");
        } else if (log.getHandleCode() > 0) {
            return new ReturnT(500, "log repeate callback.");
        } else {
            String callbackMsg = null;
            if (IJobHandler.SUCCESS.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
                XxlJobInfo xxlJobInfo = this.xxlJobInfoDao.loadById(log.getJobId());
                if (xxlJobInfo != null && xxlJobInfo.getChildJobId() != null && xxlJobInfo.getChildJobId().trim().length() > 0) {
                    callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_child_run") + "<<<<<<<<<<< </span><br>";
                    String[] childJobIds = xxlJobInfo.getChildJobId().split(",");

                    for(int i = 0; i < childJobIds.length; ++i) {
                        int childJobId = childJobIds[i] != null && childJobIds[i].trim().length() > 0 && this.isNumeric(childJobIds[i]) ? Integer.valueOf(childJobIds[i]) : -1;
                        if (childJobId > 0) {
                            JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, (String)null, (String)null, (String)null);
                            ReturnT<String> triggerChildResult = ReturnT.SUCCESS;
                            callbackMsg = callbackMsg + MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"), i + 1, childJobIds.length, childJobIds[i], triggerChildResult.getCode() == 200 ? I18nUtil.getString("system_success") : I18nUtil.getString("system_fail"), triggerChildResult.getMsg());
                        } else {
                            callbackMsg = callbackMsg + MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"), i + 1, childJobIds.length, childJobIds[i]);
                        }
                    }
                }
            }

            StringBuffer handleMsg = new StringBuffer();
            if (log.getHandleMsg() != null) {
                handleMsg.append(log.getHandleMsg()).append("<br>");
            }

            if (handleCallbackParam.getExecuteResult().getMsg() != null) {
                handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
            }

            if (callbackMsg != null) {
                handleMsg.append(callbackMsg);
            }

            log.setHandleTime(new Date());
            log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
            if (handleMsg.length() > 15000) {
                log.setHandleMsg(handleMsg.substring(0, 15000));
            } else {
                log.setHandleMsg(handleMsg.toString());
            }

            this.xxlJobLogDao.updateHandleInfo(log);
            return ReturnT.SUCCESS;
        }
    }

    private boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException var3) {
            return false;
        }
    }

    public ReturnT<String> registry(RegistryParam registryParam) {
        int ret = this.xxlJobRegistryDao.registryUpdate(new Date(), registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret < 1) {
            this.xxlJobRegistryDao.registrySave(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());
        }

        return ReturnT.SUCCESS;
    }

    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        this.xxlJobRegistryDao.registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        return ReturnT.SUCCESS;
    }
}

