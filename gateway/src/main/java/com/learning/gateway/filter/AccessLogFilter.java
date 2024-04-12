package com.learning.gateway.filter;

import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.IpUtil;
import com.learning.core.utils.StringUtils;
import com.learning.core.utils.date.DateMsUnit;
import com.learning.core.utils.date.DateUtils;
import com.learning.gateway.Constant.GatewayConstant;
import com.learning.gateway.config.LogProperties;
import com.learning.gateway.model.GatewayLog;
import com.learning.gateway.model.GatewayLogInfoFactory;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

/**
 * 日志记录过滤器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccessLogFilter implements GlobalFilter, Ordered {

    private final LogProperties logProperties;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    /**
     * default HttpMessageReader.
     */
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /*
     *  在CncloudRequestGlobalFilter后面执行 先清洗url在进行路径的日志的打印
     * */
    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 判断是否打开相应是日志配置 ingore配置校验
        if (!logProperties.getEnabled()||hasIgnoredFlag(exchange,logProperties)){
            return chain.filter(exchange);
        }
        // 获得请求上下文
        GatewayLog gatewayLog = parseGateway(exchange);
        ServerHttpRequest request = exchange.getRequest();
        MediaType mediaType = request.getHeaders().getContentType();
        if (Objects.isNull(mediaType)){
            return writeNormalLog(exchange,chain,gatewayLog);
        }
        gatewayLog.setRequestContentType(mediaType.getType() + "/" + mediaType.getSubtype());
        // 对不同的请求类型做相应的处理
        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)){
            return writeBodyLog(exchange,chain,gatewayLog);
        }else if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType) || MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)){
            return readFormData(exchange,chain,gatewayLog);
        }else {
            return writeBasicLog(exchange,chain,gatewayLog);
        }
    }

    /**
     * 校验白名单
     * @param exchange
     * @param logProperties
     * @return
     */
    private Boolean hasIgnoredFlag(ServerWebExchange exchange,LogProperties logProperties){
        List<String> ignoredPatterns = Arrays.asList(logProperties.getMatchUrl().split(",").clone());
        if (CollectionUtils.isEmpty(ignoredPatterns)){
            return Boolean.TRUE;
        }
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        for (String pattern : ignoredPatterns) {
            if (antPathMatcher.match(pattern,uri.getPath())){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 生成相应的报告并推送qq邮箱消息
     */
    private void report(GatewayLog gatewayLog){

        boolean reported = exceptionReport(gatewayLog);
        if (!reported){
            slowApiReport(gatewayLog);
        }
    }

    /**
     * 异常报警
     * @param gatewayLog
     * @return
     */
    private Boolean exceptionReport(GatewayLog gatewayLog){
        int code = gatewayLog.getCode();
        if (code== HttpStatus.OK.value()){
            return Boolean.FALSE;
        }
        LogProperties.ApiAlarmConfiguration apiAlarmConfiguration = logProperties.getFail();
        if (!apiAlarmConfiguration.isAlarm()){
            log.debug("api exception alarm disabled.");
            return Boolean.FALSE;
        }
        if (!CollectionUtils.isEmpty(apiAlarmConfiguration.getExclusion()) && apiAlarmConfiguration.getExclusion().contains(code)) {
            log.debug("status [{}] excluded.", code);
            return Boolean.FALSE;
        }
        String alarmContent = String.format("【API异常】 请求ip:[{%s}],请求路由:[{%s}],请求地址:[{%s}],返回状态码:[{%d}],执行时间:%d ms",gatewayLog.getIp(),gatewayLog.getTargetServer(),gatewayLog.getRequestPath(),code,gatewayLog.getExecuteTime());
//        notifier.notify(alarmContent);
        return Boolean.TRUE;
    }

    private Boolean slowApiReport(GatewayLog gatewayLog){
        LogProperties.SlowApiAlarmConfiguration slowApiAlarmConfiguration = logProperties.getSlow();
        long threshold = slowApiAlarmConfiguration.getThreshold();
        if (gatewayLog.getExecuteTime()<threshold){
            return Boolean.FALSE;
        }
        if (!slowApiAlarmConfiguration.isAlarm()) {
            log.debug("slow api alarm disabled.");
            return Boolean.FALSE;
        }
        String slowContent = String.format("【API执行时间过长,超过设定阈值】 请求ip:[{%s}],请求路由:[{%s}],请求地址:[{%s}],执行时间:%d ms",gatewayLog.getIp(),gatewayLog.getTargetServer(),gatewayLog.getRequestPath(),gatewayLog.getExecuteTime());
//        notifier.notify(slowContent);
        return Boolean.TRUE;
    }


    /**
     * 获得当前请求分发的路由
     * @param exchange
     * @return
     */
    private Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }

    private GatewayLog parseGateway(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().pathWithinApplication().value();
        Route route = getGatewayRoute(exchange);
        String ip = IpUtil.getIpAddress(request);
        String requestId = request.getId();
        GatewayLog gatewayLog = new GatewayLog();
        gatewayLog.setRequestId(requestId);
        gatewayLog.setSchema(request.getURI().getScheme());
        gatewayLog.setMethod(request.getMethodValue());
        gatewayLog.setRequestPath(requestPath);
        gatewayLog.setTargetServer(route.getId());
        gatewayLog.setIp(ip);
        gatewayLog.setRequestTime(new Date());
        return gatewayLog;
    }

    private Mono writeNormalLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog gatewayLog){
        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            ServerHttpResponse response = exchange.getResponse();
            int value = response.getStatusCode().value();
            gatewayLog.setCode(value);
            long executeTime = DateUtils.between(gatewayLog.getRequestTime(), new Date(), DateMsUnit.MS);
            gatewayLog.setExecuteTime(executeTime);
            ServerHttpRequest request = exchange.getRequest();
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            Map<String, String> paramsMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(queryParams)) {
                for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                    paramsMap.put(entry.getKey(), StringUtils.join(entry.getValue(), StringUtils.COMMA));
                }
            }
            gatewayLog.setQueryParams(paramsMap);
            GatewayLogInfoFactory.log(GatewayConstant.NORMAL_REQUEST,gatewayLog);
            // 推送相应的报告
            report(gatewayLog);
        }));
    }

    /**
     * 解决 request body 只能读取一次问题，
     * 参考: org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
     * @param exchange
     * @param chain
     * @param gatewayLog
     * @return
     */
    @SuppressWarnings("unchecked")
    private Mono writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog gatewayLog) {
        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            gatewayLog.setRequestBody(body);
            return Mono.just(body);
        });
        // 通过 BodyInserter 插入 body(支持修改body), 避免 request body 只能获取一次
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        // the new content type will be computed by bodyInserter
        // and then set in the request decorator
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {                    // 重新封装请求
            ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);                    // 记录响应日志
            ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, gatewayLog);                    // 记录普通的
            return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build()).then(Mono.fromRunnable(() -> {                                // 打印日志
                GatewayLogInfoFactory.log(GatewayConstant.APPLICATION_JSON_REQUEST,gatewayLog);
                // 推送相应的报告
                report(gatewayLog);
            }));
        }));
    }


    /**
     * 读取form-data数据
     * @param exchange
     * @param chain
     * @param accessLog
     * @return
     */
    private Mono<Void> readFormData(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog accessLog) {
        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            DataBufferUtils.retain(dataBuffer);
            final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
            final ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedFlux;
                }
                @Override
                public MultiValueMap<String, String> getQueryParams() {
                    return UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).build().getQueryParams();
                }
            };
            final HttpHeaders headers = exchange.getRequest().getHeaders();
            if (headers.getContentLength() == 0) {
                return chain.filter(exchange);
            }
            ResolvableType resolvableType;
            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(headers.getContentType())) {
                resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
            } else {
                //解析 application/x-www-form-urlencoded
                resolvableType = ResolvableType.forClass(String.class);
            }

            return MESSAGE_READERS.stream().filter(reader -> reader.canRead(resolvableType, mutatedRequest.getHeaders().getContentType())).findFirst().orElseThrow(() -> new IllegalStateException("no suitable HttpMessageReader.")).readMono(resolvableType, mutatedRequest, Collections.emptyMap()).flatMap(resolvedBody -> {
                if (resolvedBody instanceof MultiValueMap) {
                    LinkedMultiValueMap map = (LinkedMultiValueMap) resolvedBody;
                    if (CollectionUtils.isNotEmpty(map)) {
                        StringBuilder builder = new StringBuilder();
                        final Part bodyPartInfo = (Part) ((MultiValueMap) resolvedBody).getFirst("body");
                        if (bodyPartInfo instanceof FormFieldPart) {
                            String body = ((FormFieldPart) bodyPartInfo).value();
                            builder.append("body=").append(body);
                        }
                        accessLog.setRequestBody(builder.toString());
                    }
                } else {
                    accessLog.setRequestBody((String) resolvedBody);
                }

                //获取响应体
                ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, accessLog);
                return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build()).then(Mono.fromRunnable(() -> {                                    // 打印日志
                    // 打印响应的日志
                    GatewayLogInfoFactory.log(GatewayConstant.FORM_DATA_REQUEST,accessLog);
                    // 推送相应的报告
                    report(accessLog);
                }));
            });
        });
    }


    private Mono<Void> writeBasicLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog accessLog) {
        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            DataBufferUtils.retain(dataBuffer);
            final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
            final ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedFlux;
                }

                @Override
                public MultiValueMap<String, String> getQueryParams() {
                    return UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).build().getQueryParams();
                }
            };
            StringBuilder builder = new StringBuilder();
            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            if (CollectionUtils.isNotEmpty(queryParams)) {
                for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                    builder.append(entry.getKey()).append("=").append(entry.getValue()).append(StringUtils.COMMA);
                }
            }
            accessLog.setRequestBody(builder.toString());            //获取响应体
            ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, accessLog);
            return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build()).then(Mono.fromRunnable(() -> {                        // 打印日志
                GatewayLogInfoFactory.log(GatewayConstant.BASIC_REQUEST,accessLog);
                // 推送相应的报告
                report(accessLog);
            }));
        });
    }

    /**
     * 请求装饰器，重新计算 headers
     * @param exchange
     * @param headers
     * @param outputMessage
     * @return
     */
    private ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }
            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    /**
     * 记录响应日志
     * 通过 DataBufferFactory 解决响应体分段传输问题。
     */
    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange, GatewayLog gatewayLog) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    // 计算执行时间
                    long executeTime = DateUtils.between(gatewayLog.getRequestTime(), new Date(), DateMsUnit.MS);
                    gatewayLog.setExecuteTime(executeTime);
                    // 获取响应类型，如果是 json 就打印
                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);//
                    gatewayLog.setCode(this.getStatusCode().value());
                    //
                    if (Objects.equals(this.getStatusCode(), HttpStatus.OK)
                            && !StringUtil.isNullOrEmpty(originalResponseContentType)
                            && originalResponseContentType.contains("application/json")) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            // 释放掉内存
                            join.read(content);
                            DataBufferUtils.release(join);
                            return bufferFactory.wrap(content);
                        }));
                    }else {

                    }
                }
                return super.writeWith(body);
            }
        };
    }
}
