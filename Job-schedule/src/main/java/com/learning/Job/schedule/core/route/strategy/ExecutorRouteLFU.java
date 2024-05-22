package com.learning.Job.schedule.core.route.strategy;

import com.learning.Job.schedule.core.route.ExecutorRouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExecutorRouteLFU extends ExecutorRouter {
    private static ConcurrentMap<Integer, HashMap<String, Integer>> jobLfuMap = new ConcurrentHashMap();
    private static long CACHE_VALID_TIME = 0L;

    public ExecutorRouteLFU() {
    }

    public String route(int jobId, List<String> addressList) {
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLfuMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 86400000L;
        }

        HashMap<String, Integer> lfuItemMap = (HashMap)jobLfuMap.get(jobId);
        if (lfuItemMap == null) {
            lfuItemMap = new HashMap();
            jobLfuMap.putIfAbsent(jobId, lfuItemMap);
        }

        Iterator var4 = addressList.iterator();

        while(true) {
            String address;
            do {
                if (!var4.hasNext()) {
                    List<String> delKeys = new ArrayList();
                    Iterator var9 = lfuItemMap.keySet().iterator();

                    String delKey;
                    while(var9.hasNext()) {
                        delKey = (String)var9.next();
                        if (!addressList.contains(delKey)) {
                            delKeys.add(delKey);
                        }
                    }

                    if (delKeys.size() > 0) {
                        var9 = delKeys.iterator();

                        while(var9.hasNext()) {
                            delKey = (String)var9.next();
                            lfuItemMap.remove(delKey);
                        }
                    }

                    List<Map.Entry<String, Integer>> lfuItemList = new ArrayList(lfuItemMap.entrySet());
                    Collections.sort(lfuItemList, new Comparator<Map.Entry<String, Integer>>() {
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            return ((Integer)o1.getValue()).compareTo((Integer)o2.getValue());
                        }
                    });
                    Map.Entry<String, Integer> addressItem = (Map.Entry)lfuItemList.get(0);
                    String minAddress = (String)addressItem.getKey();
                    addressItem.setValue((Integer)addressItem.getValue() + 1);
                    return (String)addressItem.getKey();
                }

                address = (String)var4.next();
            } while(lfuItemMap.containsKey(address) && (Integer)lfuItemMap.get(address) <= 1000000);

            lfuItemMap.put(address, (new Random()).nextInt(addressList.size()));
        }
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = this.route(triggerParam.getJobId(), addressList);
        return new ReturnT(address);
    }
}
