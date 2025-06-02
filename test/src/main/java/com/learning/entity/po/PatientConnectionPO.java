package com.learning.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author:LanChen
 * @ClassName:PatientConnection
 * @Description:
 * @date:2021-11-5 14:47
 */
@Data
@Accessors(chain = true)
@Table("TB_WLYL_YHJZRXXGL")
public class PatientConnectionPO {

    @Id
    private String CBM;

    private String CYHBM;

    private String CYHZHID;

    private String CJZRBM;

    private String CGXBM;

    private Integer ISFMR;

    private String CJHRXM;

    private String CJHRSFZH;

    private String CJHRLXFS;

    private Date DCJSJ;

    private Date DXGSJ;
}
