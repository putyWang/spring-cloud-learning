package com.learning.config.client;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * es 日志 po
 * 预估每月 100 万条数据
 * 每条数据 10 kb
 * 总共数据量 10 G 单主分片-写入压力大 2 副本
 * 日志允许一定量丢失，刷盘大小（20 M）与时间（取 5 s）可以适当扩大
 *
 *
 * @author WangWei
 * @version v 2.9.2
 * @date 2025-03-17
 **/
@Data
@Accessors(chain = true)
public class InterfaceAccessLogPo {

    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 接口编码
     */
    private String code;

    /**
     * 地址
     */
    private String address;

    /**
     * 是否成功
     */
    private int isSuccess;

    /**
     * 是否成功
     */
    private String orgCode;

    /**
     * 是否成功
     */
    private String orgName;

    /**
     * 厂商编码
     */
    private int manufacturer;

    /**
     * 响应时间
     */
    private long respTime;

    /**
     * 入参参数
     */
    private String param;

    /**
     * 返回数据
     */
    private String returnData;

    /**
     * 错误原因
     */
    private String reason;

    /**
     * 异常内容
     */
    private Date createTime;
}
