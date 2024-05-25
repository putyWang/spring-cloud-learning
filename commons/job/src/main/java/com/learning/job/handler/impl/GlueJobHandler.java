package com.learning.job.handler.impl;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.handler.IJobHandler;
import com.learning.job.log.XxlJobLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GlueJobHandler extends IJobHandler {
    @Getter
    private long glueUpdateTime;
    private IJobHandler jobHandler;

    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("----------- glue.version:" + this.glueUpdateTime + " -----------", new Object[0]);
        return this.jobHandler.execute(param);
    }
}