package com.learning.job.biz;

import com.learning.job.biz.model.RegistryParam;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.biz.model.HandleCallbackParam;

import java.util.List;

public interface AdminBiz {
    String MAPPING = "/api";

    ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);

    ReturnT<String> registry(RegistryParam registryParam);

    ReturnT<String> registryRemove(RegistryParam registryParam);
}
