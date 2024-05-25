package com.learning.job.schedule.core.route.strategy;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.biz.model.TriggerParam;
import com.learning.job.schedule.core.route.ExecutorRouter;

import java.util.List;

public class ExecutorRouteLast extends ExecutorRouter {
    public ExecutorRouteLast() {
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return new ReturnT(addressList.get(addressList.size() - 1));
    }
}
