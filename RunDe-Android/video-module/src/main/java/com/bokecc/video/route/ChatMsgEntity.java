package com.bokecc.video.route;

public class ChatMsgEntity extends Message {

    public static final int PUBLIC_CHAT = 1;
    public static final int HISTORY_CHAT = 2;


    public Object extra;

    public ChatMsgEntity(int type, Object extra) {
        super(type, 0);
        this.extra = extra;
    }
}
