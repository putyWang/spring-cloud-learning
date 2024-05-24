package com.learning.orm.model;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SelectModel
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class SelectModel extends BaseModel {
    public SelectModel(String dataBaseName, String tableName, List<String> fieldList) {
        this.dataBaseName = dataBaseName;
        this.tableName = tableName;
        this.fieldList = fieldList;
    }

    public IPage<Map<String, Object>> selectPage(IPage page, Wrapper query) {
        return MAPPER.selectPage(page, query, this);
    }

    public IPage<Map<String, Object>> selectPage(IPage page) {
        return selectPage(page, Wrappers.emptyWrapper());
    }

    public List<Map<String, Object>> selectList(Wrapper query) {
        return MAPPER.selectList(query, this);
    }

    public List<Map<String, Object>> selectList() {
        return selectList(Wrappers.emptyWrapper());
    }

    public Map<String, Object> selectOne(Wrapper query) {
        return MAPPER.selectOne(query, this);
    }

    public int selectCount(Wrapper query) {
        return MAPPER.selectCount(query, this);
    }

    public boolean isExist(Wrapper query) {
        return selectCount(query) > 0;
    }
}
