package cn.ittiger.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;

import cn.ittiger.player.state.PlayState;
import cn.ittiger.player.view.IjkVideoContract;

/**
 * Created by kiven on 2/2/18.
 */

public class VideoPresenter implements IjkVideoContract.IVideoPresenter{

    private IjkVideoContract.IVideoView mVideoView;

    public VideoPresenter(IjkVideoContract.IVideoView videoView) {
        this.mVideoView = videoView;
    }


    @Override
    public void handleVideoContainerLogic(int playState, boolean needHiden) {
        switch (playState) {
            case PlayState.STATE_NORMAL:
                mVideoView.startPlayVideo();
                break;
            case PlayState.STATE_AUTO_COMPLETE:
                //播放完成
            case PlayState.STATE_NET_ERROR:
                //网络错误
                break;
            case PlayState.STATE_PLAYING:
                handleViewState(needHiden);
                break;
        }
    }

    @Override
    public void handleScreenRotate(Activity activity) {
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //当前小屏，需要且成全屏
            mVideoView.changeUIFullScreen();
        } else {
            //当前屏幕是全屏，需要切出正常屏幕
            mVideoView.changeUINormalScreen();
        }

    }

    /**
     * 处理播放
     * @param needHiden
     */
    private void handleViewState(boolean needHiden) {
        if (needHiden) {
            mVideoView.hidenAllView();
        } else {
            mVideoView.showAllView();
        }
    }
}
