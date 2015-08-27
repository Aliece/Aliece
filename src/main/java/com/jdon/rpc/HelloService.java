package com.jdon.rpc;

import com.jdon.controller.events.Event;

import java.util.List;

/**
 * Created by zhangsaizhong on 15/7/29.
 */
public interface HelloService {

    String hello(String name);

    int test(int name);

    Hello todo();

    List<?> getList();

}
