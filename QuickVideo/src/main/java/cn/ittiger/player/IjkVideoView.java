package cn.ittiger.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ittiger.player.PlayerManager;
import cn.ittiger.player.ProgressHandler;
import cn.ittiger.player.R;
import cn.ittiger.player.R2;
import cn.ittiger.player.message.Message;
import cn.ittiger.player.message.UIStateMessage;
import cn.ittiger.player.state.PlayState;
import cn.ittiger.player.state.ScreenState;
import cn.ittiger.player.util.Utils;
import cn.ittiger.player.view.BatteryView;
import cn.ittiger.player.view.DigitalClock;
import cn.ittiger.player.view.IjkVideoContract;
import cn.ittiger.player.view.NetStatusView;

/**
 * Created by kiven on 2/1/18.
 */

public class IjkVideoView extends FrameLayout implements
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener,
        AudioManager.OnAudioFocusChangeListener,
        IjkVideoContract.IVideoView,
        Observer{

    @BindView(R2.id.surface_container)
    protected ViewGroup mTextureViewContainer; //渲染控件父类

    protected View mSmallClose; //小窗口关闭按键

    protected Map<String, String> mMapHeadData = new HashMap<>();

    protected TextureView mTextureView;

    @BindView(R2.id.btn_start)
    ImageView mStartButton;

    @BindView(R2.id.video_prop_window)
    RelativeLayout mPopView;

    @BindView(R2.id.video_time_ctrl_view)
    LinearLayout mPopTimeCtrlView;

    @BindView(R2.id.video_pop_icon)
    ImageView mPopIconView;

    @BindView(R2.id.video_pop_content)
    FrameLayout mPopContentView;

    @BindView(R2.id.tv_current)
    TextView mCurrentTimeView;

    @BindView(R2.id.tv_duration)
    protected TextView totalTimeText;

    @BindView(R2.id.video_pop_progress)
    protected ProgressBar popProgressBar;

    @BindView(R2.id.bottom_seekbar)
    protected SeekBar mBottomSeekBar;

    @BindView(R2.id.bottom_progressbar)
    protected ProgressBar mBottomProgressBar;

    @BindView(R2.id.icon_fullscreen)
    protected ImageView mFullscreenButton;

    @BindView(R2.id.bottom_tv_current)
    protected TextView mCurrentTimeTextView;

    @BindView(R2.id.bottom_tv_total)
    protected TextView mTotalTimeTextView;

    @BindView(R2.id.loading)
    RelativeLayout mLoadingProgressBar;

    @BindView(R2.id.layout_top)
    ViewGroup mTopContainer;

    @BindView(R2.id.layout_bottom)
    ViewGroup mBottomContainer;

    @BindView(R2.id.vp_video_thumb)
    ImageView mVideoThumbView;

    @BindView(R2.id.system_status_view)
    protected ViewGroup mTopStatusView;

    @BindView(R2.id.data)
    protected NetStatusView mWifiView;

    @BindView(R2.id.battery)
    protected BatteryView mBatteryView;

    @BindView(R2.id.time)
    protected DigitalClock mTimeView;

    @BindView(R2.id.icon_back)
    protected ImageView mBackButton;

    @BindView(R2.id.net_error)
    protected ViewGroup mNetView;

    @BindView(R2.id.net_error_again)
    protected TextView mRetryBtn; // retry btn when net error.

    @BindView(R2.id.video_replay_view)
    protected ViewGroup mReplayView;

    @BindView(R2.id.video_tv_replay)
    protected TextView mReplayBtn;

    @BindView(R2.id.lock_screen)
    protected ImageView mLockBtn;

    @BindView(R2.id.video_cover_img)
    protected ImageView mCoverView; // video cover img.

    @BindView(R2.id.tv_title)
    protected TextView mTitleTextView; //title

    @BindView(R2.id.player_buffer)
    TextView mBufferTextView; // 网络缓冲速度

    protected Bitmap mFullPauseBitmap = null;//暂停时的全屏图片；
    /**
     * 视频时长，miliseconds
     */
    private int mDuration = 0;
    /**
     * 当前Observer（即：VideoPlayerView本身）对象的hashcode
     */
    private int mViewHash;

    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;
    /**
     * 小窗口的宽度
     */
    private int mSmallWindowWidth;
    /**
     * 小窗口的高度
     */
    private int mSmallWindowHeight;

    /**
     * 视频标题
     */
    private CharSequence mVideoTitle;
    /**
     * 视频地址
     */
    private String mVideoUrl;


    protected int mCurrentState = -1; //当前的播放状态
    /**
     * 当前屏幕播放状态
     */
    private int mCurrentScreenState = ScreenState.SCREEN_STATE_NORMAL;

    private VideoPresenter mPresenter;

    /**
     * 正常状态下的标题是否显示
     */
    private boolean mShowNormalStateTitleView = true;
    private Handler mHandler;

    public IjkVideoView(@NonNull Context context) {
        super(context);
        initView(context);
        initData(context);
    }


    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initData(context);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initData(context);
    }



    @Override
    public void onAudioFocusChange(int focusChange) {

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PlayerManager.getInstance().addObserver(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mCurrentState != PlayState.STATE_NORMAL) {
            PlayerManager.getInstance().stop();
            onPlayStateChanged(PlayState.STATE_NORMAL);
        }
        PlayerManager.getInstance().removeObserver(this);
    }

    private void initView(Context context) {

        View videoView = LayoutInflater.from(context).inflate(R.layout.layout_ijk_video, this);
        ButterKnife.bind(this, videoView);
        //避免ListView中item点击无法响应的问题
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setBackgroundColor(Color.BLACK);
        mPresenter = new VideoPresenter(this);
    }

    private void initData(Context context) {
        mViewHash = this.toString().hashCode();
        mScreenWidth = Utils.getWindowWidth(context);
        mScreenHeight = Utils.getWindowHeight(context);
        mSmallWindowWidth = mScreenWidth / 2;
        mSmallWindowHeight = (int) (mSmallWindowWidth * 1.0f / 16 * 9 + 0.5f);
        mHandler = new ProgressHandler(this);
        mBottomSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 绑定数据
     * @param videoUrl
     */
    public void bind(String videoUrl, CharSequence title) {
        bind(videoUrl, title, mShowNormalStateTitleView);
        resetViewState();
    }

    private void resetViewState() {

        mCurrentState = PlayState.STATE_NORMAL;
        mCurrentScreenState = ScreenState.SCREEN_STATE_NORMAL;
        onPlayStateChanged(mCurrentState);
    }

    /**
     * 绑定数据
     * @param videoUrl
     */
    public void bind(String videoUrl, CharSequence title, boolean showNormalStateTitleView) {
        mShowNormalStateTitleView = showNormalStateTitleView;
        mVideoTitle = title;
        mVideoUrl = videoUrl;
        if(!TextUtils.isEmpty(mVideoTitle)) {
            mTitleTextView.setText(mVideoTitle);
        }
        resetViewState();
    }



    @Override
    public void onClick(View v) {

    }

    @OnClick(R2.id.surface_container)
    void onClickContainer() {
        if(!PlayerManager.getInstance().isViewPlaying(mViewHash)) {
            //存在正在播放的视频，先将上一个视频停止播放，再继续下一个视频的操作
            PlayerManager.getInstance().stop();
        }
        mPresenter.handleVideoContainerLogic(mCurrentState, mBottomContainer.getVisibility() ==VISIBLE);
    }


    @OnClick(R2.id.btn_start)
    void onClickStartBtn() {
        if (TextUtils.isEmpty(mVideoUrl)) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if(!PlayerManager.getInstance().isViewPlaying(mViewHash)) {
            //存在正在播放的视频，先将上一个视频停止播放，再继续下一个视频的操作
            PlayerManager.getInstance().stop();
        }
        switch (mCurrentState) {
            case PlayState.STATE_NORMAL:
            case PlayState.STATE_ERROR:
                startPlayVideo();
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
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser && seekBar != null) {
            int seekToTime = seekBar.getProgress() * mDuration / 100;
            PlayerManager.getInstance().seekTo(seekToTime);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void update(Observable o, final Object arg) {
        //判断当前layout是否销毁
        if(getContext() == null) {
            return;
        }
        //判断当前是否是
        if(!(arg instanceof Message)) {
            return;
        }

        if(mViewHash != ((Message) arg).getHash() ||
                !mVideoUrl.equals(((Message) arg).getVideoUrl())) {
            return;
        }

        if(!(arg instanceof UIStateMessage)) {
            return;
        }
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                onPlayStateChanged(((UIStateMessage) arg).getState());
            }
        });


    }

    private void onPlayStateChanged(int state) {
        mCurrentState = state;
        onChangeUIState(state);
    }

    public void onChangeUIState(int state) {
        switch (state) {
            case PlayState.STATE_NORMAL:
                changeUINormal();
                break;
            case PlayState.STATE_LOADING:
                changeUILoading();
                break;
            case PlayState.STATE_PLAYING:
                changeUIPlay();
                break;
            case PlayState.STATE_PAUSE:
                changeUIPause();
                break;
            case PlayState.STATE_PLAYING_BUFFERING_START:
                changeUIBuffer();
                break;
            case PlayState.STATE_AUTO_COMPLETE:
                changeUICompeted();
                break;
            case PlayState.STATE_ERROR:
                changeUIError();
                break;
            case PlayState.STATE_NET_ERROR:
                changeUINetError();
                break;
            default:
                throw new IllegalStateException("Illegal Play State:" + state);
        }
    }



    public ImageView getThumbImageView() {

        return mVideoThumbView;
    }

    public TextureView createTextureView() {
        //重新为播放器关联TextureView
        TextureView textureView = newTextureView();
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureView.setLayoutParams(params);
        return textureView;
    }

    /**
     * 创建一个TextureView
     * 此处单独写成一个方法可以方便后续自定义扩展TextureView
     *
     * @return
     */
    protected TextureView newTextureView() {

        return new TextureView(getContext());
    }


    /************************ UI状态更新 ********************************/
    @Override
    public void enterFullScreen() {
        //设置全屏按钮
        mFullscreenButton.setImageResource(R.drawable.news_video_full_off);
        //设置返回按钮
        mBackButton.setVisibility(View.VISIBLE);
        //设置锁屏按钮
        mLockBtn.setVisibility(VISIBLE);

        //设置右上角wifi，电量，等view
        mTopStatusView.setVisibility(VISIBLE);
    }

    @Override
    public void changeUICompeted() {
    //显示视频预览图
        Utils.showViewIfNeed(mVideoThumbView);
    }

    @Override
    public void changeUIError() {

    }

    @Override
    public void changeUILoading() {

        Utils.hideViewIfNeed(mStartButton);
        Utils.showViewIfNeed(mLoadingProgressBar);
    }

    @Override
    public void changeUINetError() {

    }

    @Override
    public void changeUINormal() {

        //显示视频预览图
        Utils.showViewIfNeed(mVideoThumbView);

        //显示视频标题
        Utils.showViewIfNeed(mTitleTextView);
        //隐藏视频返回键
        Utils.hideViewIfNeed(mBackButton);
        //底部布局隐藏
        mBottomContainer.setVisibility(View.INVISIBLE);
        //loading 布局隐藏
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        //锁屏按钮隐藏
        mLockBtn.setVisibility(GONE);
        //重播按钮隐藏
        mReplayView.setVisibility(GONE);
        //progressbar 隐藏
        mBottomProgressBar.setVisibility(View.INVISIBLE);
        //开始按钮显示
        mStartButton.setVisibility(View.VISIBLE);
        mStartButton.setImageResource(R.drawable.news_video_start);
    }

    @Override
    public void changeUIPause() {
        //顶部返回键显示
        mBackButton.setVisibility(View.VISIBLE);
        //顶部title显示
        Utils.showViewIfNeed(mTitleTextView);
        //loading 布局隐藏
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        //开始按钮显示
        mStartButton.setVisibility(View.VISIBLE);
        mStartButton.setImageResource(R.drawable.news_video_start);
        //底部控制布局显示
        mBottomContainer.setVisibility(View.VISIBLE);
        //底部progressbar 隐藏
        mBottomProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void changeUIPlay() {
        Utils.showViewIfNeed(mStartButton);
        mStartButton.setImageResource(R.drawable.news_video_pause);
        //隐藏视频预览图
        Utils.hideViewIfNeed(mVideoThumbView);

        Utils.hideViewIfNeed(mReplayView);
        Utils.hideViewIfNeed(mLoadingProgressBar);
        mHandler.sendEmptyMessageDelayed(ProgressHandler.UPDATE_CONTROLLER_VIEW, ProgressHandler.AUDO_HIDE_WIDGET_TIME);
    }

    @Override
    public void changeUIBuffer() {
        Utils.showViewIfNeed(mLoadingProgressBar);
        Utils.hideViewIfNeed(mStartButton);
    }

    @Override
    public void hidenAllView() {
        Utils.hideViewIfNeed(mTitleTextView);
        Utils.hideViewIfNeed(mBackButton);
        Utils.hideViewIfNeed(mWifiView);
        Utils.hideViewIfNeed(mBatteryView);
        Utils.hideViewIfNeed(mTimeView);
        Utils.hideViewIfNeed(mReplayView);
        Utils.hideViewIfNeed(mStartButton);
        Utils.hideViewIfNeed(mBottomContainer);
    }

    @Override
    public void showAllView() {
        Utils.showViewIfNeed(mTitleTextView);
        Utils.showViewIfNeed(mStartButton);
        Utils.showViewIfNeed(mBottomContainer);
        mHandler.removeMessages(ProgressHandler.UPDATE_CONTROLLER_VIEW);
        mHandler.sendEmptyMessageDelayed(ProgressHandler.UPDATE_CONTROLLER_VIEW, ProgressHandler.AUDO_HIDE_WIDGET_TIME);
    }

    /************************ 定时处理的工具 ********************************/

    /**
     * reset buffer view text to zero.
     */
    public void resetBufferProcess() {
        if (mBufferTextView != null) {
            mBufferTextView.setText(getResources().getString(R.string.text_buffer));
        }
    }


    /**
     * 更新loading速度
     */
    public void updateLoadingProgress(android.os.Message msg) {

        //TODO 根据IjkPlayer 获取
        int bufferCount = msg.arg1;
        bufferCount = (bufferCount > 100) ? 100 : bufferCount;
        String bufferText = bufferCount + "%";
        mBufferTextView.setText(bufferText);
        mBufferTextView.setVisibility(VISIBLE);
        mHandler.sendEmptyMessageDelayed(ProgressHandler.UPDATE_LOADING_SPEED, 500);
    }


    /**
     * 更新底部控制布局中的seekbar
     */
    public void updateBottomProgress(android.os.Message msg) {
        int position = msg.arg1;
        int totalTime = msg.arg2;
        int progress = position * 100 / (totalTime == 0 ? 1 : totalTime);

        if (progress != 0) {
            //可见的seekbar
            mBottomSeekBar.setProgress(progress);
            //底部最低的progressbar
            mBottomProgressBar.setProgress(progress);
        }

        if (mTotalTimeTextView != null && TextUtils.isEmpty(mTotalTimeTextView.getText().toString())) {
            mTotalTimeTextView.setText(Utils.stringForTime(totalTime));
        }
        if (position > 0) {
            mCurrentTimeTextView.setText(Utils.stringForTime(position));
        }

        if (!mHandler.hasMessages (ProgressHandler.UPDATE_BOTTOM_PROGRESS) && (PlayerManager.getInstance().isPlaying() || mCurrentState == PlayState.STATE_PLAYING)) {
            mHandler.sendEmptyMessageDelayed(ProgressHandler.UPDATE_BOTTOM_PROGRESS, 500);
        }
    }


    /**
     * 更新视频控制界面
     */
    public void updateControllerView() {
        if (mCurrentState != PlayState.STATE_NORMAL
                && mCurrentState != PlayState.STATE_ERROR
                && mCurrentState != PlayState.STATE_AUTO_COMPLETE) {
            hidenAllView();
            mLockBtn.setVisibility(INVISIBLE);
        }

    }


    /**
     * 开始播放视频
     */
    public void startPlayVideo() {

        if(!Utils.isConnected(getContext())) {
            if(!PlayerManager.getInstance().isCached(mVideoUrl)) {
                Toast.makeText(getContext(), R.string.vp_no_network, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        ((Activity)getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestAudioFocus();
        //先移除播放器关联的TextureView
        PlayerManager.getInstance().removeTextureView();

        TextureView textureView = createTextureView();
        mTextureViewContainer.addView(textureView);
        //准备开始播放
        PlayerManager.getInstance().start(mVideoUrl, mViewHash);
        PlayerManager.getInstance().setTextureView(textureView);
    }

    /**
     * 请求获取AudioFocus
     */
    private void requestAudioFocus() {

        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }
}
