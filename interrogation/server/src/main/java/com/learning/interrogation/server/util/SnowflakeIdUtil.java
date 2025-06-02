package com.learning.interrogation.server.util;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Date;

/**
 * SnowflakeIdUtil
 *
 * @author lihaoru
 * @date 2/14/22
 */
@Slf4j
@Component
public class SnowflakeIdUtil {

    /**
     * 数据中心ID
     */
    private final long dataCenterId;

    /**
     * 机器ID
     */
    private final long workerId;



    public SnowflakeIdUtil() {
        this.dataCenterId = getDataCenterId();
        this.workerId = this.getWorkerId();
        log.info("SnowflakeIdUtil dataCenterId:{}, workerId:{}", this.dataCenterId, this.workerId);
    }

    private static long getDataCenterId() {
        long id = 0L;
        try {
            //获取本机(或者服务器ip地址)
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            //一般不是null会进入else
            if (network == null) {
                id = 1L;
            } else {
                //获取物理网卡地址
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 2]) | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
                    id = id % (32);
                }
            }
        } catch (Exception e) {
            log.warn("SnowflakeIdUtil getDataCenterId: " + e.getMessage());
        }
        return id;
    }

    private long getWorkerId() {
        long maxWorkerId = 31L;
        StringBuilder sb = new StringBuilder();
        sb.append(this.dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotBlank(name)) {
            /*
             * GET jvmPid
             */
            sb.append(name.split(StringPool.AT)[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * @param
     * @return {@link String}
     * @throws
     * @description 获取ID
     * @author lihaoru
     * @date 2/14/22
     */
    public String getSnowflakeId() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextIdStr();
    }


    /**
     * 根据雪花ID，反解析其生成时间
     * @param strId
     * @return
     */
    public static Date parseSonwFlakeIdForDate(String strId) {
        long timestampLeftShift = 22L;
        long id = Long.parseLong(strId);
        String sonwFlakeId = Long.toBinaryString(id);
        int len = sonwFlakeId.length();
        long timeStart = len < timestampLeftShift ? 0 : len - timestampLeftShift;
        String time = timeStart == 0 ? "0" : sonwFlakeId.substring(0, (int)timeStart);
        long diffTime = Long.parseLong(time, 2);
        long timeLong = diffTime + 1288834974657L;

        //转换日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeLong);
        return calendar.getTime();
    }

}
