package com.learning.interrogation.domain.po.org;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 机构信息扩展
 *
 * @author wr
 * @email yanhua@chinaforwards.com
 * @date 2020-11-05 18:29:59
 */
@Data
@Accessors(chain = true)
@TableName("TB_WLYL_JGXXKZ")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class InstitutionalExtensionPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "CBM", type = IdType.ASSIGN_ID)
    private String CBM;


    @TableField("CJGBM")
    private String CJGBM;

    @TableField("CJGMC")
    private String CJGMC;

    @TableField("CJGMCZY")
    private String CJGMCZY;

    @TableField("CYYDJZY")
    private String CYYDJZY;

    @TableField("CJKKJGID")
    private String CJKKJGID;

    @TableField("CYYDZZY")
    private String CYYDZZY;

    @TableField("IZT")
    private Integer IZT;

    @TableField("CJZKYW")
    private String CJZKYW;

    @TableField(value = "DCJSJ",fill = FieldFill.INSERT)
    private Date DCJSJ;

    @TableField(value = "DXGSJ",fill = FieldFill.UPDATE)
    private Date DXGSJ;

    @TableField("CYYLXFS")
    private String CYYLXFS;

    @TableField("CYYDJH")
    private String CYYDJH;


    @TableField("IJRFS")
    private Integer IJRFS;

    @TableField("ITZLX")
    private Integer ITZLX;


    @TableField("CHTML")
    private String CHTML;

    @TableField("CWXAPPID")
    private String CWXAPPID;

    @TableField("CWXPATH")
    private String CWXPATH;

    @TableField("CWXEXDATA")
    private String CWXEXDATA;

    @TableField("CZFBAPPID")
    private String CZFBAPPID;

    @TableField("CZFBPATH")
    private String CZFBPATH;

    @TableField("CZFBEXDATA")
    private String CZFBEXDATA;

    @TableField("CJSJKTAPPID")
    private String CJSJKTAPPID;

    @TableField("CJSJKTJGID")
    private String CJSJKTJGID;

    @TableField("IHJZT")
    private Integer IHJZT;

    @TableField("CYWJHZH")
    private String CYWJHZH;

    @TableField("CYWJHMY")
    private String CYWJHMY;


    @TableField("CHLWFZLQ")
    private String CHLWFZLQ;


    @TableField("CHLWSXRQ")
    private String CHLWSXRQ;


    @TableField("CHLWDQRQ")
    private String CHLWDQRQ;


    @TableField("CYLJGBM")
    private String CYLJGBM;

    @TableField("CYLJGLBBM")
    private String CYLJGLBBM;

    @TableField("CYLJGLBMC")
    private String CYLJGLBMC;

    @TableField("BJTBC")
    private Integer BJTBC;

    @TableField("BXZFW")
    private Integer BXZFW;

    @TableField("CYYJBBM")
    private String CYYJBBM;

    @TableField("CYYJBMC")
    private String CYYJBMC;

    @TableField("CYYDCBM")
    private String CYYDCBM;

    @TableField("CYYDCMC")
    private String CYYDCMC;

    @TableField("CSYZJGBM")
    private String CSYZJGBM;

    @TableField("CSYZJGMC")
    private String CSYZJGMC;

    @TableField("CYLXZBM")
    private String CYLXZBM;

    @TableField("CYLXZMC")
    private String CYLXZMC;

    @TableField("IZCZJ")
    private BigDecimal IZCZJ;

    @TableField("DCLRQ")
    private String DCLRQ;

    @TableField("CYYDJDZ")
    private String CYYDJDZ;

    @TableField("CYYGW")
    private String CYYGW;

    @TableField("CFRXM")
    private String CFRXM;

    @TableField("CFZRXM")
    private String CFZRXM;

    @TableField("CFWDH")
    private String CFWDH;

    @TableField("BHLWYY")
    private Integer BHLWYY;

    @TableField("CJRBM")
    private String CJRBM;

    @TableField("CSQSY")
    private String CSQSY;

    @TableField("CYYID")
    private String CYYID;

    @TableField("CXTMY")
    private String CXTMY;

    @TableField("CJGPTJGBM")
    private String CJGPTJGBM;

    @TableField("CYYDRMC")
    private String CYYDRMC;


    /**
     * 默认排序 序号
     * v2.7.0
     * */
    @TableField(value = "IPX")
    private Integer sortNum;


    @TableField("CJGZRWJ")
    private String CJGZRWJ;

    @TableField("CZLKMZRWJ")
    private String CZLKMZRWJ;

    @TableField("CHLWYYTB")
    private String CHLWYYTB;


    /**
     * v2.8.0
     * His厂商编码
     * */
    @TableField(value = "CHISCS")
    private String CHISCS;

    /**
     *  请求头参数
     * */
    @TableField(value = "CQQTCS")
    private String  CQQTCS;


    /**
     * 是否开通线下业务
     * 0-否
     * 1-是
     * */
    @TableField(value = "SFKTXX")
    private Integer isOfflineEnable;

    /**
     *  是否多院区 （0：否 1：是 默认0）
     * */
    @TableField(value = "IDYQ")
    private Integer IDYQ;

}
