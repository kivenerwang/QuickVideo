package com.browser2345.player.message;

/**
 * Created by kiven on 2/2/18.
 */

public class BufferPointMessage  extends Message{

    public void setmBufferPoint(int mBufferPoint) {
        this.mBufferPoint = mBufferPoint;
    }

    private int mBufferPoint;

    public BufferPointMessage(int hash, String videoUrl, int percent) {
        super(hash, videoUrl);
        setmBufferPoint(percent);
    }
    public int getBufferPoint() {
        return mBufferPoint;
    }
}
