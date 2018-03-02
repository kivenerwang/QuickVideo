package com.browser2345.player;

import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import com.quickplayer.utils.NetworkUtils;

import com.browser2345.player.state.PlayState;
import com.browser2345.player.state.ScreenState;
import com.browser2345.player.util.Utils;
import com.browser2345.player.view.IjkVideoContract;

/**
 * Created by kiven on 2/2/18.
 */

public class VideoPresenter implements IjkVideoContract.IVideoPresenter{

    private IjkVideoContract.IVideoView mVideoView;

    private boolean mLockState; //锁屏状态

    private float mLastPositonX; //上次触摸的位置

    private float mLastPositonY; //上次触摸的位置

    private int THRES_HOLD = 80; //手势偏差值
    private int touchType = -1;

    public boolean isLastTouchFinish() {
        return isLastTouchFinish;
    }

    public void setLastTouchFinish(boolean lastTouchFinish) {
        isLastTouchFinish = lastTouchFinish;
    }

    private boolean isLastTouchFinish = false;

    private static final int TOUCH_TYPE_HORIZONTAL = 1;
    //竖向触摸屏幕左边
    private static final int TOUCH_TYPE_VERTICAL_LEFT = 2;
    //竖向触摸屏幕右边
    private static final int TOUCH_TYPE_VERTICAL_RIGHT =3;

    public VideoPresenter(IjkVideoContract.IVideoView videoView) {
        this.mVideoView = videoView;
    }


    @Override
    public void handleNetChangeLogic(int netType, boolean needWifiTip) {
        if (PlayerManager.getInstance().isPlaying()) {
            if (netType ==  ConnectivityManager.TYPE_WIFI) {

            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                handleMobileDataLogic(needWifiTip);
            } else if (netType == -1) {

            }
        }
    }

    /**
     * 处理当前流量情况下的逻辑
     * @param needWifiTip
     */
    private boolean handleMobileDataLogic(boolean needWifiTip) {
        boolean result;
        if (needWifiTip) {
            PlayerManager.getInstance().pause();
            mVideoView.showMobileDataDialog();
            result = false;
        } else {
            mVideoView.showToast(R.string.tips_play_mobile_data);
            result = true;
        }
        return result;
    }

    @Override
    public void handleClickContainerLogic(int playState, int screenState, boolean needHiden, boolean needWifiTip) {
        if (mLockState) {
            //屏幕锁住状态，不做任何处理。
            return;
        }
        switch (playState) {
            case PlayState.STATE_NORMAL:
                if (!isNetworkAvailable(needWifiTip)) {
                    return;
                }
                mVideoView.startPlayVideo();
                break;
            case PlayState.STATE_AUTO_COMPLETE:
                //播放完成
            case PlayState.STATE_NET_ERROR:
                //网络错误
                break;
            case PlayState.STATE_PLAYING:
                handleViewState(needHiden, screenState);
                break;
            case PlayState.STATE_PAUSE:
                //当前暂停状态，需要显示startbtn
                handleViewState(false, screenState);
                break;
            default:
                break;
        }
    }

