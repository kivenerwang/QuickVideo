package cn.ittiger.player.state;

/**
 * 屏幕状态
 * @author: ylhu
 * @time: 17-9-14
 */
public final class ScreenState {
    /**
     * 正常播放状态
     */
    public static final int SCREEN_STATE_NORMAL = 1;
    /**
     * 全屏播放状态
     */
    public static final int SCREEN_STATE_FULLSCREEN = 0;
    /**
     * 小窗口播放状态
     */
    public static final int SCREEN_STATE_SMALL_WINDOW = 3;

    private int mScreenWidth;

    private int mScreenHight;

    public int getScreenHight() {
        return mScreenHight;
    }

    public void setScreenHight(int mScreenHight) {
        this.mScreenHight = mScreenHight;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public void setScreenWidth(int mScreenWidth) {
        this.mScreenWidth = mScreenWidth;
    }

    public static boolean isFullScreen(int screenState) {

        return screenState == SCREEN_STATE_FULLSCREEN;
    }

    public static boolean isSmallWindow(int screenState) {

        return screenState == SCREEN_STATE_SMALL_WINDOW;
    }

    public static boolean isNormal(int screenState) {

        return screenState == SCREEN_STATE_NORMAL;
    }
}
