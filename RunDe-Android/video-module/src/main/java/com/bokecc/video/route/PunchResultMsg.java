package com.bokecc.video.route;

/**
 * 打卡结果消息
 */
public class PunchResultMsg {
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    public int code;

    public PunchResultMsg(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
