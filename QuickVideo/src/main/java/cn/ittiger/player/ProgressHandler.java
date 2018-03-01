package cn.ittiger.player;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by kiven on 2/2/18.
 */

public class ProgressHandler  extends Handler{
    public static final int RESET_LOADING_SPEED = 1;
    public static final int UPDATE_LOADING_SPEED = 2;
    public static final int UPDATE_BOTTOM_PROGRESS = 3;
    public static final int UPDATE_CONTROLLER_VIEW = 4;

    public static final int AUDO_HIDE_WIDGET_TIME = 3000;// auto hide all widget
    public static final long TIME_UPDATE_LOADING_SPEED = 500;


    private WeakReference<IjkVideoView> reference;
    public ProgressHandler(IjkVideoView view) {
        reference = new WeakReference<IjkVideoView>(view);
    }


    @Override
    public void handleMessage(Message msg) {
        if (reference == null || reference.get() == null) {
            return;
        }

        switch (msg.what) {
            case RESET_LOADING_SPEED:
                reference.get().resetBufferProcess();
                break;
            case UPDATE_LOADING_SPEED:
                reference.get().updateLoadingProgress(msg);
                break;
            case UPDATE_BOTTOM_PROGRESS:
                reference.get().updateBottomProgress();
                break;
            case UPDATE_CONTROLLER_VIEW:
                reference.get().updateControllerView(msg);
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
