package com.bokecc.video.api;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.rtc.RtcClient;
import com.bokecc.video.route.RTCMessage;

public class RTCListener implements RtcClient.RtcClientListener {

    /**
     * 主播端开启连麦通知
     * {@linkplain com.bokecc.video.video.RTCVideoView#onRtcStateChange(RTCMessage)}
     */
    @Override
    public void onAllowSpeakStatus(boolean isAllowSpeak) {
        CCEventBus.getDefault().post(new RTCMessage(isAllowSpeak ?
                RTCMessage.RTC_STATE_ENABLE
                : RTCMessage.RTC_STATE_DISABLE));
    }

    /**
     * videoSize:"640x480"
     * needAdjustSize:字段已无效
     * 主播接受连麦
     */
    @Override
    public void onEnterSpeak(boolean isVideoRtc, boolean needAdjustSize, String videoSize) {
        CCEventBus.getDefault().post(new RTCMessage(RTCMessage.RTC_STATE_CONNECTED));
    }

    @Override
    public void onDisconnectSpeak() {
        CCEventBus.getDefault().post(new RTCMessage(RTCMessage.RTC_STATE_DISCONNECTED));
    }

    @Override
    public void onSpeakError(Exception e) {

    }

    @Override
    public void onCameraOpen(int i, int i1) {

    }

}
