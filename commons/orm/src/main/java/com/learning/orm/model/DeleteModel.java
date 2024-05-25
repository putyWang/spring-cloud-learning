package com.learning.orm.model;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.AllArgsConstructor;

/**
 * @ClassName: DeleteModel
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class DeleteModel extends BaseModel {
    public DeleteModel(String dataBaseName, String tableName) {
        this.dataBaseName = dataBaseName;
        this.tableName = tableName;
    }

    public int delete(Wrapper query) {
        return MAPPER.delete(query, this);
    }
}

