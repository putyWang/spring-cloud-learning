package com.learning.orm.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @ClassName: TableInfoDto
 * @Description: 动态表信息
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Setter
@Getter
@Accessors(chain = true)
public class TableInfoDto {
    private String rowId;

    /**
     * 表名
     */
    private String name;
    private Integer iType;
    private String dateKey;
    private String partitionKey;
    private Integer partitionNum;
    private String wardKey;
    private String primaryKey;

    /**
     * 数据库名
     */
    private String dataBaseName;
}
