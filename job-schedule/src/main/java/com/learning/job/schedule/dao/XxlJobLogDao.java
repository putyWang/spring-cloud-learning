package com.learning.job.schedule.dao;

import com.learning.job.schedule.core.model.XxlJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface XxlJobLogDao {
    List<XxlJobLog> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("jobGroup") int jobGroup, @Param("jobId") int jobId, @Param("triggerTimeStart") Date triggerTimeStart, @Param("triggerTimeEnd") Date triggerTimeEnd, @Param("logStatus") int logStatus);

    int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("jobGroup") int jobGroup, @Param("jobId") int jobId, @Param("triggerTimeStart") Date triggerTimeStart, @Param("triggerTimeEnd") Date triggerTimeEnd, @Param("logStatus") int logStatus);

    XxlJobLog load(@Param("id") long id);

    long save(XxlJobLog xxlJobLog);

    int updateTriggerInfo(XxlJobLog xxlJobLog);

    int updateHandleInfo(XxlJobLog xxlJobLog);

    int delete(@Param("jobId") int jobId);

    int triggerCountByHandleCode(@Param("handleCode") int handleCode);

    List<Map<String, Object>> triggerCountByDay(@Param("from") Date from, @Param("to") Date to);

    List<Map<String, Object>> groupCount(@Param("from") Date from, @Param("to") Date to);

    List<Long> findClearLogIds(@Param("jobGroup") int jobGroup, @Param("jobId") int jobId, @Param("clearBeforeTime") Date clearBeforeTime, @Param("clearBeforeNum") int clearBeforeNum, @Param("pagesize") int pagesize);

    int clearLog(@Param("logIds") List<Long> logIds);

    List<Long> findFailJobLogIds(@Param("pagesize") int pagesize);

    int updateAlarmStatus(@Param("logId") long logId, @Param("oldAlarmStatus") int oldAlarmStatus, @Param("newAlarmStatus") int newAlarmStatus);

    boolean deleteLogs(@Param("expireTime") Date expireTime);
}
