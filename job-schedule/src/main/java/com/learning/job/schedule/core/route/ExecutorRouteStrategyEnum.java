package com.learning.job.schedule.core.route;


import com.learning.Job.schedule.core.route.strategy.*;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.core.route.strategy.*;

public enum ExecutorRouteStrategyEnum {
    FIRST(I18nUtil.getString("jobconf_route_first"), new ExecutorRouteFirst()),
    LAST(I18nUtil.getString("jobconf_route_last"), new ExecutorRouteLast()),
    ROUND(I18nUtil.getString("jobconf_route_round"), new ExecutorRouteRound()),
    RANDOM(I18nUtil.getString("jobconf_route_random"), new ExecutorRouteRandom()),
    CONSISTENT_HASH(I18nUtil.getString("jobconf_route_consistenthash"), new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED(I18nUtil.getString("jobconf_route_lfu"), new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED(I18nUtil.getString("jobconf_route_lru"), new ExecutorRouteLRU()),
    FAILOVER(I18nUtil.getString("jobconf_route_failover"), new ExecutorRouteFailover()),
    BUSYOVER(I18nUtil.getString("jobconf_route_busyover"), new ExecutorRouteBusyover()),
    SHARDING_BROADCAST(I18nUtil.getString("jobconf_route_shard"), (ExecutorRouter)null);

    private String title;
    private ExecutorRouter router;

    private ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    public String getTitle() {
        return this.title;
    }

    public ExecutorRouter getRouter() {
        return this.router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem) {
        if (name != null) {
            ExecutorRouteStrategyEnum[] var2 = values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ExecutorRouteStrategyEnum item = var2[var4];
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }

        return defaultItem;
    }
}
