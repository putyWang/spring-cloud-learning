package com.learning.orm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: TableAnnotationDto
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Getter
@Setter
public class TableAnnotationDto {
    private String dateValue;
    private String wardValue;
    private String partitionValue;
}
