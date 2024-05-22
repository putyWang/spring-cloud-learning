package com.learning.Job.schedule.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import java.util.List;

public class ExecutorRouteLast extends ExecutorRouter {
    public ExecutorRouteLast() {
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return new ReturnT(addressList.get(addressList.size() - 1));
    }
}
