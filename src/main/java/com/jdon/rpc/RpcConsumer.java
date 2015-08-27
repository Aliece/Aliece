package com.jdon.rpc;

import com.jdon.rpc.proxy.AsynProxy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangsaizhong on 15/7/29.
 */
public class RpcConsumer {

    public static void main(String[] args) throws Exception {
//        HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 1234);

//        ProxyFactory pf = new ProxyFactory();
//        HelloService service = (HelloService) pf.create(HelloServiceImpl.class, new AsynProxyMethodInterceptor());

        ExecutorService executor = Executors.newCachedThreadPool();
        HelloService service = new HelloServiceImpl();

        HelloService proxy = new AsynProxy<>(service,executor).getAsynProxy();
        long start = System.currentTimeMillis();
        Hello hello = proxy.todo();
        List<?> list = proxy.getList();
        String he = proxy.hello("World" + 2);
        System.out.println("耗时：" +(System.currentTimeMillis() - start) );
        System.out.println(he);
        System.out.println("耗时：" +(System.currentTimeMillis() - start) );
        int te = proxy.test(1);
        System.out.println("耗时：" +(System.currentTimeMillis() - start) );
        System.out.println(te);
        System.out.println("耗时：" +(System.currentTimeMillis() - start) );
        System.out.println(hello.getValue());
        System.out.println("耗时：" +(System.currentTimeMillis() - start) );
        System.out.println(list.size());
        System.out.println("耗时：" +(System.currentTimeMillis() - start) );
//        for (int i = 0; i < Integer.MAX_VALUE; i ++) {
//            String hello = proxy.hello("World" + i);
//            System.out.println(hello);
//            Thread.sleep(1000);
//        }
    }
}
