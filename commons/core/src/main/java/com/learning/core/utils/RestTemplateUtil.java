package com.learning.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.learning.core.domain.constants.CallBackConstant;
import com.learning.core.exception.LearningException;
import com.learning.core.domain.model.ApiResult;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.learning.core.domain.model.ApiResult.ApiResultBuilder;

/**
 * @author WangWei
 * @version v 1.0
 * @description RestTemplate 工具类
 * @date 2024-08-01
 **/
@AllArgsConstructor
public class RestTemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);

    private RestTemplate netRestTemplate;

    /**
     * get 请求对象
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<TResult> getResult(String url, TResource data, Class<TResult> responseType) {
        return getResult(url, data, responseType, null);
    }

    /**
     * get 请求对象
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param headerMap 请求头
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<TResult> getResult(String url, TResource data, Class<TResult> responseType, Map<String, String> headerMap) {
        return result(url, data, responseType, HttpMethod.GET, headerMap);
    }



    /**
     * post 请求对象
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<TResult> postResult(String url, TResource data, Class<TResult> responseType) {
        return postResult(url, data, responseType, null);
    }

    /**
     * post 请求对象
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param headerMap 请求头
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<TResult> postResult(String url, TResource data, Class<TResult> responseType, Map<String, String> headerMap) {
        return result(url, data, responseType, HttpMethod.POST, headerMap);
    }

    /**
     * 请求对象
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param method 请求方法
     * @param headerMap 请求头
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<TResult> result(String url, TResource data, Class<TResult> responseType, HttpMethod method, Map<String, String> headerMap) {
        return gainResult(gainResult(url, data, method, headerMap), responseType);
    }

    /**
     * get 请求列表
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<List<TResult>> getResultOfDataList(String url, TResource data, Class<TResult> responseType) {
        return getResultOfDataList(url, data, responseType, null);
    }

    /**
     * get 请求列表
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param headerMap 请求头
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<List<TResult>> getResultOfDataList(String url, TResource data, Class<TResult> responseType, Map<String, String> headerMap) {
        return resultOfDataList(url, data, responseType, HttpMethod.GET, headerMap);
    }

    /**
     * post 请求列表
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<List<TResult>> postResultOfDataList(String url, TResource data, Class<TResult> responseType) {
        return postResultOfDataList(url, data, responseType, null);
    }

    /**
     * post 请求列表
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param headerMap 请求头
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<List<TResult>> postResultOfDataList(String url, TResource data, Class<TResult> responseType, Map<String, String> headerMap) {
        return resultOfDataList(url, data, responseType, HttpMethod.POST, headerMap);
    }

    /**
     * post 请求列表
     * @param url 请求地址
     * @param data 请求数据
     * @param responseType 相应类型
     * @param method 请求方法
     * @param <TResult> 结果泛型
     * @param <TResource> 请求泛型
     * @return 请求结果
     */
    public <TResult, TResource> ApiResult<List<TResult>> resultOfDataList(String url, TResource data, Class<TResult> responseType, HttpMethod method, Map<String, String> headerMap) {
        return gainResultOfDataList(gainResult(url, data, method, headerMap), responseType);
    }

    /**
     * 发送请求
     * @param url 地址
     * @param data 请求对象
     * @param method 请求方法
     * @param headerMap 请求头
     * @param <TResource> 请求对象类型
     * @return 相应结果体
     */
    private <TResource> ApiResult<?> gainResult(String url, TResource data, HttpMethod method, Map<String, String> headerMap) {
        HttpHeaders headers = new HttpHeaders();

        if (! CollectionUtils.isEmpty(headerMap)) {
            for (Map.Entry<String, String> header : headerMap.entrySet()) {
                headers.set(header.getKey(), header.getValue());
            }
        }
        HttpEntity<TResource> requestEntity = new HttpEntity<>(data, headers);

        try {
            return netRestTemplate.exchange(url, method, requestEntity, ApiResult.class).getBody();
        } catch (Exception e) {
            log.error("{}请求失败", url, e);
            throw new LearningException("请求失败: " + e.getMessage());
        }
    }

    /**
     * 转换响应结果到指定对象
     * @param result 原始响应体
     * @param responseType 需求响应结果类型
     * @param <TResult> 响应结果类型
     * @return 转换的结果
     */
    private <TResult> ApiResult<TResult> gainResult(ApiResult<?> result, Class<TResult> responseType) {
        if (ObjectUtils.isNull(result)) {
            return ApiResult.fail();
        }

        ApiResultBuilder<TResult> resultBuilder = new ApiResultBuilder<TResult>()
                .code(result.getCode());

        if (! result.isSuccess()) {
            return resultBuilder.message(result.getMessage()).build();
        }

        return resultBuilder
                .data(JSONObject.parseObject(JSONObject.toJSONString(result.getData()), responseType))
                .build();
    }

    /**
     * 转换响应结果到指定对象列表
     * @param result 原始响应体
     * @param responseType 需求响应结果类型
     * @param <TResult> 响应结果类型
     * @return 转换的结果
     */
    private <TResult> ApiResult<List<TResult>> gainResultOfDataList(ApiResult<?> result, Class<TResult> responseType) {
        if (ObjectUtils.isNull(result)) {
            return ApiResult.fail();
        }

        ApiResultBuilder<List<TResult>> resultBuilder = new ApiResultBuilder<List<TResult>>()
                .code(result.getCode());

        if (! result.isSuccess()) {
            return resultBuilder.message(result.getMessage()).build();
        }

        return resultBuilder
                .data(JSONObject.parseArray(JSONObject.toJSONString(result.getData()), responseType))
                .build();
    }

    /**
     * post 请求数据
     * @param url 请求地址
     * @param data 请求对象
     * @param responseType 响应类型
     * @param <TResult> 响应结果类型
     * @param <TResource> 请求对象类型
     * @return 响应结果
     */
    public <TResult, TResource> TResult post(String url, TResource data, Class<TResult> responseType) {
        return post(url, data, responseType, "");
    }

    /**
     * post 请求数据
     * @param url 请求地址
     * @param data 请求对象
     * @param responseType 响应类型
     * @param token token 值
     * @param <TResult> 响应结果类型
     * @param <TResource> 请求对象类型
     * @return 响应结果
     */
    public <TResult, TResource> TResult post(String url, TResource data, Class<TResult> responseType, String token) {
        return post(url, data, responseType, addTokenToHeader(token));
    }

    /**
     * post 请求数据
     * @param url 请求地址
     * @param data 请求对象
     * @param responseType 响应类型
     * @param headers 请求头
     * @param <TResult> 响应结果类型
     * @param <TResource> 请求对象类型
     * @return 响应结果
     */
    @SneakyThrows
    public <TResult, TResource> TResult post(String url, TResource data, Class<TResult> responseType, Map<String, String> headers) {
        ApiResult<TResult> result = postResult(url, data, responseType, headers);

        if (! result.isSuccess()) {
            return responseType.newInstance();
        }

        return result.getData();
    }


    /**
     * get 请求数据
     * @param url 请求地址
     * @param data 请求对象
     * @param responseType 响应类型
     * @param <TResult> 响应结果类型
     * @param <TResource> 请求对象类型
     * @return 响应结果
     */
    public <TResult, TResource> TResult get(String url, TResource data, Class<TResult> responseType) {
        return get(url, data, responseType, "");
    }

    /**
     * get 请求数据
     * @param url 请求地址
     * @param data 请求对象
     * @param responseType 响应类型
     * @param token token 值
     * @param <TResult> 响应结果类型
     * @param <TResource> 请求对象类型
     * @return 响应结果
     */
    public <TResult, TResource> TResult get(String url, TResource data, Class<TResult> responseType, String token) {
        return get(url, data, responseType, addTokenToHeader(token));
    }

    /**
     * get 请求数据
     * @param url 请求地址
     * @param data 请求对象
     * @param responseType 响应类型
     * @param headers 请求头
     * @param <TResult> 响应结果类型
     * @param <TResource> 请求对象类型
     * @return 响应结果
     */
    @SneakyThrows
    public <TResult, TResource> TResult get(String url, TResource data, Class<TResult> responseType, Map<String, String> headers) {
        ApiResult<TResult> result = postResult(url, data, responseType, headers);

        if (! result.isSuccess()) {
            return responseType.newInstance();
        }

        return result.getData();
    }

    /**
     * 向请求头中添加 header
     * @param token token 值
     * @return 请求头 map
     */
    private Map<String, String> addTokenToHeader(String token) {
        Map<String, String> headerMap = new HashMap<>();
        if (! StringUtil.isEmpty(token)) {
            headerMap.put(CallBackConstant.TOKEN_KEY, token);
        }
        return headerMap;
    }
}
