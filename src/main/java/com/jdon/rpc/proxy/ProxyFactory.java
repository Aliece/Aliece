package com.jdon.rpc.proxy;

import com.jdon.util.Debug;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Created by zhangsaizhong on 15/7/29.
 */
public class ProxyFactory {

    public ProxyFactory() {
        super();
    }

    public Object create(final Class modelClass, final MethodInterceptor methodInterceptor) {

        Object dynamicProxy = null;
        try {
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback(methodInterceptor);
            enhancer.setSuperclass(modelClass);
            dynamicProxy = enhancer.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dynamicProxy;
    }
}
