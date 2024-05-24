package com.learning.orm.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MultiParamListDto
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class MultiParamListDto {
    @Getter
    private List<MultiParamDto> listDtoList = new ArrayList();

    public static MultiParamListDto build() {
        return new MultiParamListDto();
    }

    public MultiParamListDto add(MultiParamDto multiParamDto) {
        this.listDtoList.add(multiParamDto);
        return this;
    }

    public MultiParamListDto addAll(List<MultiParamDto> multiParamDtoList) {
        this.listDtoList.addAll(multiParamDtoList);
        return this;
    }
}

