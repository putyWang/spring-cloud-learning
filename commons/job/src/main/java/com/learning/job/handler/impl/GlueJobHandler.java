package com.learning.job.handler.impl;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.handler.IJobHandler;
import com.learning.job.log.XxlJobLogger;

public class GlueJobHandler extends IJobHandler {
    private long glueUpdatetime;
    private IJobHandler jobHandler;

    public GlueJobHandler(IJobHandler jobHandler, long glueUpdatetime) {
        this.jobHandler = jobHandler;
        this.glueUpdatetime = glueUpdatetime;
    }

    public long getGlueUpdatetime() {
        return this.glueUpdatetime;
    }

    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("----------- glue.version:" + this.glueUpdatetime + " -----------", new Object[0]);
        return this.jobHandler.execute(param);
    }
}