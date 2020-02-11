package com.bokecc.video.msg;

public class BannedChatMsg {

    public static final int PERSION_BANNED = 1;
    public static final int ALL_BANNED = 2;
    public static final int PERSION_UNBANNED = 3;
    public static final int ALL_UNBANNED = 4;

    public int msgType;

    public BannedChatMsg(int type) {
        msgType = type;
    }
}
