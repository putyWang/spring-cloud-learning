package com.learning.Job.schedule.core.route.strategy;

import com.learning.Job.schedule.core.route.ExecutorRouter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ExecutorRouteConsistentHash extends ExecutorRouter {
    private static int VIRTUAL_NODE_NUM = 5;

    public ExecutorRouteConsistentHash() {
    }

    private static long hash(String key) {
        MessageDigest md5;
        NoSuchAlgorithmException keyBytes;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var9) {
            keyBytes = var9;
            throw new RuntimeException("MD5 not supported", keyBytes);
        }

        md5.reset();
        keyBytes = null;

        byte[] keyBytes;
        try {
            keyBytes = key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException var8) {
            UnsupportedEncodingException e = var8;
            throw new RuntimeException("Unknown string :" + key, e);
        }

        md5.update(keyBytes);
        byte[] digest = md5.digest();
        long hashCode = (long)(digest[3] & 255) << 24 | (long)(digest[2] & 255) << 16 | (long)(digest[1] & 255) << 8 | (long)(digest[0] & 255);
        long truncateHashCode = hashCode & 4294967295L;
        return truncateHashCode;
    }

    public String hashJob(int jobId, List<String> addressList) {
        TreeMap<Long, String> addressRing = new TreeMap();
        Iterator var4 = addressList.iterator();

        while(var4.hasNext()) {
            String address = (String)var4.next();

            for(int i = 0; i < VIRTUAL_NODE_NUM; ++i) {
                long addressHash = hash("SHARD-" + address + "-NODE-" + i);
                addressRing.put(addressHash, address);
            }
        }

        long jobHash = hash(String.valueOf(jobId));
        SortedMap<Long, String> lastRing = addressRing.tailMap(jobHash);
        if (!lastRing.isEmpty()) {
            return (String)lastRing.get(lastRing.firstKey());
        } else {
            return (String)addressRing.firstEntry().getValue();
        }
    }

    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = this.hashJob(triggerParam.getJobId(), addressList);
        return new ReturnT(address);
    }
}

