package com.learning.job.schedule.core.route.strategy;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.biz.model.TriggerParam;
import com.learning.job.schedule.core.route.ExecutorRouter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExecutorRouteRound extends ExecutorRouter {
    private static ConcurrentMap<Integer, Integer> routeCountEachJob = new ConcurrentHashMap();
    private static long CACHE_VALID_TIME = 0L;

    public ExecutorRouteRound() {
    }

    private static int count(int jobId) {
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountEachJob.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 86400000L;
        }

        Integer count = (Integer)routeCountEachJob.get(jobId);
        count = count != null && count <= 1000000 ? Integer.valueOf(count + 1) : (new Random()).nextInt(100);
        routeCountEachJob.put(jobId, count);
        return count;
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = (String)addressList.get(count(triggerParam.getJobId()) % addressList.size());
        return new ReturnT(address);
    }
}
