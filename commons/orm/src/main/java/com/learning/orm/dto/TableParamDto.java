package com.learning.orm.dto;

import com.learning.orm.enums.TableTypeEnum;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @ClassName: TableParamDto
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Data
public class TableParamDto {
    private static final Logger log = LoggerFactory.getLogger(TableParamDto.class);
    private String tableField;
    private TableYearPartitionDto yearPartitionDto;
    private TableTypeEnum typeEnum;

    public static TableParamDto initDateParam(Date dateKey) {
        Assert.isTrue(!Objects.isNull(dateKey), "时间不能为空");
        TableParamDto tableParamDto = new TableParamDto();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tableParamDto.setTableField(formatter.format(dateKey));
        return tableParamDto;
    }

    public static TableParamDto initSingleTable() {
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setTypeEnum(TableTypeEnum.SINGLE_TABLE);
        return tableParamDto;
    }

    public static TableParamDto init(TableTypeEnum typeEnum) {
        log.error("未实现的功能,请勿调用");
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setTypeEnum(typeEnum);
        return tableParamDto;
    }

    public static TableParamDto initDateParam(Date dateKey, TableTypeEnum typeEnum) {
        TableParamDto tableParamDto = initDateParam(dateKey);
        tableParamDto.setTypeEnum(typeEnum);
        return tableParamDto;
    }

    public static TableParamDto initDateParam(String dateKey) {
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setTableField(dateKey);
        return tableParamDto;
    }

    public static TableParamDto initDateParam(String dateKey, TableTypeEnum typeEnum) {
        TableParamDto tableParamDto = initDateParam(dateKey);
        tableParamDto.setTypeEnum(typeEnum);
        return tableParamDto;
    }

    public static TableParamDto initWardParam(String wardKey) {
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setTableField(wardKey);
        tableParamDto.setTypeEnum(TableTypeEnum.WARD_TABLE);
        return tableParamDto;
    }

    public static TableParamDto initLibraryPartitionParam(String partitionKey) {
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setTableField(partitionKey);
        tableParamDto.setTypeEnum(TableTypeEnum.SEVEN);
        return tableParamDto;
    }

    public static TableParamDto initPartitionParam(String partitionKey) {
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setTableField(partitionKey);
        tableParamDto.setTypeEnum(TableTypeEnum.SINGLE_LIBRARY_PARTITION);
        return tableParamDto;
    }

    public static TableParamDto initYearPartitionParam(String dateKey, String partitionKey) {
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setYearPartitionDto(new TableYearPartitionDto(dateKey, partitionKey));
        tableParamDto.setTypeEnum(TableTypeEnum.YEAR_LIBRARY_PARTITION);
        return tableParamDto;
    }

    public static TableParamDto initYearPartitionParam(String dateKey, String partitionKey, Integer partitionNum) {
        TableParamDto tableParamDto = new TableParamDto();
        tableParamDto.setYearPartitionDto(new TableYearPartitionDto(dateKey, partitionKey, partitionNum));
        tableParamDto.setTypeEnum(TableTypeEnum.YEAR_LIBRARY_PARTITION);
        return tableParamDto;
    }
}
