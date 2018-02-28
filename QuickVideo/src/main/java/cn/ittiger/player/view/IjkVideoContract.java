package cn.ittiger.player.view;

import android.support.annotation.StringRes;
import android.view.MotionEvent;

/**
 * Ijk 视频协议类，定义一些逻辑和UI的操作
 * Created by kiven on 2/2/18.
 */

public interface IjkVideoContract {

    /**
     * 视频UI实现类
     */
    interface IVideoView {

        //正常屏幕下-视频进入初始化状态
        void changeUINormal();

        void changeUILoading();

        //视频暂停
        void changeUIPause();

        //视频播放
        void changeUIPlay();

        //视频缓冲
        void changeUIBuffer();

        //隐藏所有控件
        void hidenAllView();

        //显示所有控件
        void showAllView();

        void changeUICompeted();

        void changeUIError();

        void changeUINetError();

        void startPlayVideo();

        void changeUIFullScreen();

        void changeUINormalScreen();

        void changeUIErrorToast();

        void changeUILock();

        void changeUIShowCover();

        void changeUIUnLock();

        void hideViewInFullScreenState();

        void showViewInFullScreenState();

        void showPositionRightAnimation(String seekTime, String totalTime);

        void showPositionLiftAnimation(String seekTime, String totalTime);

        void changeVolumeAnimation();

        void changeScreenBrightness(float deltaY);

        void hidePopView();

        void cancleDismissControlViewTimer();

        void startDismissFullScreenViewTimer();

        void showBrightnessAnimation();

        void changeMediaVolume(float deltaY);

        void showMobileDataDialog();

        void showToast(@StringRes int msg);

        void hideMobileDataDialog();

        void startDismissNormalViewTime();

        void updateCurPlayTime(String strSeekTime);
        //显示截屏信息
        void showCoverView();

        void changeUIBackBtnShow();
    }

    interface IVideoPresenter {
        //处理视频container点击逻辑
        void handleClickContainerLogic(int playState, int screenState, boolean needHiden);
        //处理屏幕选择
        void handleScreenRotate(int screenType, int screenState);

        void handleStartLogic(int mViewHash, String mVideoUrl, int state);

        void handleLockLogic();

        boolean handleContainerTouchLogic(int playState, MotionEvent event, int screenWidth, int screenHight, int screenType);

        void handleNetChangeLogic(int netType);

        void handleStopPlayMobileDataLogic();

        void handleContinuePlayMobileDataLogic(int playState);

        void handleAutoHideView(int requestedOrientation);

        boolean handleBottomSeekBarTouchLogic(int playState, MotionEvent event, int currentPosition, int position);

        void handleChangeUIState(int screenType, int playState, boolean hasFocus);
    }





}
