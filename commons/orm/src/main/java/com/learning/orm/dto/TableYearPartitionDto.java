package com.learning.orm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName: TableYearPartitionDto
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
public class TableYearPartitionDto {
    private String dateKey;
    private String partitionKey;
    private Integer partitionNum;

    public TableYearPartitionDto(String dateKey, String partitionKey) {
        this.dateKey = dateKey;
        this.partitionKey = partitionKey;
    }
}
