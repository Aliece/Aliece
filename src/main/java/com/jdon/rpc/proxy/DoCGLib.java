package com.jdon.rpc.proxy;

import com.jdon.rpc.Hello;

import java.lang.reflect.Constructor;

/**
 * Created by zhangsaizhong on 15/7/31.
 */
public class DoCGLib {
    public static void main(String[] args) {
        Constructor[] sss = Hello.class.getDeclaredConstructors();


        CglibProxy proxy = new CglibProxy();
        //通过生成子类的方式创建代理类
        Hello sproxy = (Hello) proxy.getProxy(Hello.class);
        sproxy.getValue();
    }
}
