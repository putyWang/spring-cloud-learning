package com.learning.Job.schedule.core.utils;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalCacheUtil {
    private static ConcurrentMap<String, LocalCacheData> cacheRepository = new ConcurrentHashMap();

    public LocalCacheUtil() {
    }

    public static boolean set(String key, Object val, long cacheTime) {
        cleanTimeutCache();
        if (key != null && key.trim().length() != 0) {
            if (val == null) {
                remove(key);
            }

            if (cacheTime <= 0L) {
                remove(key);
            }

            long timeoutTime = System.currentTimeMillis() + cacheTime;
            LocalCacheData localCacheData = new LocalCacheData(key, val, timeoutTime);
            cacheRepository.put(localCacheData.getKey(), localCacheData);
            return true;
        } else {
            return false;
        }
    }

    public static boolean remove(String key) {
        if (key != null && key.trim().length() != 0) {
            cacheRepository.remove(key);
            return true;
        } else {
            return false;
        }
    }

    public static Object get(String key) {
        if (key != null && key.trim().length() != 0) {
            LocalCacheData localCacheData = (LocalCacheData)cacheRepository.get(key);
            if (localCacheData != null && System.currentTimeMillis() < localCacheData.getTimeoutTime()) {
                return localCacheData.getVal();
            } else {
                remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean cleanTimeutCache() {
        if (!cacheRepository.keySet().isEmpty()) {
            Iterator var0 = cacheRepository.keySet().iterator();

            while(var0.hasNext()) {
                String key = (String)var0.next();
                LocalCacheData localCacheData = (LocalCacheData)cacheRepository.get(key);
                if (localCacheData != null && System.currentTimeMillis() >= localCacheData.getTimeoutTime()) {
                    cacheRepository.remove(key);
                }
            }
        }

        return true;
    }

    private static class LocalCacheData {
        private String key;
        private Object val;
        private long timeoutTime;

        public LocalCacheData() {
        }

        public LocalCacheData(String key, Object val, long timeoutTime) {
            this.key = key;
            this.val = val;
            this.timeoutTime = timeoutTime;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getVal() {
            return this.val;
        }

        public void setVal(Object val) {
            this.val = val;
        }

        public long getTimeoutTime() {
            return this.timeoutTime;
        }

        public void setTimeoutTime(long timeoutTime) {
            this.timeoutTime = timeoutTime;
        }
    }
}

