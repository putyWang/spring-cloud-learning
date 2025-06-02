package com.learning.interrogation.domain.po.patient;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 实名记录 结果
 * 
 * @author cc
 * @email yanhua@chinaforwards.com
 * @date 2020-08-14 10:15:34
 */
@Data
@Accessors(chain = true)
@TableName("TB_WLYL_JZRSMJLJG")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class RealRecordPO implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(value="CBM", type= IdType.ASSIGN_ID)
	
	private String CBM;

    @TableField("CJZRBM")
	private String CJZRBM;

    @TableField("IYWLY")
	private Integer IYWLY;

    @TableField("CSSYWBM")
	private String CSSYWBM;

    @TableField("ISMJG")
	private Integer ISMJG;

    @TableField("CSHYJ")
	private String CSHYJ;

    @TableField("CSMJLBM")
	private String CSMJLBM;

    @TableField("DCJSJ")
	private Date DCJSJ;

 	@TableField("DXGSJ")
	private Date DXGSJ;
}
