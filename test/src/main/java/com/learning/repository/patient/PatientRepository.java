package com.learning.repository.patient;

import com.learning.entity.po.InformationPatientPO;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/28 下午10:03
 */
@Repository
public interface PatientRepository extends ReactiveCrudRepository<InformationPatientPO, String> {
}
