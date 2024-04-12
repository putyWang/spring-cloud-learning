package com.learning.temp.strategy;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class TableShardingAlgorithm implements PreciseShardingAlgorithm {

    public TableShardingAlgorithm(){}

    @Override
    public String doSharding(Collection collection, PreciseShardingValue preciseShardingValue) {
        return String.format("ds%s", ((Long)preciseShardingValue.getValue() & 3) / 2);
    }
}
