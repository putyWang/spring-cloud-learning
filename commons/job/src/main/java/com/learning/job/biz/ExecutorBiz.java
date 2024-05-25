package com.learning.job.biz;

import com.learning.job.biz.model.LogResult;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.biz.model.TriggerParam;

public interface ExecutorBiz {
    ReturnT<String> beat();

    ReturnT<String> idleBeat(int jobId);

    ReturnT<String> kill(int jobId);

    ReturnT<LogResult> log(long logDateTim, long logId, int fromLineNum);

    ReturnT<String> run(TriggerParam triggerParam);
}
