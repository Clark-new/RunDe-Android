package com.bokecc.video.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.rtc.CCRTCRender;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.route.RTCMessage;
import com.bokecc.video.widget.ToastUtils;

import org.webrtc.SurfaceViewRenderer;

public abstract class RTCVideoView extends BaseVideoView implements RTCController {

    private CCRTCRender mRemoteRender;

    public RTCVideoView(@NonNull Context context) {
        this(context, null);
    }

    public RTCVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RTCVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        mRemoteRender = new CCRTCRender(getContext().getApplicationContext());
        LayoutParams lp = new FrameLayout.LayoutParams(0, 0);
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        addView(mRemoteRender, lp);
        mRemoteRender.setVisibility(GONE);

        HDApi.get().setRtcRender(new SurfaceViewRenderer(getContext().getApplicationContext()), mRemoteRender);
    }


    /**
     * 连麦接通是对界面的处理
     */
    private void onRTCConnected() {
        //暂停播放器
        if (mPlayer != null) {
            mPlayer.pause();
            mPlayer.stop();
        }
        mRemoteRender.setVisibility(VISIBLE);
        mRemoteRender.setBackgroundColor(0xff000000);
    }

    /**
     * 连麦接通是对界面的处理
     */
    private void onRTCDisConnected() {
        //重新连接直播
        HDApi.get().reconnectVideo();
        mRemoteRender.setVisibility(GONE);
    }


    /**
     * RTC状态发生改变
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRtcStateChange(RTCMessage message) {
        if (mVideoController == null) return;
        int code = message.getCode();
        switch (code) {
            case RTCMessage.RTC_STATE_DISABLE:
                mVideoController.setRtcState(RTCMessage.RTC_STATE_DISABLE);
                break;
            case RTCMessage.RTC_STATE_ENABLE:
                mVideoController.setRtcState(RTCMessage.RTC_STATE_ENABLE);
                break;
            case RTCMessage.RTC_STATE_CONNECTED:
                onRTCConnected();
                mVideoController.setRtcState(RTCMessage.RTC_STATE_CONNECTED);
                break;
            case RTCMessage.RTC_STATE_DISCONNECTED:
                onRTCDisConnected();
                mVideoController.setRtcState(RTCMessage.RTC_STATE_DISCONNECTED);
                break;
        }
    }

    @Override
    public void applyVideoRtc() {
        HDApi.get().applyRtc(true);
        if (mVideoController != null) {
            mVideoController.setRtcState(RTCMessage.RTC_STATE_APPLYING);
        }
    }

    @Override
    public void applyAudioRtc() {

    }

    @Override
    public void handUpRtc() {
        HDApi.get().handUpRtc();
    }

    @Override
    public void handUpApplyRtc() {
        HDApi.get().handUpApplyRtc();
        if (mVideoController != null) {
            mVideoController.setRtcState(RTCMessage.RTC_STATE_DISCONNECTED);
        }
    }

    @Override
    public void release() {
        super.release();
        mRemoteRender.release();
    }
}