    @Override
    public void handleAutoHideView(int requestedOrientation) {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mVideoView.startDismissFullScreenViewTimer();
        } else {
            mVideoView.startDismissNormalViewTime();
        }
    }

    /**
     * 判断当前网络状态是否可以播放的逻辑
     * @return
     */
    private boolean isNetworkAvailable(boolean needWifiTip) {
        boolean enable = true;
        if (NetworkUtils.isNetworkAvailable()) {
            if (NetworkUtils.isMobileConnected()) {
                enable = handleMobileDataLogic(needWifiTip);
            }
        } else {
            mVideoView.showToast(R.string.net_error_text);
            enable = false;
        }
        return enable;
    }

    @Override
    public void handleScreenRotate(int screenType, int playState) {
        if (mLockState) {
            //锁屏状态
            mVideoView.showToast(R.string.tips_screen_locked);
            return;
        }
        mVideoView.cancleDismissControlViewTimer();
        //判断的播放状态，是否需要截屏
        if ((playState == PlayState.STATE_PAUSE
                || playState == PlayState.STATE_PLAYING_BUFFERING_START)) {

            mVideoView.makeScreenShotsInfo();
        }

        if (ScreenState.isNormal(screenType)) {
            //当前小屏，需要且成全屏
            mVideoView.changeUIFullScreen();
        } else {
            //当前屏幕是全屏，需要切出正常屏幕
            mVideoView.changeUINormalScreen();
        }
        //屏幕选择
        if (playState != PlayState.STATE_PAUSE) {
            if (ScreenState.isNormal(screenType)) {
                mVideoView.startDismissFullScreenViewTimer();
            } else {
                mVideoView.startDismissNormalViewTime();
            }

        }
    }

    @Override
    public void handleClickStartLogic(int mViewHash, String mVideoUrl, int state, boolean needWifiTip) {
        if (TextUtils.isEmpty(mVideoUrl)) {
            mVideoView.showToast(R.string.no_url);
        }
        if(!PlayerManager.getInstance().isViewPlaying(mViewHash)) {
            //存在正在播放的视频，先将上一个视频停止播放，再继续下一个视频的操作
            PlayerManager.getInstance().stop();
        }

        if (!isNetworkAvailable(needWifiTip)) {
            return;
        }

        switch (state) {
            case PlayState.STATE_NORMAL:
            case PlayState.STATE_ERROR:
                mVideoView.startPlayVideo();
                break;
            case PlayState.STATE_PLAYING:
                PlayerManager.getInstance().pause();
                mVideoView.cancleDismissControlViewTimer();
                break;
            case PlayState.STATE_PAUSE:
                PlayerManager.getInstance().play();
                break;
            case PlayState.STATE_AUTO_COMPLETE:
                PlayerManager.getInstance().seekTo(0);
                PlayerManager.getInstance().play();
                mVideoView.startUpdateBottomPlayInfo();
                break;
            default:
                break;
        }
    }

    @Override
    public void handleLockLogic() {
        if (mLockState) {
            mVideoView.changeUIUnLock();
            mVideoView.startDismissFullScreenViewTimer();
        } else {
            mVideoView.changeUILock();
            mVideoView.cancleDismissControlViewTimer();
        }
        mLockState = !mLockState;
    }


    @Override
    public boolean handleBottomSeekBarTouchLogic(int playState, MotionEvent event, int currentPosition, int screenType) {

        if (!isTouchAvailable(playState, screenType)){
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastPositonX = event.getX();
                mVideoView.cancleDismissControlViewTimer();
                break;
            case MotionEvent.ACTION_MOVE:
                handleSeekBarMove(event, currentPosition);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVideoView.hideAnimationView();
                mVideoView.startDismissFullScreenViewTimer();
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void handleChangeUIState(int screenType, int playState, boolean hasFocus) {
        if (playState != PlayState.STATE_NORMAL && !hasFocus) {
            mVideoView.makeScreenShotsInfo();
            mVideoView.showScreenShots();
            //失去焦點，需要顯示視頻暫停的UI
            mVideoView.changeUIPause();
        } else {
            updateVideoUIState(playState);
        }
        //继续处理全屏状态
        if (ScreenState.isFullScreen(screenType)) {
            mVideoView.changeUIBackBtnShow();
        }

    }

    /**
     * 更新視頻UI
     * @param playState
     */
    private void updateVideoUIState(int playState) {
        switch (playState) {
            case PlayState.STATE_NORMAL:
                mVideoView.changeUINormal();
                break;
            case PlayState.STATE_LOADING:
                mVideoView.changeUILoading();
                break;
            case PlayState.STATE_PLAYING:
                mVideoView.changeUIPlay();
                mVideoView.releaseScreenShots();
                break;
            case PlayState.STATE_PAUSE:
                mVideoView.changeUIPause();
                break;
            case PlayState.STATE_PLAYING_BUFFERING_START:
                mVideoView.changeUIBufferStart();
                break;
            case PlayState.STATE_AUTO_COMPLETE:
                mVideoView.changeUICompeted();
                break;
            case PlayState.STATE_ERROR:
                mVideoView.changeUIError();
                break;
            case PlayState.STATE_NET_ERROR:
                mVideoView.changeUINetError();
                break;
            default:
                throw new IllegalStateException("Illegal Play State:" + playState);
        }
    }

    @Override
    public void handleViewChange(int playState) {
        if (playState == PlayState.STATE_PAUSE) {
            mVideoView.showScreenShots();

        }
    }

    /**
     * 判断当前的触摸事件是否有效
     * @param playState
     * @return
     */
    private boolean isTouchAvailable(int playState, int screenState) {
        boolean available = true;
        //1.判断屏幕状态
        if (!ScreenState.isFullScreen(screenState)) {
            //非全屏状态返回
            available =  false;
        }
        //2.判断锁屏状态
        if (mLockState) {
            available =  false;
        }
        //3.判断播放状态
        if (playState != PlayState.STATE_PLAYING &&  playState != PlayState.STATE_PAUSE && playState != PlayState.STATE_PLAYING_BUFFERING_START) {
            available =  false;
        }
        return available;
    }

    private void handleSeekBarMove(MotionEvent event, int currentPosition) {
        int totalTime = PlayerManager.getInstance().getTotalTime();
        float deltaX = event.getX() - mLastPositonX;
        int seekTimePosition = (currentPosition * totalTime / 100);

        if (seekTimePosition > totalTime) {
            seekTimePosition = totalTime;
        }
        String strSeekTime = Utils.stringForTime(seekTimePosition);
        String strTotalTime = Utils.stringForTime(totalTime);
        if (deltaX > 0) {
            mVideoView.showPositionRightAnimation(strSeekTime,strTotalTime);
        } else {
            mVideoView.showPositionLiftAnimation(strSeekTime, strTotalTime);
        }
        mVideoView.updateCurPlayTime(strSeekTime);
    }

    @Override
    public boolean handleContainerTouchLogic(int playState, MotionEvent event, int screenWidth, int screenHight, int screenType) {
        if (!isTouchAvailable(playState, screenType)){
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastPositonX = event.getX();
                mLastPositonY = event.getY();
                setLastTouchFinish(true);
                mVideoView.cancleDismissControlViewTimer();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isLastTouchFinish()) {
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
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVideoView.hideAnimationView();
                mVideoView.startDismissFullScreenViewTimer();
                setLastTouchFinish(true);
                break;
            default:
                break;
        }

        return false;
    }

    /**
     * 处理音量变化的逻辑
     * @param deltaY
     */
    private void handlVolumeChangeLogic(float deltaY) {
        deltaY = -deltaY;

        mVideoView.changeVolumeAnimation();
        mVideoView.changeMediaVolume(deltaY);
    }


    /**
     * 处理位置变化的逻辑操作
     * @param deltaX
     * @param screenWidth
     */
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
        PlayerManager.getInstance().seekTo(seekPostition);
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
            setLastTouchFinish(false);
            return TOUCH_TYPE_HORIZONTAL;
        } else if (absDelteY > THRES_HOLD) {
            // 纵向滑动
            if (isLastTouchFinish) {
                if (mLastPositonX < screenWidth * 0.5) {
                    type =  TOUCH_TYPE_VERTICAL_LEFT;
                    setLastTouchFinish(false);
                    return type;
                }
            }
            if (type != TOUCH_TYPE_VERTICAL_LEFT) {
                type = TOUCH_TYPE_VERTICAL_RIGHT;
                setLastTouchFinish(false);
                return type;
            }
        }

        return type;
    }

    /**
     * 处理播放
     * @param needHiden
     * @param screenState
     */
    private void handleViewState(boolean needHiden, int screenState) {

        if (ScreenState.isFullScreen(screenState)) {
            //全屏操作操作
            if (needHiden) {
                //隐藏全屏状态下的UI控件
                mVideoView.hideViewInFullScreenState();
            } else {
                mVideoView.showViewInFullScreenState();
                mVideoView.startDismissFullScreenViewTimer();
            }

        } else {
            //正常屏幕操作
            if (needHiden) {
                mVideoView.hidenNormalScreenView();
            } else {
                mVideoView.showNormalScreenView();
                mVideoView.startDismissNormalViewTime();
            }
        }


    }

    @Override
    public void handleContinuePlayMobileDataLogic(int playState) {
        if (playState == PlayState.STATE_NORMAL) {
            mVideoView.startPlayVideo();
        } else if (playState == PlayState.STATE_PAUSE){
            PlayerManager.getInstance().play();
        }
        mVideoView.hideMobileDataDialog();

    }



    @Override
    public void handleStopPlayMobileDataLogic() {
        mVideoView.hideMobileDataDialog();
        mVideoView.setNeedWifiTip(true);
    }
}
