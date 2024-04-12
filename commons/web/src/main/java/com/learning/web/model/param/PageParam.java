package com.learning.web.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 */
@Data
public class PageParam {

    //当前页
    @Schema(name = "当前页", title = "当前页")
    private int pageNumber;

    //当前页面数据数
    @Schema(name = "当前页面数据数", title = "当前页面数据数")
    private int pageSize;

    //关键字
    @Schema(name = "关键字", title = "关键字")
    private String[] keywords;
}
