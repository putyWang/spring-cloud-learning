package com.learning.orm.config;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.io.grpc.internal.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.learning.core.utils.CollectionUtils;
import com.learning.orm.config.properties.OrmProperties;
import com.learning.orm.dto.TableInfoDto;
import com.learning.orm.dto.TableParamDto;
import com.learning.orm.model.BaseModel;
import com.learning.orm.model.SelectModel;
import com.learning.orm.utils.PoUtil;
import com.learning.orm.utils.TableThreadLocalUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName: OrmUtilConfig
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Component
@Log4j2
public class OrmUtilConfig {
    public static ApplicationContext applicationContext = null;
    private static OrmProperties ormProperties;
    public static RestTemplate restTemplateOrm;

    @Autowired
    public OrmUtilConfig(ApplicationContext applicationContextValue, OrmProperties ormPropertiesValue, RestTemplate restTemplateOrmValue) {
        applicationContext = applicationContextValue;
        ormProperties = ormPropertiesValue;
        restTemplateOrm = restTemplateOrmValue;
    }

    public static synchronized void initTableInfoByTableCode(String tableCode) {
        TableInfoDto tableInfo = PoUtil.TABLE_CACHE_INFO.get(tableCode);
        if (Objects.isNull(tableInfo)) {
            if (ormProperties.getOrmSql()) {
                Boolean tenant = ormProperties.getTenant();
                log.debug(Thread.currentThread().getName() + "    tableCode: " + tableCode + " , tenant: " + tenant);
                TableParamDto tableParam = TableThreadLocalUtil.getTableParam();
                SelectModel yhSelectModel = BaseModel.initSelect(null, ormProperties.getOrmTableName(), null, tableParam);
                Map<String, Object> map = yhSelectModel.selectOne(
                        new QueryWrapper<>()
                        .eq("row_id", tableCode)
                );
                log.debug(Thread.currentThread().getName() + "    tenant: " + tenant);
                if (!CollectionUtils.isNotEmpty(map)) {
                    String tableName = (String)PoUtil.TABLE_NAME_CACHE.get(tableCode);
                    throw new ServiceException(String.format("没有查询到表信息,表id：%s, 表名：%s", tableCode, tableName));
                }

                TableInfoDto tableInfoDto = (TableInfoDto) JsonUtil.parseObject(JsonUtil.toJson(map), TableInfoDto.class);
                PoUtil.TABLE_CACHE_INFO.put(tableCode, tableInfoDto);
                TableThreadLocalUtil.setTableInfo(tableInfoDto);
            } else {
                String url = String.format("http://%s%s%s", ormProperties.getApplicationName(), ormProperties.getContextPath(), ormProperties.getOrmApi());

                PublicResult publicResult;
                try {
                    publicResult = (PublicResult)restTemplateOrm.getForObject(url, PublicResult.class, new Object[]{tableCode});
                } catch (Exception var8) {
                    log.error("调用数据标准接口异常", var8);
                    throw new ServiceException("调用数据标准接口异常");
                }

                Assert.notNull(publicResult, "调用数据标准响应失败");
                if (!publicResult.ok()) {
                    throw new ServiceException(String.format("调用数据标准接口获取数据异常; msg=%s data=%s", publicResult.getMsg(), publicResult.getData()));
                }

                Assert.notNull(publicResult.getData(), String.format("获取表信息失败，数据为空。表id：%s", tableCode));
                Map data = (Map)publicResult.getData();
                TableInfoDto tableInfoDto = (TableInfoDto)JsonUtil.parseObject(JsonUtil.toJson(data), TableInfoDto.class);
                PoUtil.TABLE_CACHE_INFO.put(tableCode, tableInfoDto);
                TableThreadLocalUtil.setTableInfo(tableInfoDto);
            }
        } else {
            TableThreadLocalUtil.setTableInfo(tableInfo);
        }

    }
}
