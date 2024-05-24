package com.learning.orm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: TableInfoDto
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Setter
@Getter
public class TableInfoDto {
    private String rowId;
    private String name;
    private Integer iType;
    private String dateKey;
    private String partitionKey;
    private Integer partitionNum;
    private String wardKey;
    private String primaryKey;
    private String dataBaseName;
}
