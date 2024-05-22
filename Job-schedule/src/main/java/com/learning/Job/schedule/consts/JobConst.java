package com.learning.Job.schedule.consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作调度
 */
public interface JobConst {
    String AUTHORIZATION = "Authorization";
    String USERNAME = "admin";
    String PASSWORD = "123456";
    List<Map<String, Object>> EXECUTOR_ROUTE_STRATEGY_ENUM = new ArrayList<Map<String, Object>>(10) {
        {
            ExecutorRouteStrategyEnum[] values = ExecutorRouteStrategyEnum.values();
            ExecutorRouteStrategyEnum[] var3 = values;
            int var4 = values.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                ExecutorRouteStrategyEnum value = var3[var5];
                Map<String, Object> map = new HashMap();
                map.put("value", value.getTitle());
                map.put("key", value);
                this.add(map);
            }

        }
    };
    List<Map<String, Object>> GLUE_TYPE_ENUM = new ArrayList<Map<String, Object>>(7) {
        {
            GlueTypeEnum[] values = GlueTypeEnum.values();
            GlueTypeEnum[] var3 = values;
            int var4 = values.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                GlueTypeEnum value = var3[var5];
                Map<String, Object> map = new HashMap();
                map.put("value", value.getDesc());
                map.put("key", value);
                this.add(map);
            }

        }
    };
    List<Map<String, Object>> EXECUTOR_BLOCK_STRATEGY_ENUM = new ArrayList<Map<String, Object>>(3) {
        {
            ExecutorBlockStrategyEnum[] values = ExecutorBlockStrategyEnum.values();
            ExecutorBlockStrategyEnum[] var3 = values;
            int var4 = values.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                ExecutorBlockStrategyEnum value = var3[var5];
                Map<String, Object> map = new HashMap();
                map.put("value", value.getTitle());
                map.put("key", value);
                this.add(map);
            }

        }
    };
}
