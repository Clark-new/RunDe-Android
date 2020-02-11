package com.bokecc.video.route;

/**
 * 切换成其他视频的消息
 * Other Video switch message => OnVideoSwitchMsg
 */
public class OnVideoSwitchMsg extends Message {
    //准备切换视频
    public static final int PREPARE = 0x0001;
    //开始切换视频
    public static final int START = 0x0002;

    public OnVideoSwitchMsg() {
    }

    public OnVideoSwitchMsg(int type, String msg) {
        super(type, msg);
    }
}
