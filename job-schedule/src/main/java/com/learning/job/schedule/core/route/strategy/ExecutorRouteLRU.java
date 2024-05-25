package com.learning.job.schedule.core.route.strategy;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.biz.model.TriggerParam;
import com.learning.job.schedule.core.route.ExecutorRouter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExecutorRouteLRU extends ExecutorRouter {
    private static ConcurrentMap<Integer, LinkedHashMap<String, String>> jobLRUMap = new ConcurrentHashMap();
    private static long CACHE_VALID_TIME = 0L;

    public ExecutorRouteLRU() {
    }

    public String route(int jobId, List<String> addressList) {
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLRUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 86400000L;
        }

        LinkedHashMap<String, String> lruItem = (LinkedHashMap)jobLRUMap.get(jobId);
        if (lruItem == null) {
            lruItem = new LinkedHashMap(16, 0.75F, true);
            jobLRUMap.putIfAbsent(jobId, lruItem);
        }

        Iterator var4 = addressList.iterator();

        String eldestKey;
        while(var4.hasNext()) {
            eldestKey = (String)var4.next();
            if (!lruItem.containsKey(eldestKey)) {
                lruItem.put(eldestKey, eldestKey);
            }
        }

        List<String> delKeys = new ArrayList();
        Iterator var8 = lruItem.keySet().iterator();

        String delKey;
        while(var8.hasNext()) {
            delKey = (String)var8.next();
            if (!addressList.contains(delKey)) {
                delKeys.add(delKey);
            }
        }

        if (delKeys.size() > 0) {
            var8 = delKeys.iterator();

            while(var8.hasNext()) {
                delKey = (String)var8.next();
                lruItem.remove(delKey);
            }
        }

        eldestKey = (String)((Map.Entry)lruItem.entrySet().iterator().next()).getKey();
        delKey = (String)lruItem.get(eldestKey);
        return delKey;
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = this.route(triggerParam.getJobId(), addressList);
        return new ReturnT(address);
    }
}
