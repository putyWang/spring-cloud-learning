package com.learning.interrogation.domain.po.patient;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

/**
 * 就诊卡信息表
 * 
 * @author cc
 * @email yanhua@chinaforwards.com
 * @date 2020-05-25 16:03:19
 */
@Data
@Accessors(chain = true)
@TableName("TB_WLYL_JZKXX")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@Transactional
public class AdmissionCardInfoPO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 就诊卡编码
	 */
	@TableId(value="CBM", type= IdType.ASSIGN_ID)
	private String CBM;

	@TableField("CJZRBM")
	private String CJZRBM;

	/**
	 * 绑定状态0：未绑定1：已绑定
	 */
	@TableField("IBDZT")
	private Integer IBDZT;

	@TableField("DBDSJ")
	private Date DBDSJ;

	@TableField("DJBSJ")
	private Date DJBSJ;

	@TableField("CJGBM")
	private String CJGBM;

	@TableField("DXGSJ")
	private Date DXGSJ;

	@TableField("IKLX")
	private Integer IKLX;

	@TableField("IKZT")
	private Integer IKZT;

	@TableField("CJGMC")
	private String CJGMC;

	@TableField("DCJSJ")
	private Date DCJSJ;

	@TableField("CKH")
	private String CKH;

	@TableField("CJHRXM")
	private String CJHRXM;

	@TableField("CJHRNL")
	private String CJHRNL;

	@TableField("CJHRSFZH")
	private String CJHRSFZH;

	@TableField("CJHRLXFS")
	private String CJHRLXFS;

	@TableField("CEWM")
	private String CEWM;

	@TableField("ISFMR")
	private Integer ISFMR;

	@TableField("CBRID")
	private String patientId;

}
