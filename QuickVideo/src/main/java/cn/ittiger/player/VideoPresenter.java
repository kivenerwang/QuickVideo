package cn.ittiger.player;

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
