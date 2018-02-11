package com.example.commonutil;

/**
 * Created by kiven on 2/11/18.
 */

public class NetMsgEvent {
    private int netType;

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public NetMsgEvent(int netType) {

        this.netType = netType;
    }
}
