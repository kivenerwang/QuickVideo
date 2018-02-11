package com.example.commonutil;

/**
 * Created by kiven on 2/11/18.
 */

public class KeyMsgEvent {
    private int keyCode;

    public KeyMsgEvent(int keyCode) {
        this.keyCode = keyCode;
    }
    public int getKeyCode() {
        return keyCode;
    }


    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}
