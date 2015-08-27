package com.jdon.rpc;

/**
 * Created by zhangsaizhong on 15/7/29.
 */
public class RpcProvider {

    public static void main (String[] args) throws Exception {
        HelloService service = new HelloServiceImpl();
        RpcFramework.export(service, 1234);
    }
}
