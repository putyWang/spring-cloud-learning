package com.learning.Job.schedule.core.trigger;

import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.learning.Job.schedule.core.conf.XxlJobAdminConfig;
import com.learning.Job.schedule.core.model.XxlJobGroup;
import com.learning.Job.schedule.core.model.XxlJobInfo;
import com.learning.Job.schedule.core.model.XxlJobLog;
import com.learning.Job.schedule.core.route.ExecutorRouteStrategyEnum;
import com.learning.Job.schedule.core.utils.I18nUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

@Log4j2
public class XxlJobTrigger {
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");

    public XxlJobTrigger() {
    }

    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        if (jobInfo == null) {
            log.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
        } else {
            if (executorParam != null) {
                jobInfo.setExecutorParam(executorParam);
            }

            int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(jobInfo.getJobGroup());
            if (addressList != null && addressList.trim().length() > 0) {
                group.setAddressType(1);
                group.setAddressList(addressList.trim());
            }

            int[] shardingParam = null;
            if (executorShardingParam != null) {
                String[] shardingArr = executorShardingParam.split("/");
                if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                    shardingParam = new int[]{Integer.valueOf(shardingArr[0]), Integer.valueOf(shardingArr[1])};
                }
            }

            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), (ExecutorRouteStrategyEnum)null) && group.getRegistryList() != null && !group.getRegistryList().isEmpty() && shardingParam == null) {
                for(int i = 0; i < group.getRegistryList().size(); ++i) {
                    processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
                }
            } else {
                if (shardingParam == null) {
                    shardingParam = new int[]{0, 1};
                }

                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
            }

        }
    }

    private static boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    private static void processTrigger(XxlJobGroup group, XxlJobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {
        boolean isHttpHandler = "httpJobHandler".equalsIgnoreCase(jobInfo.getExecutorHandler());
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), (ExecutorRouteStrategyEnum)null);
        String shardingParam = ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum ? String.valueOf(index).concat("/").concat(String.valueOf(total)) : null;
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        log.debug(">>>>>>>>>>> yh-job trigger start, jobId:{}", jobLog.getId());
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);
        String address = null;
        ReturnT<String> routeAddressResult = null;
        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < group.getRegistryList().size()) {
                    address = (String)group.getRegistryList().get(index);
                } else {
                    address = (String)group.getRegistryList().get(0);
                }
            } else {
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == 200) {
                    address = (String)routeAddressResult.getContent();
                }
            }
        } else if (!isHttpHandler) {
            routeAddressResult = new ReturnT(500, I18nUtil.getString("jobconf_trigger_address_empty"));
        }

        ReturnT<String> triggerResult = null;
        if (address == null && !isHttpHandler) {
            triggerResult = new ReturnT(500, (String)null);
        } else {
            triggerResult = runExecutor(triggerParam, address, isHttpHandler);
        }

        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：").append(address);
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：").append(group.getAddressType() == 0 ? I18nUtil.getString("jobgroup_field_addressType_0") : I18nUtil.getString("jobgroup_field_addressType_1"));
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：").append(group.getRegistryList());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：").append(executorRouteStrategyEnum.getTitle());
        if (shardingParam != null) {
            triggerMsgSb.append("(" + shardingParam + ")");
        }

        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：").append(finalFailRetryCount);
        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_run") + "<<<<<<<<<<< </span><br>").append(routeAddressResult != null && routeAddressResult.getMsg() != null ? routeAddressResult.getMsg() + "<br><br>" : "").append(triggerResult.getMsg() != null ? triggerResult.getMsg() : "");
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(jobLog);
        log.debug(">>>>>>>>>>> yh-job trigger end, jobId:{}", jobLog.getId());
    }

    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address, boolean isHttpHandler) {
        if (isHttpHandler) {
            return runHttpExecutor(triggerParam, address);
        } else {
            String result = null;
            String postUrl = "http://" + address + "/v1/executor/api";

            ReturnT runResult;
            try {
                result = HttpUtil.sendHttpPost(postUrl, triggerParam);
                runResult = (ReturnT)JSON.toJavaObject(JSON.parseObject(result), ReturnT.class);
            } catch (Exception var9) {
                Exception e = var9;
                log.info(">>>>>>>>>>> yh-job trigger error, please check if the executor[{}] is running.", address, e);
                runResult = new ReturnT(500, e.toString() + "返回信息：" + result);
            }

            if (null == runResult) {
                log.info(">>>>>>>>>>> 返回结果为空！");
                runResult = new ReturnT(500, "返回信息为空，网络通信异常！");
            }

            StringBuffer runResultSB = null;

            try {
                runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
                runResultSB.append("<br>address：").append(address);
                runResultSB.append("<br>code：").append(runResult.getCode());
                runResultSB.append("<br>msg：").append(runResult.getMsg());
            } catch (Exception var8) {
                Exception e = var8;
                e.printStackTrace();
            }

            runResult.setMsg(runResultSB.toString());
            return runResult;
        }
    }

    public static ReturnT<String> runHttpExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult = new ReturnT();
        long logId = triggerParam.getLogId();
        String param = triggerParam.getExecutorParams();
        if (StringUtils.isEmpty(param)) {
            return new ReturnT(500, "param[" + param + "] invalid.");
        } else {
            String[] httpParams = param.split("\n");
            String url = null;
            String method = null;
            String data = null;
            String[] var10 = httpParams;
            int var11 = httpParams.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                String httpParam = var10[var12];
                if (httpParam.startsWith("url:")) {
                    url = httpParam.substring(httpParam.indexOf("url:") + 4).trim();
                }

                if (httpParam.startsWith("method:")) {
                    method = httpParam.substring(httpParam.indexOf("method:") + 7).trim().toUpperCase();
                }

                if (httpParam.startsWith("data:")) {
                    data = httpParam.substring(httpParam.indexOf("data:") + 5).trim();
                }
            }

            if (!StringUtils.isEmpty(url) && url.trim().length() != 0) {
                if (!StringUtils.isEmpty(method) && method.trim().length() != 0 && Arrays.asList("GET", "POST").contains(method)) {
                    boolean isPostMethod = method.equalsIgnoreCase("POST");
                    StringBuffer runResultSB = null;
                    HttpURLConnection connection = null;
                    BufferedReader bufferedReader = null;

                    try {
                        if ("get".equalsIgnoreCase(method) && !StringUtils.isEmpty(data)) {
                            try {
                                Matcher matcher = CHINESE_PATTERN.matcher(data);

                                for(String temp = ""; matcher.find(); data = data.replaceAll(temp, URLEncoder.encode(temp, "UTF-8"))) {
                                    temp = matcher.group();
                                }
                            } catch (UnsupportedOperationException var31) {
                                log.error("get 请求参数处理失败! url: {}, data: {}", new Object[]{url, data, var31});
                            }

                            url = url + "?" + data;
                        }

                        URL realUrl = new URL(url);
                        connection = (HttpURLConnection)realUrl.openConnection();
                        connection.setRequestMethod(method);
                        connection.setDoOutput(isPostMethod);
                        connection.setDoInput(true);
                        connection.setUseCaches(false);
                        connection.setReadTimeout(180000);
                        connection.setConnectTimeout(6000);
                        connection.setRequestProperty("connection", "Keep-Alive");
                        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
                        connection.setRequestProperty("yhjob-logid", String.valueOf(logId));
                        connection.connect();
                        if (isPostMethod && data != null && data.trim().length() > 0) {
                            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                            dataOutputStream.write(data.getBytes("UTF-8"));
                            dataOutputStream.flush();
                            dataOutputStream.close();
                        }

                        int statusCode = connection.getResponseCode();
                        if (statusCode != 200) {
                            throw new RuntimeException("Http Request StatusCode(" + statusCode + ") Invalid.");
                        } else {
                            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                            StringBuilder result = new StringBuilder();

                            String line;
                            while((line = bufferedReader.readLine()) != null) {
                                result.append(line);
                            }

                            String responseMsg = result.toString();
                            if (null == runResult) {
                                log.info(">>>>>>>>>>> 返回结果为空！");
                                runResult = new ReturnT(500, "返回信息为空，网络通信异常！");
                            } else {
                                log.info("请求:{}, 返回数据: {}", url, responseMsg);
                                runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
                                runResultSB.append("<br>address：").append(address);
                                runResultSB.append("<br>code：").append(statusCode);
                                runResultSB.append("<br>msg：").append(responseMsg);
                                runResult.setCode(statusCode);
                                runResult.setMsg(runResultSB.toString());
                            }
                        }
                    } catch (Exception var32) {
                        Exception e = var32;
                        log.error("请求url: {} 失败", url, e);
                        runResult = new ReturnT(500, "请求异常！");
                    } finally {
                        try {
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }

                            if (connection != null) {
                                connection.disconnect();
                            }
                        } catch (Exception var30) {
                            Exception e2 = var30;
                            log.error("关闭数据流失败!", e2);
                        }

                        return runResult;
                    }
                } else {
                    log.error("method[" + url + "] invalid.");
                    return new ReturnT(500, "method[" + method + "] invalid.");
                }
            } else {
                log.error("url[" + url + "] invalid.");
                return new ReturnT(500, "url[" + url + "] invalid.");
            }
        }
    }
}

