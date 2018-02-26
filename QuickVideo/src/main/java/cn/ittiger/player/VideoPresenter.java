package cn.ittiger.player;

import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.quickplayer.CompatApplication;
import com.quickplayer.utils.NetworkUtils;

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
    public void handleNetChangeLogic(int netType) {
        Log.i("dongdong","handle net  change =" + netType);
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
    private boolean handleMobileDataLogic() {
        boolean result;
        if (CompatApplication.isNeedShowWifiTip()) {
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
    public void handleClickContainerLogic(int playState, int screenState, boolean needHiden) {
        if (mLockState) {
            //屏幕锁住状态，不做任何处理。
            return;
        }

        switch (playState) {
            case PlayState.STATE_NORMAL:
                if (!isNetworkAvailable()) {
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
    public void handleHideView(int requestedOrientation) {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mVideoView.startDismissControlViewTimer();
        } else {
            mVideoView.startDismissNormalViewTime();
        }
    }

    /**
     * 判断当前网络状态是否可以播放的逻辑
     * @return
     */
    private boolean isNetworkAvailable() {
        boolean enable = true;
        if (NetworkUtils.isNetworkAvailable()) {
            if (NetworkUtils.isMobileConnected()) {
                enable = handleMobileDataLogic();
            }
        } else {
            mVideoView.showToast(R.string.net_error_text);
            enable = false;
        }
        return enable;
    }

    @Override
    public void handleScreenRotate(int screenType) {
        if (mLockState) {
            //锁屏状态
            mVideoView.showToast(R.string.tips_screen_locked);
            return;
        }
        if (screenType == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //当前小屏，需要且成全屏
            mVideoView.changeUIFullScreen();
        } else {
            //当前屏幕是全屏，需要切出正常屏幕
            mVideoView.changeUINormalScreen();
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

        if (!isNetworkAvailable()) {
            return;
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
            default:
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
                mVideoView.hidePopView();
                mVideoView.startDismissControlViewTimer();
                break;
            default:
                break;
        }

        return false;
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
                mVideoView.hidePopView();
                mVideoView.startDismissControlViewTimer();
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
                mVideoView.startDismissNormalViewTime();
            }

        } else {
            //正常屏幕操作
            if (needHiden) {
                mVideoView.hidenAllView();
            } else {
                mVideoView.showAllView();
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
        CompatApplication.setNeedShowWifiTip(false);
        mVideoView.hideMobileDataDialog();

    }



    @Override
    public void handleStopPlayMobileDataLogic() {
        CompatApplication.setNeedShowWifiTip(true);
        mVideoView.hideMobileDataDialog();
    }
}
