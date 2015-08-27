package com.jdon.rpc;

import java.io.Serializable;

/**
 * Created by zhangsaizhong on 15/7/29.
 */
public class Hello {

    private int value;

//    public Hello() {}

    public Hello(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
