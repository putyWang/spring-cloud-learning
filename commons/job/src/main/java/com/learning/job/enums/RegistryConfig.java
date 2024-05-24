package com.learning.job.enums;

public class RegistryConfig {
    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = 90;

    public RegistryConfig() {
    }

    public enum RegistryType {
        EXECUTOR,
        ADMIN;

        RegistryType() {
        }
    }
}
