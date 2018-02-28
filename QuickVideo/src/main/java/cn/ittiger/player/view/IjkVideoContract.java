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
        //视频UI 进入Loading状态
        void changeUILoading();

        //视频UI 进入暂停状态
        void changeUIPause();

        //视频播放中UI
        void changeUIPlay();

        //视频缓冲UI
        void changeUIBuffer();
        //视频播放完成UI
        void changeUICompeted();

        //视频播放错误UI
        void changeUIError();

        //视频播放网络错误UI
        void changeUINetError();

        //开始播放
        void startPlayVideo();

        //进入全屏UI
        void changeUIFullScreen();

        //退出全屏UI
        void changeUINormalScreen();

        //视频锁屏UI
        void changeUILock();

        //视频屏幕解锁UI
        void changeUIUnLock();

        //视频显示封面
        void changeUIShowCover();

        //隐藏正常屏幕下的控件
        void hidenNormalScreenView();

        //显示正常屏幕下控件
        void showNormalScreenView();

        //隐藏全屏状态下的控件
        void hideViewInFullScreenState();

        //显示全屏状态下的控件
        void showViewInFullScreenState();

        //网络错误提示
        void showErrorToast();

        //视频快进动画
        void showPositionRightAnimation(String seekTime, String totalTime);

        //视频快退动画
        void showPositionLiftAnimation(String seekTime, String totalTime);

        //改变视频音量
        void changeMediaVolume(float deltaY);

        //视频改变音量动画
        void changeVolumeAnimation();

        //视频改变屏幕亮度
        void changeScreenBrightness(float deltaY);

        //显示视频改变屏幕亮度的动画
        void showBrightnessAnimation();

        //隐藏音量，进度，亮度动画
        void hideAnimationView();

        //取消自动隐藏视频播放控制器的定时器
        void cancleDismissControlViewTimer();

        //开启自动隐藏全屏状态下的播放控制器的定时器
        void startDismissFullScreenViewTimer();

        //开启自动隐藏正常状态下的播放控制器的定时器
        void startDismissNormalViewTime();

        //显示消耗流量播放的弹框提示
        void showMobileDataDialog();

        //显示toast提示
        void showToast(@StringRes int msg);

        //隐藏流量提醒弹框提示
        void hideMobileDataDialog();

        //更新当前的播放视频
        void updateCurPlayTime(String strSeekTime);
        //显示截屏信息
        void showCoverView();

        //视频返回键显示
        void changeUIBackBtnShow();
        //开始更新底部播放信息
        void startUpdateBottomPlayInfo();
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
