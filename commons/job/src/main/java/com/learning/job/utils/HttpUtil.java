package com.learning.job.utils;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    public static final String executorApi = "/v1/executor/api";
    public static final String adminList = "/v1/executor/list";
    public static final String callBackApi = "/api";

    public HttpUtil() {
    }

    public static String sendHttpPost(String url, Object regJson) throws Exception {
        String str = JSON.toJSONString(regJson);
        logger.info("url: " + url + " , params: " + str);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String type = "application/json";
        return result(url, type, new StringEntity(str, "UTF-8"), httpClient);
    }

    public static String postFromParam(String url, Map<String, String> map) throws Exception {
        logger.info("url: " + url + " , params: " + JSON.toJSONString(map));
        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManagerShared(true).build();
        List<NameValuePair> basicNameValuePairList = new ArrayList(2);
        Iterator var4 = map.keySet().iterator();

        String type;
        while (var4.hasNext()) {
            type = (String) var4.next();
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(type, (String) map.get(type));
            basicNameValuePairList.add(basicNameValuePair);
        }

        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(basicNameValuePairList);
        type = "application/x-www-form-urlencoded;charset=UTF-8";
        result(url, type, formEntity, httpClient);
        return result(url, type, formEntity, httpClient);
    }

    private static String result(String url, String type, HttpEntity httpEntity, CloseableHttpClient httpClient) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", type);
        httpPost.setEntity(httpEntity);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
        response.close();
        httpClient.close();
        return responseContent;
    }
}
