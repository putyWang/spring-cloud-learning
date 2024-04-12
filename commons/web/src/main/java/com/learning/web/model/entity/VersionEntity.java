package com.learning.web.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

@Data
public class VersionEntity extends BaseEntity {

    @TableField(
            fill = FieldFill.INSERT_UPDATE,
            update = "%s+1"
    )
    @Version
    private Integer version;
}
