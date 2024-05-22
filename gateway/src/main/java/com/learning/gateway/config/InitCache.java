package com.learning.gateway.config;

import com.alibaba.fastjson.JSONObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.learning.gateway.filter.resolver.WhitelistResolver;
import com.learning.gateway.service.CacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Log4j2
public class InitCache implements InitializingBean {

    @Autowired
    private RouteService routeService;
    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private WhitelistService whitelistService;
    @Autowired
    private ServiceApiService serviceApiService;
    @Autowired
    private MyServiceService myServiceService;
    @Autowired
    private ServiceRateService serviceRateRepository;
    @Autowired
    private ServiceApiWhiteListsService serviceApiWhiteListsService;
    @Autowired
    private TripartiteAuthorizationService tripartiteAuthorizationService;
    @Autowired
    private AuthorizationServiceApiService authorizationServiceApiService;
    @Autowired
    private LoadBalanceService loadBalanceRepository;

    @Autowired
    @Qualifier("redis-cache-service")
    private CacheService cacheService;

    @Value("${yanhua.gateway.rate-limiter.default.rate}")
    private Integer defaultRate;

    @Value("${yanhua.gateway.rate-limiter.default.capacity}")
    private Integer defaultCapacity;

    public InitCache() {
    }

    public void initRateLimiterCache() {
        this.cacheService.evictAllCacheValues("service_rate_limiter");
        String defaultRateCapacity = String.format("%d%s%d", this.defaultRate, "@", this.defaultCapacity);
        this.cacheService.putToCache("service_rate_limiter", "default", defaultRateCapacity);
        List<MyService> allService = this.myServiceService.list();
        List<ServiceRate> allServiceRate = this.serviceRateRepository.list();
        Iterator var4 = allService.iterator();

        while(var4.hasNext()) {
            MyService service = (MyService)var4.next();
            Iterator var6 = allServiceRate.iterator();

            while(var6.hasNext()) {
                ServiceRate serviceRate = (ServiceRate)var6.next();
                if (service.getId().equals(serviceRate.getServiceId())) {
                    ServiceRateVo serviceRateVO = new ServiceRateVo();
                    BeanUtils.copyProperties(serviceRate, serviceRateVO);
                    serviceRateVO.setServiceName(service.getName());
                    this.cacheService.putToCache("service_rate_limiter", service.getName(), serviceRate.getReplenishRate() + "@" + serviceRate.getBurstCapacity());
                }
            }
        }

    }

