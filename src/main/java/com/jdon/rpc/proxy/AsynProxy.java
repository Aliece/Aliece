package com.jdon.rpc.proxy;

import com.jdon.rpc.proxy.AsynProxyMethodInterceptor;
import com.jdon.rpc.proxy.ProxyFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhangsaizhong on 15/7/29.
 */
public class AsynProxy<T> {

    private T object;
    private ExecutorService executor;
    private ProxyFactory proxyFactory;
    public AsynProxy(T object, ExecutorService executor) {
        this.object = object;
        this.executor = executor;
        this.proxyFactory = new ProxyFactory();
    }

    public AsynProxy(T object) {
        this.object = object;
        this.proxyFactory = new ProxyFactory();
    }

    public Object getProxy(Future<Object> future) {

        return (T) proxyFactory.create(object.getClass(), new ProxyMethodInterceptor(future));
    }

    public T getAsynProxy() {

        return (T) proxyFactory.create(object.getClass(), new AsynProxyMethodInterceptor(executor));
    }
}
