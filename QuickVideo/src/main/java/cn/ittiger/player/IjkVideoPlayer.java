package cn.ittiger.player;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.TextureView;

import java.lang.ref.WeakReference;

import cn.ittiger.player.state.PlayState;
import tv.danmaku.ijk.media.player.AbstractMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by kiven on 1/29/18.
 */

public class IjkVideoPlayer extends AbsSimplePlayer implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener{
    private static final String TAG = "VideoMediaPlayer";
    private static final int MSG_PREPARE = 1;
    protected AbstractMediaPlayer mMediaPlayer;
    private HandlerThread mMediaHandlerThread;
    private MediaHandler mMediaHandler;
    private String mVideoUrl;
    private int mState;


    public IjkVideoPlayer() {
        mMediaPlayer = new IjkMediaPlayer();
        mMediaHandlerThread = new HandlerThread(TAG);
        mMediaHandlerThread.start();
        mMediaHandler = new MediaHandler(mMediaHandlerThread.getLooper(), this);
    }


    @Override
    public void start(String url) {
        this.mVideoUrl = url;
    }

    @Override
    public void play() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }

    }

    @Override
    public void pause() {
        if (getState() == PlayState.STATE_PLAYING) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void release() {
        if (mMediaHandler != null) {
            mMediaHandler.obtainMessage(MediaHandler.MSG_RELEASE).sendToTarget();
        }
    }

    @Override
    public void setState(int state) {
        this.mState = state;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer != null ? (int)mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getDuration() {
        return mMediaPlayer != null ? (int)mMediaPlayer.getDuration() : 0;
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    protected void prepare() {
        if (mMediaHandler != null) {
            mMediaHandler.obtainMessage(MediaHandler.MSG_PREPARE).sendToTarget();
        }
    }


    private void initOption(){
        if (false) {
            ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "mediacodec-auto-rotate", 1);
            ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "mediacodec-handle-resolution-change", 1);
        }
        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024L);

    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if(mPlayCallback != null) {
            mPlayCallback.onDurationChanged((int)mp.getDuration());
            mPlayCallback.onPlayStateChanged(PlayState.STATE_PLAYING);
        }
        play();
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        if(mPlayCallback != null) {
            mPlayCallback.onComplete();
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        if (mPlayCallback != null && isPlaying()) {
            mPlayCallback.onPlayStateChanged(PlayState.STATE_PLAYING);
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            mMediaPlayer.setSurface(new Surface(surface));
        } catch (Exception e) {

        }
        super.onSurfaceTextureAvailable(surface, width, height);
    }


    @Override
    public void setTextureView(TextureView textureView) {

        if(textureView == null && mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        super.setTextureView(textureView);
    }

    static class MediaHandler extends Handler {
        private WeakReference<IjkVideoPlayer> reference;
        public static final int MSG_PREPARE = 1;
        public static final int MSG_RELEASE = 2;
        public MediaHandler(Looper looper, IjkVideoPlayer player) {
            super(looper);
            reference = new WeakReference<IjkVideoPlayer>(player);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_RELEASE:
                    reference.get().releaseMedia();
                    break;
                case MSG_PREPARE:
                    reference.get().prepareMedia();
                    break;
                default:
            }
        }
    }

    private void prepareMedia() {
        try {
            mMediaPlayer.release();
            mMediaPlayer = new IjkMediaPlayer();
            initOption();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(IjkVideoPlayer.this);
            mMediaPlayer.setOnCompletionListener(IjkVideoPlayer.this);
            mMediaPlayer.setOnBufferingUpdateListener(IjkVideoPlayer.this);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnSeekCompleteListener(IjkVideoPlayer.this);
            mMediaPlayer.setOnErrorListener(IjkVideoPlayer.this);
            mMediaPlayer.setOnInfoListener(IjkVideoPlayer.this);
            mMediaPlayer.setDataSource(mVideoUrl);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseMedia() {
        mMediaPlayer.release();
    }

}
