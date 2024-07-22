package com.learning.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.core.model.ApiResult;
import com.learning.core.utils.CommonBeanUtil;
import com.learning.web.except.ExceptionBuilder;
import com.learning.web.model.dto.BaseDto;
import com.learning.web.model.entity.BaseEntity;
import com.learning.web.model.param.PageParam;
import com.learning.web.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseController<T extends BaseEntity, D extends BaseDto> {

    /**
     * 返回当前服务类
     *
     * @return
     */
    public abstract BaseService<D, T> getService();

    /**
     * 获取当前Dto
     *
     * @return
     */
    public abstract D getDto();

    /**
     * 获取当前实体类
     *
     * @return
     */
    public abstract T getEntity();

    /**
     * 分页查询所有数据
     *
     * @param page
     * @return
     */
    public ApiResult<IPage<D>> basePageList(PageParam page) {

        IPage<T> iPage = getService().page(page, null);
        List<T> records = iPage.getRecords();
        List<D> list = new ArrayList<>();

        if (!CollectionUtils.isEmpty(records)) {
            list = (List<D>) CommonBeanUtil.copyList(records, this.getDto().getClass());
        }

        list.forEach(dto -> dto = resultDtoHandler(dto));

        return ApiResult.ok((new Page<D>())
                .setPages(iPage.getPages())
                .setCurrent(iPage.getCurrent())
                .setRecords(list)
                .setTotal(iPage.getTotal())
                .setSize(iPage.getSize()));
    }

    /**
     * 展示所有数据
     *
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "展示所有数据", description = "展示所有数据")
    @Permission(value = "query", notes = "查询")
    public ApiResult<List<D>> list() {

        List<T> list = getService().list();
        List<D> ds = (List<D>) CommonBeanUtil.copyList(list, this.getDto().getClass());
        ds.forEach(dto -> dto = resultDtoHandler(dto));
        return ApiResult.ok(ds);
    }

    /**
     * 查看详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "查看详情", description = "查看详情")
    @Permission(value = "detail", notes = "查看详情")
    public ApiResult<D> selectById(@PathVariable long id) {
        T result = getService().getById(id);

        if (result == null) {
            return ApiResult.ok(null);
        } else {
            D dto = this.getDto();
            CommonBeanUtil.copyAndFormat(dto, result);
            dto = detailDtoHandler(dto);

            return ApiResult.ok(resultDtoHandler(dto));
        }

    }

    /**
     * "新增数据"
     *
     * @param dto
     * @return
     */
    @PostMapping("insert")
    @Operation(summary = "新增数据", description = "新增数据")
    @Permission(value = "insert", notes = "新增数据")
    public ApiResult<Boolean> insert(@RequestBody @Validated D dto) {

        if (dto == null) {

            return ApiResult.ok(null);
        }

        T entity = getEntity();
        CommonBeanUtil.copyAndFormat(entity, dto);
        return ApiResult.ok(getService().insert(entity));
    }

    /**
     * 批量新值
     *
     * @param list
     * @return
     */
    @PostMapping("/insert/batch")
    @Operation(summary = "批量新增数据", description = "批量新增数据")
    @Permission(value = "insert", notes = "新增数据")
    public ApiResult<Boolean> insertBatch(@RequestBody @Validated ArrayList<D> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw ExceptionBuilder.build("参数为空");
        }

        T entity = this.getEntity();
        List entities = CommonBeanUtil.copyList(list, entity.getClass());

        return ApiResult.ok(getService().insertBatch(entities));
    }

    /**
     * 更新数据
     *
     * @param dto
     * @return
     */
    @PostMapping("/update")
    @Operation(summary = "更新数据", description = "更新数据")
    @Permission(value = "update", notes = "更新数据")
    public ApiResult<Boolean> update(@RequestBody @Validated D dto) {
        T entity = getEntity();
        CommonBeanUtil.copyAndFormat(entity, dto);
        return ApiResult.ok(getService().update(entity));
    }

    /**
     * 根据id删除数据
     *
     * @param ids
     * @return
     */
    @PostMapping("/delete/{id}")
    @Operation(summary = "删除数据", description = "删除数据")
    @Permission(value = "delete", notes = "删除数据")
    public ApiResult deleteBatch(@RequestBody List<Serializable> ids) {
        getService().deleteBatch(ids);

        return ApiResult.ok();
    }

    /**
     * dto细节处理方法
     */
    public D detailDtoHandler(D dto) {
        return dto;
    }

    /**
     * dto结果处理方法
     *
     * @param dto
     * @return
     */
    public D resultDtoHandler(D dto) {
        return dto;
    }
}
