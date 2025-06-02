package com.learning.interrogation.domain.po.patient;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 账户就诊卡关联表
 *
 * @author cc
 * @email yanhua@chinaforwards.com
 * @date 2020-05-25 16:03:19
 */
@Data
@Accessors(chain = true)
@TableName("TB_WLYL_YHJZKGL")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class VisitCardAssociationPO {
    private static final long serialVersionUID = 1L;

    @TableId(value="CZHJZKGLID", type= IdType.ASSIGN_ID)
    private String CZHJZKGLID;

    /**
     * 用户帐号id
     */
    @TableField("CYHZHID")
    private String CYHZHID;

    @TableField("CJZKID")
    private String CJZKID;

    @TableField("ISFMR")
    private Integer ISFMR;

    @TableField("DCJSJ")
    private Date DCJSJ;

    @TableField("CYHBM")
    private String CYHBM;
}
