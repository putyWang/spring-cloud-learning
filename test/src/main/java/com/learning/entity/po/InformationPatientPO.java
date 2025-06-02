package com.learning.entity.po;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.learning.conditions.Wrappers;
import com.learning.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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
@Table("TB_WLYL_JZRXX")
@JsonAutoDetect(
		fieldVisibility = JsonAutoDetect.Visibility.ANY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class InformationPatientPO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 就诊人编码
	 */
	@Id
	private String CJZRBM;

	/**
	 * 就诊人头像
	 */
	private String CJZRTX;

	/**
	 * 就诊人姓名
	 */
	private String CJZRXM;

	/**
	 * 就诊人身份证号
	 */
	private String CJZRSFZH;

	/**
	 * 就诊人手机
	 */
	private String CJZRSJ;

	/**
	 * 性别编码
	 */
	private String CXBBM;

	/**
	 * 性别名称
	 */
	private String CXBMC;

	/**
	 * 民族编码
	 */
	private String CMZBM;

	/**
	 * 民族名称
	 */
	private String CMZMC;

	/**
	 * 创建时间
	 */
	private Date DCJSJ;

	/**
	 * 修改时间
	 */
	private Date DXGSJ;

	/**
	 * 患者主索引
	 */
	private String CHZZSY;

	/**
	 * 职业编码
	 */
	private String CZYBM;

	/**
	 * 职业名称
	 */
	private String CZYMC;

	/**
	 * 省地址编码
	 */
	private String CSDZBM;

	/**
	 * 省地址名称
	 */
	private String CSDZMC;

	/**
	 * 市地址编码
	 */
	private String CSQDZBM;

	/**
	 * 市地址名称
	 */
	private String CSQDZMC;

	/**
	 * 县(区)地址编码
	 */
	private String CQDZBM;

	/**
	 * 县(区)地址名称
	 */
	private String CQDZMC;

	/**
	 * 乡地址编码
	 */
	private String CXDZBM;

	/**
	 * 乡地址名称
	 */
	private String CXDZMC;

	/**
	 * 村地址编码
	 */
	private String CCDZBM;

	/**
	 * 村地址名称
	 */
	private String CCDZMC;

	/**
	 * 门牌号
	 */
	private String CMPH;

	/**
	 * 出生年月
	 */
	private Date DCSNY;

	/**
	 * 婚姻编码
	 */
	private String CHYBM;

	/**
	 * 婚姻名称
	 */
	private String CHYMC;

	/**
	 * 证件类型
	 */
	private String CZJLX;

	/**
	 * 证件类型名称
	 */
	private String CZJLXMC;

	/**
	 * 户籍详细地址
	 */
	private String CHJXXDZ;

	/**
	 * 监护人姓名
	 */
	private String CJHRXM;

	/**
	 * 监护人身份证号码
	 */
	private String CJHRSFZH;

	/**
	 * 监护人联系方式
	 */
	private String CJHRLXFS;

	/**
	 * 监护人年龄
	 */
	private String CJHRNL;

	/**
	 * 加密就诊人编码
	 */
	private String CJMJZRBM;

	public static void main(String[] args) {
		LambdaQueryWrapper<InformationPatientPO> eq = Wrappers.lambdaQuery(InformationPatientPO.class)
				.eq(InformationPatientPO::getCCDZBM, "1");
		System.out.println(eq.getCustomSqlSegment());
	}
}
