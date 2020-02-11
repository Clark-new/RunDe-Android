package com.bokecc.video.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.video.route.RTCMessage;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.video.HDVideoView;
import com.bokecc.video.widget.StatusView;
import com.bokecc.video.widget.ToastUtils;

import java.util.Formatter;
import java.util.Locale;

import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * 控制器基类
 */

public abstract class BaseVideoController extends FrameLayout {
    //控制器视图
    protected View mControllerView;
    //播放器
    protected MediaPlayerControl mPlayer;
    //控制器是否处于显示状态
    protected boolean mShowing = false;

    protected boolean mIsFullScreen = false;


    protected int mDefaultTimeout = 4000;

    private StringBuilder mFormatBuilder;

    private Formatter mFormatter;

    protected int mCurrentPlayState;

    protected StatusView mStatusView;

    protected OtherFunctionCallback otherFunctionCallback;
    protected RtcControlCallback rtcControlCallback;

    //当前RTC的状态
    protected int mCurrentRTCState = RTCMessage.RTC_STATE_DISABLE;

    protected DanmakuView danmakuView;

    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        danmakuView = new DanmakuView(getContext());
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mControllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), null);
        addView(danmakuView, params);
        addView(mControllerView, params);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mStatusView = new StatusView(getContext());
        setClickable(true);
        setFocusable(true);
    }

    public void setOtherFunctionCallback(OtherFunctionCallback callback) {
        otherFunctionCallback = callback;
    }

    public void setRtcControlCallback(RtcControlCallback callback) {
        this.rtcControlCallback = callback;
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 显示控制栏
     */
    public void show() {
    }

    /**
     * 隐藏控制栏
     */
    public void hide() {
    }

    /**
     * 设置是否是直播
     */
    public void setLive(boolean isLive) {
    }

    /**
     * 设置播放器状态
     */
    public void setPlayState(int playState) {
        mCurrentPlayState = playState;
        hideStatusView();
        switch (playState) {
            case HDVideoView.STATE_ERROR:
                mStatusView.setMessage("播放失败");
                mStatusView.setButtonTextAndAction("重试", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideStatusView();
                        mPlayer.replay(false);
                    }
                });
                this.addView(mStatusView, 0);
                break;
        }
    }

    public void showStatusView() {
        this.removeView(mStatusView);
        mStatusView.setMessage("您正在使用移动网络,继续播放将消耗流量");
        mStatusView.setButtonTextAndAction("继续播放", new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStatusView();
                HDVideoView.IS_PLAY_ON_MOBILE_NETWORK = true;
                mPlayer.mobileStart();
            }
        });
        this.addView(mStatusView);
    }

    public void hideStatusView() {
        this.removeView(mStatusView);
    }


    protected void doPauseResume() {
        if (mCurrentPlayState == HDVideoView.STATE_BUFFERING) return;
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
    }

    /**
     * 横竖屏切换
     */
    protected void doStartStopFullScreen() {
        Activity activity = CommonUtils.scanForActivity(getContext());
        if (activity == null) return;
        if (mIsFullScreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mIsFullScreen = false;
            onIntoOrStopFullScreen(false);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mIsFullScreen = true;
            onIntoOrStopFullScreen(true);
        }
    }


    /**
     * 进入或者离开横屏的时候回调
     */
    protected abstract void onIntoOrStopFullScreen(boolean isFullScreen);


    protected void doBack() {
        Activity activity = CommonUtils.scanForActivity(getContext());
        if (activity == null) return;
        if (mIsFullScreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mIsFullScreen = false;
        } else {
            activity.onBackPressed();
        }
    }


    /**
     * 显示播放进度
     */
    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    protected int setProgress() {
        return 0;
    }

    protected String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowProgress);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    /**
     * 进入全屏
     */
    public void startFullScreen() {
        Activity activity = CommonUtils.scanForActivity(getContext());
        if (activity == null) return;
        if (mIsFullScreen) return;
        mIsFullScreen = true;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 退出全屏
     */
    public void stopFullScreen() {
        Activity activity = CommonUtils.scanForActivity(getContext());
        if (activity == null) return;
        if (!mIsFullScreen) return;
        mIsFullScreen = false;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        this.mPlayer = mediaPlayer;
    }


    /**
     * 设置RTC连麦状态
     */
    public void setRtcState(int rtcState) {
        if (mCurrentRTCState == RTCMessage.RTC_STATE_CONNECTED &&
                rtcState == RTCMessage.RTC_STATE_DISCONNECTED) {
            ToastUtils.showRedToast(getContext(), "连麦已中断");
        }
        mCurrentRTCState = rtcState;
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }

}
