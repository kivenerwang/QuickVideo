package cn.ittiger.player;

import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.view.MotionEvent;

import cn.ittiger.player.state.PlayState;
import cn.ittiger.player.state.ScreenState;
import cn.ittiger.player.util.Utils;
import cn.ittiger.player.view.IjkVideoContract;

/**
 * Created by kiven on 2/2/18.
 */

public class VideoPresenter implements IjkVideoContract.IVideoPresenter{

    private IjkVideoContract.IVideoView mVideoView;

    private boolean mLockState; //锁屏状态

    private int mScreenState; //屏幕横竖屏状态

    private float mLastPositonX; //上次触摸的位置

    private float mLastPositonY; //上次触摸的位置

    protected int THRES_HOLD = 80; //手势偏差值

    private boolean isLastTouchFinish = false;

    private boolean mNeedShowWifiTip = true;

    private static final int TOUCH_TYPE_HORIZONTAL = 1;
    //竖向触摸屏幕左边
    private static final int TOUCH_TYPE_VERTICAL_LEFT = 2;
    //竖向触摸屏幕右边
    private static final int TOUCH_TYPE_VERTICAL_RIGHT =3;

    public VideoPresenter(IjkVideoContract.IVideoView videoView) {
        this.mVideoView = videoView;
    }


    @Override
    public void handleNetChangeLogic(int netType) {
        if (PlayerManager.getInstance().isPlaying()) {
            if (netType ==  ConnectivityManager.TYPE_WIFI) {

            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                handleMobileDataLogic();
            } else if (netType == -1) {

            }
        }
    }

    /**
     * 处理当前流量情况下的逻辑
     */
    private void handleMobileDataLogic() {
        if (isNeedShowWifiTip()) {
            PlayerManager.getInstance().pause();
            mVideoView.showMobileDataDialog();
        } else {
            mVideoView.showMobileToast();
        }
    }

