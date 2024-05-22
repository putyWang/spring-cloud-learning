package com.learning.Job.schedule.core.route.strategy;

import com.learning.Job.schedule.core.route.ExecutorRouter;
import com.learning.Job.schedule.core.utils.I18nUtil;

import java.util.Iterator;
import java.util.List;

public class ExecutorRouteFailover extends ExecutorRouter {
    public ExecutorRouteFailover() {
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        StringBuffer beatResultSB = new StringBuffer();
        Iterator var4 = addressList.iterator();

        while(var4.hasNext()) {
            String address = (String)var4.next();
            ReturnT<String> beatResult = null;

            try {
                ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
                beatResult = executorBiz.beat();
            } catch (Exception var8) {
                Exception e = var8;
                logger.error(e.getMessage(), e);
                beatResult = new ReturnT(500, "" + e);
            }

            beatResultSB.append(beatResultSB.length() > 0 ? "<br><br>" : "").append(I18nUtil.getString("jobconf_beat") + "：").append("<br>address：").append(address).append("<br>code：").append(beatResult.getCode()).append("<br>msg：").append(beatResult.getMsg());
            if (beatResult.getCode() == 200) {
                beatResult.setMsg(beatResultSB.toString());
                beatResult.setContent(address);
                return beatResult;
            }
        }

        return new ReturnT(500, beatResultSB.toString());
    }
}

