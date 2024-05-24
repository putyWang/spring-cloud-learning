package com.learning.orm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.learning.core.utils.StringUtil;
import com.learning.orm.config.properties.OrmProperties;
import com.learning.orm.dto.TableInfoDto;
import com.learning.orm.model.*;
import com.learning.orm.utils.PoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @ClassName: OrmController
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@RequestMapping({"/yhOrm"})
@RestController
public class OrmController {
    @Autowired
    private OrmProperties ormProperties;

    @GetMapping({"delCacheByKey"})
    public PublicResult delCacheByKey(@RequestParam List<String> rowIds) {
        Iterator var2 = rowIds.iterator();

        while(var2.hasNext()) {
            String rowId = (String)var2.next();
            PoUtil.TABLE_CACHE_INFO.remove(rowId);
        }

        return PublicResult.success();
    }

    @GetMapping({"delCacheAll"})
    public PublicResult delCacheAll() {
        PoUtil.TABLE_CACHE_INFO.clear();
        return PublicResult.success();
    }

    @PostMapping({"getCachePage"})
    public PublicResult getCachePage(@RequestBody TableCacheQuery query) {
        String rowId = query.getRowId();
        String name = query.getName();
        String dataBaseName = query.getDataBaseName();
        List<TableInfoDto> list = new ArrayList();
        Set<Map.Entry<String, TableInfoDto>> entrySet = PoUtil.TABLE_CACHE_INFO.entrySet();
        Iterator var7 = entrySet.iterator();

        while(true) {
            Map.Entry entry;
            TableInfoDto value;
            do {
                String key;
                do {
                    do {
                        if (!var7.hasNext()) {
                            long current = query.getPage();
                            long size = query.getPageSize();
                            PageUtils<TableInfoDto> page = new PageUtils(list, (int)size);
                            Map<String, Object> resultMap = new HashMap((int)size);
                            resultMap.put("records", page.page((int)current));
                            resultMap.put("total", list.size());
                            return PublicResult.success(resultMap);
                        }

                        entry = (Map.Entry)var7.next();
                        value = (TableInfoDto)entry.getValue();
                        key = (String)entry.getKey();
                    } while(StringUtil.isNotBlank(rowId) && !key.toLowerCase().contains(rowId.toLowerCase()));
                } while(StringUtil.isNotBlank(name) && (StringUtil.isBlank(value.getName()) || !value.getName().toLowerCase().contains(name.toLowerCase())));
            } while(StringUtil.isNotBlank(dataBaseName) && (StringUtil.isBlank(value.getDataBaseName()) || !value.getDataBaseName().toLowerCase().contains(dataBaseName.toLowerCase())));

            list.add(entry.getValue());
        }
    }

    @PostMapping({"tablePage"})
    public PublicResult getTablePage(@RequestBody TableQuery query) {
        if (!this.ormProperties.getOrmSql()) {
            return PublicResult.failed("暂不支持");
        } else {
            SelectModel yhSelectModel = BaseModel.initSelect((String)null, this.ormProperties.getOrmTableName(), (List)null);
            QueryWrapper qw = new QueryWrapper();
            qw.like(StringUtil.isNotBlank(query.getRowId()), "row_id", query.getRowId());
            qw.like(StringUtil.isNotBlank(query.getName()), "name", query.getName());
            qw.like(StringUtil.isNotBlank(query.getDataBaseName()), "database_name", query.getDataBaseName());
            return PublicResult.success(yhSelectModel.yhPage(query.getPageObj(), qw));
        }
    }

    @PostMapping({"addTable"})
    public PublicResult addTable(@RequestBody Map<String, Object> map) {
        if (!this.ormProperties.getOrmSql()) {
            return PublicResult.failed("暂不支持");
        } else {
            Object rowId = map.get("row_id");
            if (Objects.isNull(rowId) || StringUtil.isBlank(rowId.toString())) {
                long id = IdWorker.getId();
                map.put("row_id", id);
            }

            map.put("id", map.get("row_id"));
            map.put("obj_id", "");
            if (this.isExistTable(map, false)) {
                return PublicResult.failed("相同类型的表名已经存在");
            } else {
                InsertModel yhInsertModel = BaseModel.initInsert((String)null, this.ormProperties.getOrmTableName(), map);
                yhInsertModel.insert();
                return PublicResult.success();
            }
        }
    }

    @PostMapping({"updateTable"})
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public PublicResult updateTable(@RequestBody Map<String, Object> map) {
        if (!this.ormProperties.getOrmSql()) {
            return PublicResult.failed("暂不支持");
        } else {
            map.remove("__row_number__");
            if (this.isExistTable(map, true)) {
                return PublicResult.failed("相同类型的表名已经存在");
            } else {
                Object rowId = map.get("row_id");
                if (!Objects.isNull(rowId) && !StringUtil.isBlank(rowId.toString())) {
                    UpdateModel yhUpdateModel = BaseModel.initUpdate(null, this.ormProperties.getOrmTableName(), map);
                    QueryWrapper qw = new QueryWrapper();
                    qw.eq("row_id", map.get("row_id"));
                    yhUpdateModel.yhUpdate(qw);
                    this.delCacheByKey(Collections.singletonList(rowId.toString()));
                    return PublicResult.success();
                } else {
                    return PublicResult.failed("主键信息异常");
                }
            }
        }
    }

    @GetMapping({"removeTable"})
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public PublicResult removeTable(@RequestParam List<String> rowIds) {
        if (!this.ormProperties.getOrmSql()) {
            return PublicResult.failed("暂不支持");
        } else {
            DeleteModel yhDeleteModel = BaseModel.initDelete((String)null, this.ormProperties.getOrmTableName());
            QueryWrapper qw = new QueryWrapper();
            qw.in("row_id", rowIds);
            yhDeleteModel.yhDelete(qw);
            this.delCacheByKey(rowIds);
            return PublicResult.success();
        }
    }

    public boolean isExistTable(Map<String, Object> map, boolean flag) {
        SelectModel yhSelectModel = BaseModel.initSelect(null, this.ormProperties.getOrmTableName(), null);
        QueryWrapper qw1 = new QueryWrapper();
        qw1.eq("name", map.get("name"));
        qw1.eq("iType", map.get("iType"));
        qw1.ne(flag, "row_id", map.get("row_id"));
        return yhSelectModel.yhExist(qw1);
    }
}