    @Override
    public void handleVideoContainerLogic(int playState, boolean needHiden) {
        if (mLockState) {
            //屏幕锁住状态，不做任何处理。
            return;
        }
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
            case PlayState.STATE_PAUSE:
                //当前暂停状态，需要显示startbtn
                handleViewState(false);
                break;
        }
    }

    @Override
    public void handleScreenRotate(int screenType) {
        if (screenType == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //当前小屏，需要且成全屏
            mVideoView.changeUIFullScreen();
            mScreenState = ScreenState.SCREEN_STATE_FULLSCREEN;
        } else {
            //当前屏幕是全屏，需要切出正常屏幕
            mVideoView.changeUINormalScreen();
            mScreenState = ScreenState.SCREEN_STATE_NORMAL;
        }
    }

    @Override
    public void handleStartLogic(int mViewHash, String mVideoUrl, int state) {
        if (TextUtils.isEmpty(mVideoUrl)) {
            mVideoView.changeUIErrorToast();
        }
        if(!PlayerManager.getInstance().isViewPlaying(mViewHash)) {
            //存在正在播放的视频，先将上一个视频停止播放，再继续下一个视频的操作
            PlayerManager.getInstance().stop();
        }

        switch (state) {
            case PlayState.STATE_NORMAL:
            case PlayState.STATE_ERROR:
                mVideoView.startPlayVideo();
                break;
            case PlayState.STATE_PLAYING:
                PlayerManager.getInstance().pause();
                break;
            case PlayState.STATE_PAUSE:
                PlayerManager.getInstance().play();
                break;
            case PlayState.STATE_AUTO_COMPLETE:
                PlayerManager.getInstance().seekTo(0);
                PlayerManager.getInstance().play();
                break;
        }
    }

    @Override
    public void handleLockLogic() {
        if (mLockState) {
            mVideoView.changeuiUnLock();
            mVideoView.startDismissControlViewTimer();
        } else {
            mVideoView.changeUILock();
            mVideoView.cancleDismissControlViewTimer();
        }
        mLockState = !mLockState;
    }

    @Override
    public boolean handleContainerTouchLogic(int playState, MotionEvent event, int screenWidth, int screenHight) {
        //1.判断屏幕状态
        if (!ScreenState.isFullScreen(mScreenState)) {
            //非全屏状态返回
            return false;
        }
        //2.判断锁屏状态
        if (mLockState) {
            return false;
        }
        //3.判断播放状态
        if (playState != PlayState.STATE_PLAYING &&  playState != PlayState.STATE_PAUSE && playState != PlayState.STATE_PLAYING_BUFFERING_START) {
            return false;
        }
        int touchType = -1;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastPositonX = event.getX();
            mLastPositonY = event.getY();
            isLastTouchFinish = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //首先判断触摸事件的类型是横向滑动还是纵向滑动
            if (isLastTouchFinish) {
                touchType = getTouchType(event, screenWidth);
            }
            if (touchType == TOUCH_TYPE_HORIZONTAL) {
                float deltaX = event.getX() - mLastPositonX;
                handlePositionMoveLogic(deltaX, screenWidth);
            } else if (touchType ==  TOUCH_TYPE_VERTICAL_RIGHT) {
                float deltaY = event.getY() - mLastPositonY;
                handlVolumeChangeLogic(deltaY);
            } else if (touchType == TOUCH_TYPE_VERTICAL_LEFT) {
                float deltaY = event.getY() - mLastPositonY;
                deltaY = (-deltaY / screenHight);
                mVideoView.changeScreenBrightness(deltaY);
                mVideoView.showBrightnessAnimation();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mVideoView.hidePopView();
        }
        //最后计时器再次处理
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mVideoView.cancleDismissControlViewTimer();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mVideoView.startDismissControlViewTimer();
            isLastTouchFinish = true;
        }
        return false;
    }

    private void handlVolumeChangeLogic(float deltaY) {
        deltaY = -deltaY;

        mVideoView.changeVolumeAnimation();
        mVideoView.changeMediaVolume(deltaY);
    }

    private void handlePositionMoveLogic(float deltaX, int screenWidth) {
        int seekPostition;
        int downPostion = PlayerManager.getInstance().getCurrentPosition();
        int videoTime = PlayerManager.getInstance().getTotalTime();
        if (deltaX > 0) {
            seekPostition = (int) (downPostion + (deltaX - THRES_HOLD) *
                    videoTime / screenWidth);
        } else {
            seekPostition = (int) (downPostion + (deltaX + THRES_HOLD) *
                    videoTime / screenWidth);
        }
        if (seekPostition < 0){
            seekPostition = 0;
        }
        if (seekPostition > videoTime){
            seekPostition = videoTime;
        }
        String seekTime = Utils.stringForTime(seekPostition);
        String totalTime = Utils.stringForTime(videoTime);
        if (deltaX > 0) {
            //向右移动
            mVideoView.showPositionRightAnimation(seekTime, totalTime);
        } else {
            mVideoView.showPositionLiftAnimation(seekTime, totalTime);
        }
    }

    /**
     * 获取触摸的类型
     * @param event
     * @return
     */
    private int getTouchType(MotionEvent event, int screenWidth) {
        int type = 0;
        float delteX = event.getX() - mLastPositonX;
        float delteY = event.getY() - mLastPositonY;
        float absDelteX = Math.abs(delteX);
        float absDelteY = Math.abs(delteY);
        if (absDelteX > THRES_HOLD) {
            //横向滑动
            isLastTouchFinish = false;
            return TOUCH_TYPE_HORIZONTAL;
        } else if (absDelteY > THRES_HOLD) {
            // 纵向滑动
            if (isLastTouchFinish) {
                if (mLastPositonX < screenWidth * 0.5) {
                    type =  TOUCH_TYPE_VERTICAL_LEFT;
                    isLastTouchFinish = false;
                    return type;
                }
            }
            if (type != TOUCH_TYPE_VERTICAL_LEFT) {
                type = TOUCH_TYPE_VERTICAL_RIGHT;
                isLastTouchFinish = false;
                return type;
            }
        }

        return type;
    }

    /**
     * 处理播放
     * @param needHiden
     */
    private void handleViewState(boolean needHiden) {

        if (ScreenState.isFullScreen(mScreenState)) {
            //全屏操作操作
            if (needHiden) {
                //隐藏全屏状态下的UI控件
                mVideoView.hideViewInFullScreenState();
            } else {
                mVideoView.showViewInFullScreenState();
            }

        } else {
            //正常屏幕操作
            if (needHiden) {
                mVideoView.hidenAllView();
            } else {
                mVideoView.showAllView();
            }
        }


    }

    @Override
    public void handleContinuePlayMobileDataLogic() {
        setNeedShowWifiTip(false);
        PlayerManager.getInstance().play();
    }



    @Override
    public void handleStopPlayMobileDataLogic() {
        setNeedShowWifiTip(true);
        mVideoView.hideMobileDataDialog();
    }

    public boolean isNeedShowWifiTip() {
        return mNeedShowWifiTip;
    }

    public void setNeedShowWifiTip(boolean mNeedShowWifiTip) {
        this.mNeedShowWifiTip = mNeedShowWifiTip;
    }
}
