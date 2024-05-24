package com.learning.job.enums;

import lombok.Getter;
import lombok.Setter;


public enum ExecutorBlockStrategyEnum {
    SERIAL_EXECUTION("Serial execution"),
    DISCARD_LATER("Discard Later"),
    DISCARD_CURRENT("Discard Current"),
    COVER_EARLY("Cover Early");

    @Setter
    @Getter
    private String title;

    ExecutorBlockStrategyEnum(String title) {
        this.title = title;
    }

    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        if (name != null) {
            for(ExecutorBlockStrategyEnum item : values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }

        return defaultItem;
    }
}
