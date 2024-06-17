package com.learning.orm.dto;

import com.learning.core.utils.date.DateUtils;
import com.learning.orm.enums.TableTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
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
@Accessors(chain = true)
@Log4j2
public class TableParamDto {
    /**
     * 分表值
     */
    private String tableField;
    private TableYearPartitionDto yearPartitionDto;

    /**
     * 分表类型
     */
    private TableTypeEnum typeEnum;

    /**
     *
     *
     * @param dateKey
     * @return
     */
    public static TableParamDto initDateParam(Date dateKey) {
        Assert.isTrue(!Objects.isNull(dateKey), "时间不能为空");
        return new TableParamDto().setTableField(DateUtils.formatDate(dateKey));
    }

    /**
     *
     *
     * @return
     */
    public static TableParamDto initSingleTable() {
        return new TableParamDto().setTypeEnum(TableTypeEnum.SINGLE_TABLE);
    }

    /**
     *
     *
     * @param typeEnum
     * @return
     */
    public static TableParamDto init(TableTypeEnum typeEnum) {
        log.error("未实现的功能,请勿调用");
        return new TableParamDto().setTypeEnum(typeEnum);
    }

    /**
     *
     *
     * @param dateKey
     * @param typeEnum
     * @return
     */
    public static TableParamDto initDateParam(Date dateKey, TableTypeEnum typeEnum) {
        return initDateParam(dateKey).setTypeEnum(typeEnum);
    }

    public static TableParamDto initDateParam(String dateKey) {
        return new TableParamDto().setTableField(dateKey);
    }

    public static TableParamDto initDateParam(String dateKey, TableTypeEnum typeEnum) {
        return initDateParam(dateKey).setTypeEnum(typeEnum);
    }

    public static TableParamDto initWardParam(String wardKey) {
        return new TableParamDto().setTableField(wardKey)
                .setTypeEnum(TableTypeEnum.WARD_TABLE);
    }

    public static TableParamDto initLibraryPartitionParam(String partitionKey) {
        return new TableParamDto().setTableField(partitionKey)
                .setTypeEnum(TableTypeEnum.SEVEN);
    }

    public static TableParamDto initPartitionParam(String partitionKey) {
        return new TableParamDto().setTableField(partitionKey)
                .setTypeEnum(TableTypeEnum.SINGLE_LIBRARY_PARTITION);
    }

    public static TableParamDto initYearPartitionParam(String dateKey, String partitionKey) {
        return new TableParamDto().setYearPartitionDto(new TableYearPartitionDto(dateKey, partitionKey))
                .setTypeEnum(TableTypeEnum.YEAR_LIBRARY_PARTITION);
    }

    public static TableParamDto initYearPartitionParam(String dateKey, String partitionKey, Integer partitionNum) {
        return new TableParamDto()
                .setYearPartitionDto(new TableYearPartitionDto(dateKey, partitionKey, partitionNum))
                .setTypeEnum(TableTypeEnum.YEAR_LIBRARY_PARTITION);
    }
}
