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
 * 就诊人信息表
 *
 * @author cc
 * @email yanhua@chinaforwards.com
 * @date 2020-05-27 11:38:47
 */
@Data
@Accessors(chain = true)
@TableName("TB_WLYL_JZRXX")
@JsonAutoDetect(
		fieldVisibility = JsonAutoDetect.Visibility.ANY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class InformationPatientPo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 就诊人编码
	 */
	@TableId(value="CJZRBM", type= IdType.ASSIGN_ID)
	private String CJZRBM;

	/**
	 * 就诊人头像
	 */
	@TableField("CJZRTX")
	private String CJZRTX;

	/**
	 * 就诊人姓名
	 */
	@TableField("CJZRXM")
	private String CJZRXM;

	/**
	 * 就诊人身份证号
	 */
	@TableField("CJZRSFZH")
	private String CJZRSFZH;

	/**
	 * 就诊人手机
	 */
	@TableField("CJZRSJ")
	private String CJZRSJ;

	/**
	 * 性别编码
	 */
	@TableField("CXBBM")
	private String CXBBM;

	/**
	 * 性别名称
	 */
	@TableField("CXBMC")
	private String CXBMC;

	/**
	 * 民族编码
	 */
	@TableField("CMZBM")
	private String CMZBM;

	/**
	 * 民族名称
	 */
	@TableField("CMZMC")
	private String CMZMC;

	/**
	 * 创建时间
	 */
	@TableField("DCJSJ")
	private Date DCJSJ;

	/**
	 * 修改时间
	 */
	@TableField("DXGSJ")
	private Date DXGSJ;

	/**
	 * 患者主索引
	 */
	@TableField("CHZZSY")
	private String CHZZSY;

	/**
	 * 职业编码
	 */
	@TableField("CZYBM")
	private String CZYBM;

	/**
	 * 职业名称
	 */
	@TableField("CZYMC")
	private String CZYMC;

	/**
	 * 省地址编码
	 */
	@TableField("CSDZBM")
	private String CSDZBM;

	/**
	 * 省地址名称
	 */
	@TableField("CSDZMC")
	private String CSDZMC;

	/**
	 * 市地址编码
	 */
	@TableField("CSQDZBM")
	private String CSQDZBM;

	/**
	 * 市地址名称
	 */
	@TableField("CSQDZMC")
	private String CSQDZMC;

	/**
	 * 县(区)地址编码
	 */
	@TableField("CQDZBM")
	private String CQDZBM;

	/**
	 * 县(区)地址名称
	 */
	@TableField("CQDZMC")
	private String CQDZMC;

	/**
	 * 乡地址编码
	 */
	@TableField("CXDZBM")
	private String CXDZBM;

	/**
	 * 乡地址名称
	 */
	@TableField("CXDZMC")
	private String CXDZMC;

	/**
	 * 村地址编码
	 */
	@TableField("CCDZBM")
	private String CCDZBM;

	/**
	 * 村地址名称
	 */
	@TableField("CCDZMC")
	private String CCDZMC;

	/**
	 * 门牌号
	 */
	@TableField("CMPH")
	private String CMPH;

	/**
	 * 出生年月
	 */
	@TableField("DCSNY")
	private Date DCSNY;

	/**
	 * 婚姻编码
	 */
	@TableField("CHYBM")
	private String CHYBM;

	/**
	 * 婚姻名称
	 */
	@TableField("CHYMC")
	private String CHYMC;

	/**
	 * 证件类型
	 */
	@TableField("CZJLX")
	private String CZJLX;

	/**
	 * 证件类型名称
	 */
	@TableField("CZJLXMC")
	private String CZJLXMC;

	/**
	 * 户籍详细地址
	 */
	@TableField("CHJXXDZ")
	private String CHJXXDZ;

	/**
	 * 监护人姓名
	 */
	@TableField("CJHRXM")
	private String CJHRXM;

	/**
	 * 监护人身份证号码
	 */
	@TableField("CJHRSFZH")
	private String CJHRSFZH;

	/**
	 * 监护人联系方式
	 */
	@TableField("CJHRLXFS")
	private String CJHRLXFS;

	/**
	 * 监护人年龄
	 */
	@TableField("CJHRNL")
	private String CJHRNL;

	/**
	 * 加密就诊人编码
	 */
	@TableField("CJMJZRBM")
	private String CJMJZRBM;

    /**
     * his 患者 id
     * 2024-08-20 六安妇幼需求
     */
    @TableField("CBRID")
    private String hisPatientId;
}
