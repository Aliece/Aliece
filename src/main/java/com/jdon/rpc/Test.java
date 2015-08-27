package com.jdon.rpc;

import java.lang.reflect.Constructor;

/**
 * Created by zhangsaizhong on 15/7/31.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Father sample = new Son();
        System.out.println("调用的属性：" + sample.name);
//        Hello.class.newInstance();
        Constructor[] constructors = Hello.class.getConstructors();
        constructors[0].newInstance(constructors[0].getParameterTypes());
        System.out.println(constructors);


    }


}

class Father {
    protected String name = "父亲属性";
}


class Son extends Father {
    protected String name = "儿子属性";

}
