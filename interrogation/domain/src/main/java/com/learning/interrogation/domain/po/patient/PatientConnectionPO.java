package com.learning.interrogation.domain.po.patient;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author:LanChen
 * @ClassName:PatientConnection
 * @Description:
 * @date:2021-11-5 14:47
 */
@Data
@Accessors(chain = true)
@TableName("TB_WLYL_YHJZRXXGL")
public class PatientConnectionPO {
    @TableId(value = "CBM", type = IdType.ASSIGN_ID)
    
    private String CBM;

    @TableField("CYHBM")
    
    private String CYHBM;

    @TableField("CYHZHID")
    
    private String CYHZHID;

    @TableField("CJZRBM")
    
    private String CJZRBM;

    @TableField("CGXBM")
    
    private String CGXBM;

    @TableField("ISFMR")
    
    private Integer ISFMR;

    @TableField("CJHRXM")
    
    private String CJHRXM;

    @TableField("CJHRSFZH")
    
    private String CJHRSFZH;

    @TableField("CJHRLXFS")
    
    private String CJHRLXFS;

    @TableField("DCJSJ")
    
    private Date DCJSJ;

    @TableField("DXGSJ")
    
    private Date DXGSJ;
}
