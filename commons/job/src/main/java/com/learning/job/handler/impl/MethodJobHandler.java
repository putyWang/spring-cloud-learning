package com.learning.job.handler.impl;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.handler.IJobHandler;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class MethodJobHandler extends IJobHandler {
    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public ReturnT<String> execute(String param) throws Exception {
        return (ReturnT)this.method.invoke(this.target, param);
    }

    public void init() throws InvocationTargetException, IllegalAccessException {
        if (this.initMethod != null) {
            this.initMethod.invoke(this.target);
        }

    }

    public void destroy() throws InvocationTargetException, IllegalAccessException {
        if (this.destroyMethod != null) {
            this.destroyMethod.invoke(this.target);
        }

    }

    public String toString() {
        return super.toString() + "[" + this.target.getClass() + "#" + this.method.getName() + "]";
    }
}
