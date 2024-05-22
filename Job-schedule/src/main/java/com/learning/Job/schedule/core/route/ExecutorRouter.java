package com.learning.Job.schedule.core.route;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    public ExecutorRouter() {
    }

    public abstract ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);
}
