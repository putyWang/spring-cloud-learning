package com.learning.job.schedule.core.route;

import java.util.List;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.biz.model.TriggerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    public ExecutorRouter() {
    }

    public abstract ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);
}
