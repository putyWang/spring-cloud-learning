package com.learning.job.handler;

import com.learning.job.biz.model.ReturnT;

import java.lang.reflect.InvocationTargetException;

public abstract class IJobHandler {
    public static final ReturnT<String> SUCCESS = new ReturnT(200, (String)null);
    public static final ReturnT<String> FAIL = new ReturnT(500, (String)null);
    public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT(502, (String)null);

    public abstract ReturnT<String> execute(String param) throws Exception;

    public void init() throws InvocationTargetException, IllegalAccessException {
    }

    public void destroy() throws InvocationTargetException, IllegalAccessException {
    }
}
