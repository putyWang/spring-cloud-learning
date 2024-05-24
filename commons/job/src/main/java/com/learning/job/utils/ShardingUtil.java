package com.learning.job.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ShardingUtil {
    private static InheritableThreadLocal<ShardingVO> contextHolder = new InheritableThreadLocal();

    public ShardingUtil() {
    }

    public static void setShardingVo(ShardingVO shardingVo) {
        contextHolder.set(shardingVo);
    }

    public static ShardingVO getShardingVo() {
        return contextHolder.get();
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class ShardingVO {
        private int index;
        private int total;
    }
}