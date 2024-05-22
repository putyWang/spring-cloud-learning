package com.learning.Job.schedule.core.route.strategy;

import com.learning.Job.schedule.core.route.ExecutorRouter;

import java.util.List;
import java.util.Random;

public class ExecutorRouteRandom extends ExecutorRouter {
    private static Random localRandom = new Random();

    public ExecutorRouteRandom() {
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = (String)addressList.get(localRandom.nextInt(addressList.size()));
        return new ReturnT(address);
    }
}