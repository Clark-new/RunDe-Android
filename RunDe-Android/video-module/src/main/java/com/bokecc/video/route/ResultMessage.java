package com.bokecc.video.route;

public class ResultMessage extends Message {

    public static final int SUCCESS = 0x01;
    public static final int FAILED = 0x02;

    public Object extra;

    public ResultMessage(int type, String msg, int code) {
        super(type, msg, code);
    }

    public ResultMessage(int type, String msg, int code,Object extra) {
        super(type, msg, code);
        this.extra = extra;
    }

}
