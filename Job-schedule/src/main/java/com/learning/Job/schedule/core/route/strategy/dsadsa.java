package com.learning.Job.schedule.core.route.strategy;

import java.util.Iterator;
import java.util.List;

public class ExecutorRouteBusyover extends ExecutorRouter {
    public ExecutorRouteBusyover() {
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        StringBuffer idleBeatResultSB = new StringBuffer();
        Iterator var4 = addressList.iterator();

        while(var4.hasNext()) {
            String address = (String)var4.next();
            ReturnT<String> idleBeatResult = null;

            try {
                ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
                idleBeatResult = executorBiz.idleBeat(triggerParam.getJobId());
            } catch (Exception var8) {
                Exception e = var8;
                logger.error(e.getMessage(), e);
                idleBeatResult = new ReturnT(500, "" + e);
            }

            idleBeatResultSB.append(idleBeatResultSB.length() > 0 ? "<br><br>" : "").append(I18nUtil.getString("jobconf_idleBeat") + "：").append("<br>address：").append(address).append("<br>code：").append(idleBeatResult.getCode()).append("<br>msg：").append(idleBeatResult.getMsg());
            if (idleBeatResult.getCode() == 200) {
                idleBeatResult.setMsg(idleBeatResultSB.toString());
                idleBeatResult.setContent(address);
                return idleBeatResult;
            }
        }

        return new ReturnT(500, idleBeatResultSB.toString());
    }
}