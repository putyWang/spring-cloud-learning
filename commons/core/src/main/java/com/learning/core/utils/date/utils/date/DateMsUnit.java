package com.learning.core.utils.date.utils.date;

public enum DateMsUnit {
    MS(1L),
    SECOND(MS.getMillis() * 1000L),
    MINUTE(SECOND.getMillis() * 60L),
    HOUR(MINUTE.getMillis() * 60L),
    DAY(HOUR.getMillis() * 24L),
    WEEK(DAY.getMillis() * 7L);

    private final long millis;

    private DateMsUnit(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return this.millis;
    }
}
