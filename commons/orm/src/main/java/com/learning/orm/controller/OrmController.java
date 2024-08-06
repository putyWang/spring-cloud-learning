package com.learning.orm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.core.domain.model.ApiResult;
import com.learning.core.utils.StringUtil;
import com.learning.orm.config.properties.OrmProperties;
import com.learning.orm.dto.TableInfoDto;
import com.learning.orm.model.*;
import com.learning.orm.query.TableCacheParam;
import com.learning.orm.query.TableQuery;
import com.learning.orm.utils.PoUtil;
import lombok.RequiredArgsConstructor;
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
@RequestMapping({"/learning/orm"})
@RestController
@RequiredArgsConstructor
public class OrmController {

    private final OrmProperties ormProperties;

    @GetMapping({"/page/cache"})
    public ApiResult<Page<TableInfoDto>> getCachePage(TableCacheParam query) {
        String rowId = query.getRowId().toLowerCase();
        String name = query.getName().toLowerCase();
        String dataBaseName = query.getDataBaseName().toLowerCase();
        List<TableInfoDto> list = new ArrayList<>();
        // 1 抽取所有匹配条件的数据
        for(Map.Entry<String, TableInfoDto> entry : PoUtil.TABLE_CACHE_INFO.entrySet()) {
            TableInfoDto tableInfoDto = entry.getValue();
            String dataBaseNameValue = tableInfoDto.getDataBaseName().toLowerCase();
            String nameValue = tableInfoDto.getName().toLowerCase();
            if ((StringUtil.isNotBlank(rowId) && ! entry.getKey().toLowerCase().contains(rowId)) ||
                    (StringUtil.isNotBlank(name) && (StringUtil.isBlank(nameValue) || ! nameValue.contains(name))) ||
                    (StringUtil.isNotBlank(dataBaseName) && (StringUtil.isBlank(dataBaseNameValue) || ! dataBaseNameValue.contains(dataBaseName)))) {
                list.add(tableInfoDto);
            }
        }

        // 2.构建分页参数
        int current = query.getPage();
        int size = query.getPageSize();
        int offset = current*size;
        int totalCount = list.size();
        return ApiResult.ok(new Page<TableInfoDto>(current, size, totalCount)
                .setRecords(offset - 1 > totalCount ? null : list.subList(offset - 1, Math.min(offset + current - 1, totalCount))));
    }

    @PostMapping({"tablePage"})
    public PublicResult getTablePage(@RequestBody TableQuery query) {
        if (! this.ormProperties.getOrmSql()) {
            return PublicResult.failed("暂不支持");
        } else {
            SelectModel yhSelectModel = BaseModel.initSelect(null, this.ormProperties.getOrmTableName(), null);
            QueryWrapper qw = new QueryWrapper();
            qw.like(StringUtil.isNotBlank(query.getRowId()), "row_id", query.getRowId());
            qw.like(StringUtil.isNotBlank(query.getName()), "name", query.getName());
            qw.like(StringUtil.isNotBlank(query.getDataBaseName()), "database_name", query.getDataBaseName());
            return PublicResult.success(yhSelectModel.selectPage(query.getPageObj(), qw));
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

    @PostMapping({"/remove/cache/key"})
    public ApiResult<Boolean> delCacheByKey(@RequestParam List<String> rowIds) {
        rowIds.forEach(rowId -> PoUtil.TABLE_CACHE_INFO.remove(rowId));
        return ApiResult.ok();
    }

    @PostMapping({"/clear/cache"})
    public ApiResult<Boolean> delCacheAll() {
        PoUtil.TABLE_CACHE_INFO.clear();
        return ApiResult.ok();
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

