package cn.ittiger.player.view;

/**
 * Ijk 视频协议类，定义一些逻辑和UI的操作
 * Created by kiven on 2/2/18.
 */

public interface IjkVideoContract {

    /**
     * 视频UI实现类
     */
    interface IVideoView {

        //视频全屏
        void enterFullScreen();

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

    }

    interface IVideoPresenter {
        //处理视频container点击逻辑
        void handleVideoContainerLogic(int playState, boolean needHiden);
    }





}
