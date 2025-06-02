package com.learning.repository.patient;

import com.learning.entity.po.PatientConnectionPO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/28 下午10:10
 */
@Repository
public interface PatientConnectionRepository extends ReactiveCrudRepository<PatientConnectionPO, String> {

    @Query("SELECT CBM, ISFMR, CJZRBM,CGXBM,CJHRXM,CJHRSFZH,CJHRLXFS,DCJSJ FROM TB_WLYL_YHJZRXXGL WHERE CYHZHID = :accountId")
    Flux<PatientConnectionPO> listByAccountId(String accountId);
}
