package com.bokecc.video.video;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.controller.BaseVideoController;
import com.bokecc.video.controller.MediaPlayerControl;
import com.bokecc.video.utils.NetworkUtils;
import com.bokecc.video.widget.ResizeTextureView;

public abstract class BaseVideoView extends FrameLayout implements MediaPlayerControl, PlayerCallback {
    //播放器的各种状态
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;
    protected int mCurrentPlayState = STATE_IDLE;//当前播放器的状态

    //记录是否在移动网络下播放视频
    public static boolean IS_PLAY_ON_MOBILE_NETWORK = false;

    protected PlayerWrapper mPlayer;
    protected ResizeTextureView mTextureView;
    protected SurfaceTexture mSurfaceTexture;

    //盛装渲染视图的容器
    protected FrameLayout mRenderContainer;

    protected BaseVideoController mVideoController;//控制器


    public BaseVideoView(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CCEventBus.getDefault().register(this);
        initView();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    /**
     * 初始化播放器视图
     */
    protected void initView() {
        mRenderContainer = new FrameLayout(getContext());
        mRenderContainer.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mRenderContainer, params);
    }

    /**
     * 创建播放器实例，设置播放器参数，
     * 并且添加用于显示视频的View
     */
    public void init(Context ctx) {
        if (mPlayer == null) {
            mPlayer = new PlayerWrapper();
        }
        mPlayer.setPlayCallback(this);
        mPlayer.init(ctx);
        addTextureView();
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_IDLE);
            mVideoController.setLive(HDApi.get().getApiType() == HDApi.ApiType.LIVE);
        }
    }

    private void addTextureView() {
        mRenderContainer.removeView(mTextureView);
        mSurfaceTexture = null;
        mTextureView = new ResizeTextureView(getContext());
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                if (mSurfaceTexture != null) {
                    mTextureView.setSurfaceTexture(mSurfaceTexture);
                } else {
                    mSurfaceTexture = surfaceTexture;
                    mPlayer.setSurface(new Surface(surfaceTexture));
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return mSurfaceTexture == null;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mRenderContainer.addView(mTextureView, 0, params);
    }


    public void setVideoController(BaseVideoController mediaController) {
        removeView(mVideoController);
        mVideoController = mediaController;
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
        }
    }

    protected boolean checkNetwork() {
        if (NetworkUtils.getNetworkType(getContext()) == NetworkUtils.NETWORK_MOBILE
                && !IS_PLAY_ON_MOBILE_NETWORK) {
            if (mVideoController != null) {
                mVideoController.showStatusView();
            }
            return true;
        }
        return false;
    }


    @Override
    public void start() {
        mCurrentPlayState = STATE_PLAYING;
        if (mPlayer != null) {
            mPlayer.start();
        }
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_PLAYING);
        }
    }

    @Override
    public void pause() {
        mCurrentPlayState = STATE_PAUSED;
        if (mPlayer != null) {
            mPlayer.pause();
        }
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_PAUSED);
        }
    }

    @Override
    public long getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(long pos) {
        if (mPlayer != null) {
            mPlayer.seeTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferedPercentage() {
        if (mPlayer != null) {
            return mPlayer.getBufferedPercentage();
        }
        return 0;
    }

    @Override
    public void setSpeed(float speed) {
        if (mPlayer != null) {
            mPlayer.setSpeed(speed);
        }
    }


    @Override
    public void replay(boolean resetPosition) {

    }

    @Override
    public void onPrepared() {
        mCurrentPlayState = STATE_PREPARED;
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_PREPARED);
        }
    }

    @Override
    public void onStartRender() {
        mCurrentPlayState = STATE_PLAYING;
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_PLAYING);
        }
    }

    @Override
    public void onBufferEnd() {
        mCurrentPlayState = STATE_BUFFERED;
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_BUFFERED);
        }
    }

    @Override
    public void onBufferStart() {
        mCurrentPlayState = STATE_BUFFERING;
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_BUFFERING);
        }
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        mTextureView.setVideoSize(videoWidth, videoHeight);
    }

    @Override
    public void onError() {
        mCurrentPlayState = STATE_ERROR;
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_ERROR);
        }
    }

    public boolean onBackPressed() {
        return mVideoController != null && mVideoController.onBackPressed();
    }

    /**
     * 是否处于可播放状态
     */
    protected boolean isInPlaybackState() {
        return (mPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED);
    }

    public void release() {
        CCEventBus.getDefault().unregister(this);
        mRenderContainer.removeView(mTextureView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mPlayer.release();
        HDApi.get().release();

    }

}
