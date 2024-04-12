package com.learning.web.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -7176390653391227433L;

    @TableId(value = "id")
    private Long id;
}
