package com.learning.interrogation.server.Contrroller.patient;

import com.learning.interrogation.domain.vo.PatientAddVO;
import com.learning.interrogation.server.service.patient.InformationPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/2 下午9:10
 */
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final InformationPatientService informationPatientService;

    @PostMapping("/add")
    public Map<String,Object> save(@RequestBody PatientAddVO patient) {
        return informationPatientService.addPatient(patient);
    }

    @PostMapping("/lite/flow/add")
    public Map<String,Object> liteFlowAdd(@RequestBody PatientAddVO patient) {
        return informationPatientService.liteFlowAddPatient(patient);
    }
}
