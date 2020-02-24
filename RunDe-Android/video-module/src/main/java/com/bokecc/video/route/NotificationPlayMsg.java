package com.bokecc.video.route;

public class NotificationPlayMsg {
    public static final int DESTROY = 0;
    public static final int PLAY_PAUSE = 1;
    public static final int NEXT = 2;
    public static final int LAST = 3;


    public int code;

    public NotificationPlayMsg(int code) {
        this.code = code;
    }
}
