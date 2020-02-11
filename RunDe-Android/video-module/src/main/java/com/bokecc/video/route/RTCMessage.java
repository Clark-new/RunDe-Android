package com.bokecc.video.route;

/**
 * 连麦消息
 */
public class RTCMessage extends Message {
    public static final int RTC_STATE_DISABLE = 0;
    public static final int RTC_STATE_ENABLE = 1;
    public static final int RTC_STATE_APPLYING = 2;
    public static final int RTC_STATE_CONNECTED = 3;
    public static final int RTC_STATE_DISCONNECTED= 4;
    public RTCMessage() {
    }
    public RTCMessage(int code) {
        super(code);
    }
    public RTCMessage(int type, int code) {
        super(type, code);
    }
}
