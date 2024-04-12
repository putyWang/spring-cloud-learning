package com.learning.web.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页排序类
 */
@Data
public class SortPageParam
        extends PageParam {

    /**
     * 排序字段
     */
    @Schema(name = "排序字段", title = "排序字段")
    private List<String> sorts;

    /**
     * 升序还是降序
     */
    @Schema(name = "升序还是降序", title = "升序还是降序")
    private List<String> ACSs;
}
