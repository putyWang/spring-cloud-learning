package com.learning.interrogation.domain.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName: ApplyPatientIDCardVO
 * @Description:
 * @Author: phy
 * @Date: 2020/8/13 9:06
 * @Version V1.0.0
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class PatientIDCardAddDTO {

    private String CJZRBM;

    private Integer ISFMR;

    private String CJGBM;

    private Integer IKLX;

    private String CJGMC;

    private String CKH;

    private String CJHRXM;

    private String CJHRNL;

    private String CJHRSFZH;

    private String CJHRLXFS;

    private Integer IBDZT;

    private String CEWM;

    private String patientId;
}