    public void initApiWhiteListCache(JSONObject apiInfo) {
        this.cacheService.evictAllCacheValues("api_whites");
        LambdaQueryWrapper<ServiceApiWhiteLists> qw = new LambdaQueryWrapper();
        qw.ne(ServiceApiWhiteLists::isDeleted, false);
        List<ServiceApiWhiteListsVo> serviceApiWhiteListsVos = this.serviceApiWhiteListsService.listWhiteList();
        if (serviceApiWhiteListsVos != null && !serviceApiWhiteListsVos.isEmpty()) {
            try {
                WhitelistResolver.whiteListQeque.clear();
                WhitelistResolver.whiteListQeque.addAll((Collection)serviceApiWhiteListsVos.stream().filter((s) -> !StringUtils.isEmpty(s.getApiPath())).map((s) -> s.getApiPath()).collect(Collectors.toList()));
            } catch (Exception var5) {
                Exception e = var5;
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }

        if (apiInfo == null) {
            serviceApiWhiteListsVos.forEach((t) -> {
                this.cacheService.putToCache("api_whites", t.getApiPath(), t.getApiId());
            });
        } else {
            serviceApiWhiteListsVos.forEach((t) -> {
                String api = apiInfo.getString(t.getApiId());
                this.cacheService.putToCache("api_whites", api, t.getApiId());
                log.info(api + "|" + t.toString());
            });
        }

    }

    public void initTripartiteAuthorizationsCache() {
        this.cacheService.evictAllCacheValues("tripartite_auth_code");
        List<TripartiteAuthorization> tripartiteAuthList = this.tripartiteAuthorizationService.tripartiteAuthList();
        tripartiteAuthList.forEach((t) -> {
            List<AuthorizationServiceApi> authorizationServiceApis = this.authorizationServiceApiService.queryAuthInfoById(t.getId());
            Set<String> paths = new HashSet();
            authorizationServiceApis.forEach((k) -> {
                if (StringUtil.isNotBlank(k.getServiceId())) {
                    String predicates = this.routeService.getRoutePredicatesByServiceId(k.getServiceId());
                    String s = Utils.analysisRoutePredicates(predicates);
                    if (StringUtil.isNotBlank(s)) {
                        paths.add(s);
                    }
                } else {
                    ServiceApi serviceApi = this.serviceApiService.getServiceApiById(k.getServiceApiId());
                    if (null != serviceApi) {
                        paths.add(serviceApi.getPath());
                    }
                }

            });
            this.cacheService.putToCache("tripartite_auth_code", "TRIP_AU_SERVICE_API_" + t.getId(), paths);
            this.cacheService.putToCache("tripartite_auth_code", "TRIP_AU_CODE_SERVICE_API_" + t.getTripartiteAuthCode(), paths);
        });
    }

    public void initServiceApiTimeoutCache(JSONObject apiInfo) {
        this.cacheService.evictAllCacheValues("service_api_timeout");
        LambdaQueryWrapper<ServiceApi> qw = new LambdaQueryWrapper();
        qw.and((consumer) -> {
            ((LambdaQueryWrapper)((LambdaQueryWrapper)consumer.ne(ServiceApi::isDeleted, 1)).or()).isNull(ServiceApi::isDeleted);
        });
        qw.orderByAsc(ServiceApi::getPath);
        List<ServiceApi> serviceApiList = this.serviceApiService.list(qw);
        serviceApiList.forEach((api) -> {
            this.cacheService.putToCache("service_api_timeout", api.getPath(), api.getTimeout());
            apiInfo.put(api.getId(), api.getPath());
            log.info(api.toString());
        });
    }

    public void initWhitelistsCache() {
        this.cacheService.evictAllCacheValues("whites");
        List<Whitelist> whitelists = this.whitelistService.listWhitelist();
        whitelists.forEach((t) -> {
            this.cacheService.putToCache("whites", t.getUserId(), t.getUserId());
            log.info(t.toString());
        });
    }

    public void initBlacklistsCache() {
        this.cacheService.evictAllCacheValues("blacks");
        List<Blacklist> blacklists = this.blacklistService.listBlacklist();
        blacklists.forEach((t) -> {
            this.cacheService.putToCache("blacks", t.getIp(), t);
            log.info(t.toString());
        });
    }

    public void initServiceApisCache() {
        this.cacheService.evictAllCacheValues("service_apis");
        List<ServiceApi> serviceApis = this.serviceApiService.listServiceApi();
        serviceApis.forEach((serviceApi) -> {
            this.cacheService.putToCache("service_apis", serviceApi.getPath(), serviceApi.getStatus());
            log.info(serviceApi.toString());
        });
    }

    public void initLoadBalance() throws Exception {
        this.cacheService.evictAllCacheValues("service_load_balance");
        List<LoadBalance> allLoadBalanaceRule = this.loadBalanceRepository.list();
        long count = allLoadBalanaceRule.stream().filter((rule) -> {
            return rule.isEnabled();
        }).count();
        log.warn("rule count: " + count);
        if (count > 1L) {
            throw new Exception("发现多个启用的负载策略");
        } else if (count == 0L) {
            log.warn("未发现启用的负载策略,将使用默认策略");
        } else {
            Optional<LoadBalance> findFirst = allLoadBalanaceRule.stream().filter((rule) -> {
                return rule.isEnabled();
            }).findFirst();
            if (findFirst.isPresent()) {
                LoadBalance loadBalance = (LoadBalance)findFirst.get();
                this.cacheService.putToCache("service_load_balance", "ruleClassName", loadBalance.getRuleClassName());
            } else {
                log.warn("未启用的负载策略,将使用默认策略");
            }

        }
    }

    public void afterPropertiesSet() throws Exception {
        JSONObject apiInfo = new JSONObject();
        this.cacheService.evictAllCaches();
        this.initRateLimiterCache();
        this.initLoadBalance();
        this.initServiceApisCache();
        this.initBlacklistsCache();
        this.initWhitelistsCache();
        this.initServiceApiTimeoutCache(apiInfo);
        this.initTripartiteAuthorizationsCache();
        this.initApiWhiteListCache(apiInfo);
        apiInfo = null;
    }
}