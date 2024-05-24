package com.learning.orm.dto;

import com.learning.orm.annotation.TableCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

/**
 * @ClassName: MultiParamDto
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Getter
@Setter
public class MultiParamDto {
    private String tableCode;
    private Class<?> cls;
    private TableParamDto tableParamDto;
    private String replaceCode;

    public static MultiParamDto initSingleTableInfoClass(Class<?> cls) {
        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
        TableCode tableCode = (TableCode)cls.getAnnotation(TableCode.class);
        return initSingleTableInfo(tableCode.value());
    }

    public static MultiParamDto initSingleTableInfo(String tableCode) {
        MultiParamDto multiParamDto = new MultiParamDto();
        multiParamDto.setTableCode(tableCode);
        return multiParamDto;
    }

    public static MultiParamDto initSingleTableInfoUseTableCodeClass(Class<?> cls) {
        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
        TableCode tableCode = (TableCode)cls.getAnnotation(TableCode.class);
        return initSingleTableInfoUseTableCode(tableCode.value());
    }

    public static MultiParamDto initSingleTableInfoUseTableCode(String tableCode) {
        MultiParamDto multiParamDto = new MultiParamDto();
        multiParamDto.setTableCode(tableCode);
        return multiParamDto;
    }

    public static MultiParamDto initSubTableClass(Class<?> cls, TableParamDto tableParamDto) {
        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
        TableCode tableCode = (TableCode)cls.getAnnotation(TableCode.class);
        return initSubTable(tableCode.value(), tableParamDto);
    }

    public static MultiParamDto initSubTable(String tableCode, TableParamDto tableParamDto) {
        MultiParamDto multiParamDto = new MultiParamDto();
        multiParamDto.setTableCode(tableCode);
        multiParamDto.setTableParamDto(tableParamDto);
        return multiParamDto;
    }

    public static MultiParamDto initSubTable(Class<?> cls, TableParamDto tableParamDto, String replaceCode) {
        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
        TableCode tableCode = (TableCode)cls.getAnnotation(TableCode.class);
        return initSubTable(tableCode.value(), tableParamDto, replaceCode);
    }

    public static MultiParamDto initSubTable(String tableCode, TableParamDto tableParamDto, String replaceCode) {
        MultiParamDto multiParamDto = new MultiParamDto();
        multiParamDto.setTableCode(tableCode);
        multiParamDto.setTableParamDto(tableParamDto);
        multiParamDto.setReplaceCode(replaceCode);
        return multiParamDto;
    }

    public static MultiParamDto initSubTableUseTableCodeClass(Class<?> cls, TableParamDto tableParamDto) {
        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
        TableCode tableCode = cls.getAnnotation(TableCode.class);
        return initSubTableUseTableCode(tableCode.value(), tableParamDto);
    }

    public static MultiParamDto initSubTableUseTableCode(String tableCode, TableParamDto tableParamDto) {
        MultiParamDto multiParamDto = new MultiParamDto();
        multiParamDto.setTableCode(tableCode);
        multiParamDto.setTableParamDto(tableParamDto);
        return multiParamDto;
    }
}
