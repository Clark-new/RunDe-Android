package com.bokecc.video.video;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.route.OnVideoSwitchMsg;

/**
 * 视频播放视图
 */
public class HDVideoView extends RTCVideoView {

    private static final String TAG = "HDVideoView";

    public HDVideoView(@NonNull Context context) {
        this(context, null);
    }

    public HDVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HDVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void videoStart() {
        if (checkNetwork()) return;

        if (mVideoController != null) {
            if (mCurrentPlayState == STATE_PLAYING || mCurrentPlayState == STATE_BUFFERED) {
                mVideoController.setPlayState(STATE_PLAYING);
            } else if (mCurrentPlayState != STATE_PAUSED) {
                mVideoController.setPlayState(STATE_PREPARING);
            } else {
                mVideoController.setPlayState(STATE_PLAYING);
            }
        }
        if(mCurrentPlayState == STATE_IDLE){
            HDApi.get().setLiveParams();
        }
        HDApi.get().start();
    }


    public void videoPuase() {
        mCurrentPlayState = STATE_PAUSED;
        HDApi.get().pause();
        if (mVideoController != null) {
            mVideoController.setPlayState(STATE_PAUSED);
        }
    }

    public void videoDestroy() {
        HDApi.get().stop();
        mCurrentPlayState = STATE_IDLE;
    }


    /**
     * 视频发生切换
     */
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 10)
    public void onVideoSwitch(OnVideoSwitchMsg message) {
        if (message.getType() == OnVideoSwitchMsg.START) {
            init(getContext());
        }
    }

    @Override
    public void mobileStart() {
        videoStart();
    }
}
