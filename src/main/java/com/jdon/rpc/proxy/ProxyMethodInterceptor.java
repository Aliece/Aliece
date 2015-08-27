package com.jdon.rpc.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

/**
 * Created by zhangsaizhong on 15/7/31.
 */
public class ProxyMethodInterceptor implements MethodInterceptor {
    Future<Object> future;

    public ProxyMethodInterceptor(Future<Object> future) {
        super();
        this.future = future;
    }

    public Object intercept(final Object object, final Method invokedmethod, final Object[] args, final MethodProxy methodProxy) throws Throwable {
        if (invokedmethod.getName().equals("finalize"))
            return null;
        Object result = null;
        try {
            Object obj = future.get();
            result = invokedmethod.invoke(obj, args);

        } catch (Exception ex) {
        } catch (Throwable ex) {
            throw new Throwable(ex);
        }
        return result;
    }

}